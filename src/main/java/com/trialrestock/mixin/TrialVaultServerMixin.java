package com.trialrestock.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.trialrestock.TrialVaultServerDataAccess;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import joptsimple.util.KeyValuePair;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(VaultBlockEntity.Server.class)
public class TrialVaultServerMixin {

    @Shadow
    private static boolean isValidKey(VaultConfig config, ItemStack stack) {return ItemStack.areItemsAndComponentsEqual(stack, config.keyItem()) && stack.getCount() >= config.keyItem().getCount();};
    @Shadow
    private static void playFailedUnlockSound(ServerWorld world, VaultServerData serverData, BlockPos pos) {

        if (world.getTime() >= ((TrialVaultServerDataAccessor)serverData).trialrestock$getLastFailedUnlockTime() + 15L) {
            world.playSound(null, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM_FAIL, SoundCategory.BLOCKS);
            ((TrialVaultServerDataAccessor)serverData).trialrestock$setLastFailedUnlockTime(world.getTime());
        }

    };

    @Inject(method = "tryUnlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private static void injected(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, PlayerEntity player, ItemStack stack, CallbackInfo ci) {

        stack.decrement(((TrialVaultServerDataAccess)serverData).trialrestock$getPlayerCosts().getOrDefault(player.getUuid(), 1) - 1);

    }

    @Inject(method = "tryUnlock", at = @At(value = "HEAD"), cancellable = true)
    private static void injected2(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, PlayerEntity player, ItemStack stack, CallbackInfo ci) {

        if (stack.getCount() < ((TrialVaultServerDataAccess)serverData).trialrestock$getPlayerCosts().getOrDefault(player.getUuid(),1)) {
            playFailedUnlockSound(world, serverData, pos);
            player.sendMessage(Text.of("You need " + ((TrialVaultServerDataAccess)serverData).trialrestock$getPlayerCosts().getOrDefault(player.getUuid(),1) + " keys to open this vault!"), true);
            ci.cancel();
        }
        if (((TrialVaultServerDataAccess)serverData).trialrestock$getPlayerCooldowns().containsKey(player.getUuid())) {
            playFailedUnlockSound(world, serverData, pos);
            ci.cancel();
        }

    }

    @Inject(method = "tick", at=@At(value = "HEAD"))
    private static void tickInject(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, CallbackInfo ci) {

        Object2LongArrayMap<UUID> playerCooldowns = ((TrialVaultServerDataAccess)serverData).trialrestock$getPlayerCooldowns();
        for (UUID uid : playerCooldowns.keySet()) {

            if (world.getTime() > playerCooldowns.getLong(uid)) {

                playerCooldowns.removeLong(uid);

            }

        }

        ((TrialVaultServerDataAccess)serverData).trialrestock$setPlayerCooldowns(playerCooldowns);

    }

    @ModifyExpressionValue(method = "tryUnlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/vault/VaultServerData;hasRewardedPlayer(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static boolean removeRewardedCheck(boolean original) {

        return false;

    }

}