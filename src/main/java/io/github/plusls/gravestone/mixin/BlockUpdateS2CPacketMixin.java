package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.minecraft.block.*;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;

@Mixin(BlockUpdateS2CPacket.class)
public abstract class BlockUpdateS2CPacketMixin extends Object {
    @Shadow
    private BlockState state;

    // hook BlockState packet
    @Inject(method = "<init>(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)V", at = @At("RETURN"))
    private void onInit(BlockView blockView, BlockPos pos, CallbackInfo info){
        Block block = this.state.getBlock();
        if (block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("BlockUpdateS2CPacket hook BlockState packet");
            this.state = ((FakeBlock) block).getClientBlockState(this.state);
        }
    }
}