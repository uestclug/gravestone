package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.block.entity.FakeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkDataS2CPacket.class)
public abstract class MixinChunkDataS2CPacket extends Object {

    // hook block entity packet
    @Redirect(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;toInitialChunkDataTag()Lnet/minecraft/nbt/CompoundTag;", ordinal = 0))
    private CompoundTag upgradeCompoundTag(BlockEntity blockEntity) {

        if (blockEntity instanceof FakeBlockEntity) {
            return ((FakeBlockEntity) blockEntity).toInitialClientChunkDataTag();
        }
        return blockEntity.toInitialChunkDataTag();
    }
}