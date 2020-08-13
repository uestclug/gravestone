package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RegistrySyncManager.class)
public class MixinRegistrySyncManager {
    //                     target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;toTag(ZLnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;",
    // 目前没有同步事件，因此不再mixin
    @ModifyVariable(method = "createPacket",
            at = @At(value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;toTag(ZLnet/minecraft/class_2487;)Lnet/minecraft/class_2487;",
                    ordinal = 0, remap = false),
            ordinal = 0, remap = false)
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
