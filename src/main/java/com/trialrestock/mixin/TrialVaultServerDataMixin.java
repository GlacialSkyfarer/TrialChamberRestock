package com.trialrestock.mixin;

import com.trialrestock.TrialVaultServerDataAccess;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.trialrestock.TrialRestock.CONFIG;

@Mixin(VaultServerData.class)
public class TrialVaultServerDataMixin implements TrialVaultServerDataAccess {

    @Unique
    public Object2LongArrayMap<UUID> trialrestock$playerCooldowns = new Object2LongArrayMap<UUID>();

    @Unique
    public Object2IntArrayMap<UUID> trialrestock$playerCosts = new Object2IntArrayMap<UUID>();

    public Object2IntArrayMap<UUID> trialrestock$getPlayerCosts() {

        return trialrestock$playerCosts;

    }
    public Object2LongArrayMap<UUID> trialrestock$getPlayerCooldowns() {

        return trialrestock$playerCooldowns;

    }
    public void trialrestock$setPlayerCooldowns(Object2LongArrayMap<UUID> value) {

        trialrestock$playerCooldowns = value;

    }

    @Redirect(method = "markPlayerAsRewarded", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"))
    public <E> boolean injected (Set instance, E e) {

        return false;

    }
    @Inject(method = "markPlayerAsRewarded", at = @At(value = "RETURN"))
    public void injected(PlayerEntity player, CallbackInfo ci) {

        if (trialrestock$playerCosts.containsKey(player.getUuid())) {

            trialrestock$playerCosts.replace(player.getUuid(), Math.min(trialrestock$playerCosts.getInt(player.getUuid()) + CONFIG.costIncrease(), 64));

        } else {

            trialrestock$playerCosts.putIfAbsent(player.getUuid(), 2);

        }

        World w = player.getWorld();

        trialrestock$playerCooldowns.put(player.getUuid(), w.getTime() + CONFIG.restockDelay());

    }

}