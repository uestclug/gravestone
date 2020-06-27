package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerActionResponseS2CPacket.class)
public abstract class PlayerActionResponseS2CPacketMixin extends Object {

    @Shadow
    private BlockState state;

    // hook BlockState packet
    @Inject(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;ZLjava/lang/String;)V", at = @At(value = "RETURN"))
    private void upgradeState(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved, String reason, CallbackInfo info) {
        Block block = this.state.getBlock();
        if (block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("PlayerActionResponseS2CPacket hook BlockState packet");
            this.state = ((FakeBlock) block).getClientBlockState(this.state);
        }
    }

}