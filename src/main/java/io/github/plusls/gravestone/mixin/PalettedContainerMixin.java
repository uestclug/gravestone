package io.github.plusls.gravestone.mixin;

import io.github.plusls.gravestone.GravestoneMod;
import io.github.plusls.gravestone.block.FakeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PalettedContainer.class)
public abstract class PalettedContainerMixin<T> extends Object {
    // ChunkDataS2CPacket->ChunkSection->PalettedContainer<BlockState>

    // clientData
    private PackedIntegerArray clientData = null;

    @Shadow
    private int paletteSize;

    @Shadow
    private Palette<T> palette;


    private void redirectSet(PackedIntegerArray data, int index, int value) {
        BlockState state = (BlockState)this.palette.getByIndex(value);
        Block block = state.getBlock();
        int newValue;
        if(block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("Fix redirectSet");
            newValue = this.palette.getIndex((T)((FakeBlock)block).getClientBlockState(state));
            this.clientData.set(index, newValue);
        } else {
            this.clientData.set(index, value);
        }
        data.set(index, value);
    }

    @Inject(method = "setPaletteSize", at = @At(value = "RETURN"))
    private void afterSetPaletteSize(int size, CallbackInfo info) {
        this.clientData = new PackedIntegerArray(this.paletteSize, 4096);
    }

    // redirect this.data.set
    @Redirect(method="setAndGetOldValue",
            at=@At(value = "INVOKE", target="Lnet/minecraft/util/collection/PackedIntegerArray;setAndGetOldValue(II)I", ordinal=0))
    private int setAndGetOldValueSet(PackedIntegerArray data, int index, int value) {
        BlockState state = (BlockState)this.palette.getByIndex(value);
        Block block = state.getBlock();
        int newValue;
        if(block instanceof FakeBlock) {
            GravestoneMod.LOGGER.info("Fix setAndGetOldValueSet");
            newValue = this.palette.getIndex((T)((FakeBlock)block).getClientBlockState(state));
            this.clientData.setAndGetOldValue(index, newValue);
        } else {
            this.clientData.setAndGetOldValue(index, value);
        }
        return data.setAndGetOldValue(index, value);
    }
    @Redirect(method="set(ILjava/lang/Object;)V",
            at=@At(value = "INVOKE", target="Lnet/minecraft/util/collection/PackedIntegerArray;set(II)V", ordinal=0))
    private void redirectSetSet(PackedIntegerArray data, int index, int value) {
        redirectSet(data, index, value);
    }
    @Redirect(method="read",
            at=@At(value = "INVOKE", target="Lnet/minecraft/util/collection/PackedIntegerArray;set(II)V", ordinal=0))
    private void redirectReadSet0(PackedIntegerArray data, int index, int value) {
        redirectSet(data, index, value);
    }
    @Redirect(method="read",
            at=@At(value = "INVOKE", target="Lnet/minecraft/util/collection/PackedIntegerArray;set(II)V", ordinal=1))
    private void redirectReadSet1(PackedIntegerArray data, int index, int value) {
        redirectSet(data, index, value);
    }
    @Inject(method="read",
            at=@At(value = "INVOKE", target="Ljava/lang/System;arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V", ordinal=0))
    private void redirectReadSetCopy(ListTag paletteTag, long[] data, CallbackInfo info) {
        System.arraycopy(data, 0, this.clientData.getStorage(), 0, data.length);
        for (int i = 0; i < 4096; ++i) {
            BlockState state = (BlockState)this.palette.getByIndex(this.clientData.get(i));
            if (state == null) {
                GravestoneMod.LOGGER.info("redirectReadSetCopy null, wtf?");
                continue;
            }
            Block block = state.getBlock();
            if (block instanceof FakeBlock) {
                GravestoneMod.LOGGER.info("Fix redirectReadSetCopy");
                this.clientData.set(i, this.palette.getIndex((T)((FakeBlock)block).getClientBlockState(state)));
            }
        }
    }

    // hook BlockState packet
    @Redirect(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/PackedIntegerArray;getStorage()[J", ordinal=0))
    private long[]  redirectGetStorage(PackedIntegerArray data) {
        if (this.palette instanceof IdListPalette) {
            GravestoneMod.LOGGER.info("IdListPalette toPacket");
        }
        return this.clientData.getStorage();
    }
}
