package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.BiMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(BiMapPalette.class)
public abstract class BiMapPaletteMixin extends Object {
    // hook BlockState packet
    // ChunkDataS2CPacket->ChunkSection->PalettedContainer<BlockState>->BiMapPalette<BlockState>
    @ModifyArg(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeVarInt(I)Lnet/minecraft/network/PacketByteBuf;", ordinal=1), index=0)
    private int modifyId(int original) {
        BlockState state = Block.getStateFromRawId(original);
        Block block = state.getBlock();
        if(block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("BiMapPalette hook BlockState packet");
            return Block.getRawIdFromState(((FakeBlock)block).getClientBlockState(state));
        }
        return original;
    }
}