package io.github.plusls.gravestone.mixin.invoker;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerEntity.class)
public interface PlayerEntityInvoker {
    @Invoker
    void callVanishCursedItems();
    @Invoker
    int callGetCurrentExperience(PlayerEntity arg);
}
