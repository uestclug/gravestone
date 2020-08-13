package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public abstract class MixinChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener> {

    // hook BlockState packet
    @Redirect(method = "<init>(Lnet/minecraft/util/math/ChunkSectionPos;Lit/unimi/dsi/fastutil/shorts/ShortSet;Lnet/minecraft/world/chunk/ChunkSection;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ChunkSection;getBlockState(III)Lnet/minecraft/block/BlockState;",
                    ordinal = 0))
    private BlockState redirectGetBlockState(ChunkSection section, int x, int y, int z) {
        BlockState blockState = section.getBlockState(x, y, z);
        Block block = blockState.getBlock();
        if (block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("ChunkDeltaUpdateS2CPacket hook BlockState packet");
            blockState = ((FakeBlock) block).getClientBlockState(blockState);
        }
        return blockState;
    }
}