package com.trialrestock.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.trialrestock.TrialVaultServerDataAccess;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Uuids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.JsonElement;
import org.spongepowered.include.com.google.gson.stream.JsonWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.net.Proxy;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.lang.System.out;

@Mixin(VaultBlockEntity.class)
public class VaultBlockEntityMixin {

    @Shadow @Final private VaultServerData serverData;

    @Inject(method = "writeNbt", at = @At("RETURN"))
    void injectWriteNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {

        TrialVaultServerDataAccess serverDataAccess = (TrialVaultServerDataAccess)serverData;

        NbtCompound cooldowns = new NbtCompound();
        for (UUID key : serverDataAccess.trialrestock$getPlayerCooldowns().keySet()) {

            cooldowns.putLong(key.toString(), serverDataAccess.trialrestock$getPlayerCooldowns().getLong(key));

        }

        nbt.put("trialrestock$playerCooldowns", cooldowns);
        NbtCompound costs = new NbtCompound();
        for (UUID key : serverDataAccess.trialrestock$getPlayerCosts().keySet()) {

            costs.putLong(key.toString(), serverDataAccess.trialrestock$getPlayerCosts().getInt(key));

        }

        nbt.put("trialrestock$playerCosts", costs);

    }
    @Inject(method = "readNbt", at = @At("RETURN"))
    void injectReadNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {

        TrialVaultServerDataAccess serverDataAccess = (TrialVaultServerDataAccess)serverData;

        if (nbt.contains("trialrestock$playerCooldowns")) {

            NbtCompound cooldowns = nbt.getCompound("trialrestock$playerCooldowns");

            Object2LongArrayMap<UUID> cds = serverDataAccess.trialrestock$getPlayerCooldowns();

            for (String s : cooldowns.getKeys()) {

                cds.put(UUID.fromString(s), cooldowns.getLong(s));

            }

            serverDataAccess.trialrestock$setPlayerCooldowns(cds);

        }

        if (nbt.contains("trialrestock$playerCosts")) {

            NbtCompound costs = nbt.getCompound("trialrestock$playerCosts");

            Object2IntArrayMap<UUID> csts = serverDataAccess.trialrestock$getPlayerCosts();

            for (String s : costs.getKeys()) {

                csts.put(UUID.fromString(s), costs.getInt(s));

            }

            serverDataAccess.trialrestock$setPlayerCosts(csts);

        }

    }

}
