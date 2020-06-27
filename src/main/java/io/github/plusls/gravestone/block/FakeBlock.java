package io.github.plusls.gravestone.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public abstract class FakeBlock extends Block {
    protected Block clientBlock;
    public FakeBlock(Block clientBlock, Settings settings) {
        super(settings);
        this.clientBlock = clientBlock;
    }

    public BlockState getClientBlockState(BlockState original) {
        // todo
        return clientBlock.getDefaultState();
    }

    public BlockState getDefaultClientBlockState() {
        return clientBlock.getDefaultState();
    }

}
