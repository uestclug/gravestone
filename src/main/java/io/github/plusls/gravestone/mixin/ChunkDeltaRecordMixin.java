package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord.class)
public abstract class ChunkDeltaRecordMixin extends Object {

    @Shadow
    private BlockState state;

    // hook BlockState packet
    @Inject(method = "<init>(Lnet/minecraft/network/packet/s2c/play/ChunkDeltaUpdateS2CPacket;SLnet/minecraft/world/chunk/WorldChunk;)V", at = @At(value = "RETURN"))
    private void upgradeState(ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket, short pos, WorldChunk worldChunk, CallbackInfo info) {
        Block block = this.state.getBlock();
        if (block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("ChunkDeltaRecord hook BlockState packet");
            this.state = ((FakeBlock) block).getClientBlockState(this.state);
        }
    }

}