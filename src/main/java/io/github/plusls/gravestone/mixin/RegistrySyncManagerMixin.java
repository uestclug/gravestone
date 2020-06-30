package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import io.github.plusls.gravestone.util.GravestoneUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;

@Mixin(RegistrySyncManager.class)
public class RegistrySyncManagerMixin {
//    @Inject(method = "createPacket", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;toTag(ZLnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;"))
//    private static void onCreatePacket(CallbackInfoReturnable callbackInfoReturnable) {
//
//    }
    @ModifyVariable(method = "createPacket",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;toTag",
                    ordinal=0, remap=false),
            ordinal=0, remap=false)
    private static CompoundTag updateCompoundTag(CompoundTag tag) {
        GravestoneMod.LOGGER.info("Try to update Registry.BLOCK sync data.");

        if (tag == null) {
            return null;
        }
        CompoundTag mainTag = tag.getCompound("registries");
        CompoundTag blockRegistryTag = mainTag.getCompound("minecraft:block");
        if (blockRegistryTag == null) {
            for (String key : blockRegistryTag.getKeys()) {
                Block block = Registry.BLOCK.get(blockRegistryTag.getInt(key));
                if (block instanceof FakeBlock) {
                    blockRegistryTag.remove(key);
                    GravestoneMod.LOGGER.info("Remove Registry.BLOCK:" + key);
                }
            }
        }
        return tag;
    }
}
