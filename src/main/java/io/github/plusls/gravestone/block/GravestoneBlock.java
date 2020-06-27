package io.github.plusls.gravestone.block;

import io.github.plusls.gravestone.block.entity.GravestoneBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class GravestoneBlock extends FakeBlock implements BlockEntityProvider {

    private static final VoxelShape SHAPE = createCuboidShape(3, 0, 3, 13, 12, 5);
    public GravestoneBlock(Settings settings) {
        super(Blocks.PLAYER_HEAD, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new GravestoneBlockEntity();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        if (world.isClient()) {
            return;
        }
        if (blockEntity instanceof  GravestoneBlockEntity) {
            GravestoneBlockEntity graveEntity = (GravestoneBlockEntity)blockEntity;
            graveEntity.drop();
        } else {
            throw new RuntimeException("wtf");
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.clientBlock.getOutlineShape(arg, arg2, arg3, arg4);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND) {
            if (world.isClient() == false) {
                ((GravestoneBlockEntity) world.getBlockEntity(pos)).sendDeathInfo(player);
            }
            return ActionResult.SUCCESS;

        } else {
            return ActionResult.PASS;
        }
    }

}
