package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.util.GravestoneUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

    private MixinServerPlayerEntity(World world) {
        super(null, null, 0, null);
    }

    // hook death
    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerPlayerEntity.drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onOnDeath(DamageSource damageSource, CallbackInfo info) {
        GravestoneUtil.deathHandle((ServerPlayerEntity) (PlayerEntity) this, getDamageTracker().getDeathMessage());
    }

}
