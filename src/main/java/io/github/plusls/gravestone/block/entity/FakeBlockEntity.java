package io.github.plusls.gravestone.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;


public abstract class FakeBlockEntity extends BlockEntity {
    protected BlockEntityType<?> clientBlockEntityType;
    public FakeBlockEntity(BlockEntityType<?> clientBlockEntityType, BlockEntityType<?> type) {
        super(type);
        this.clientBlockEntityType = clientBlockEntityType;
    }

    public CompoundTag toInitialClientChunkDataTag() {
        return this.toInitialChunkDataTag();
    }
}