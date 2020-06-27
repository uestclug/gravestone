package io.github.plusls.gravestone.util;

import io.github.plusls.gravestone.DeathInfo;
import io.github.plusls.gravestone.block.entity.GravestoneBlockEntity;
import io.github.plusls.gravestone.mixin.invoker.PlayerEntityInvoker;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static io.github.plusls.gravestone.GravestoneMod.*;

public class GravestoneUtil {
    public static final int BLOCK_MIN_Y = 1;
    public static final int NETHER_BEDROCK_MAX_Y = 127;
    public static final int SEARCH_RANGE = 5;

    public static void deathHandle(ServerPlayerEntity player, Text deathMessage) {
        World world = player.world;
        if (!world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            List<ItemStack> items = new ArrayList<>();
            // int xp = ((PlayerEntityInvoker) player).callGetCurrentExperience(null);
            int xp = player.totalExperience / 2;

            ((PlayerEntityInvoker) player).callVanishCursedItems();
            items.addAll(player.inventory.main);
            items.addAll(player.inventory.armor);
            items.addAll(player.inventory.offHand);
            player.inventory.clear();

            // only need clear experienceLevel
            player.experienceLevel = 0;
            BlockPos gravePos = findGravePos(player);
            world.getServer().send(new ServerTask(world.getServer().getTicks(),
                    placeGraveRunnable(world, gravePos,
                            new DeathInfo(player.getGameProfile(), deathMessage, System.currentTimeMillis(), xp, items))));

        }
    }

    // find pos to place gravestone
    public static BlockPos findGravePos(ServerPlayerEntity player) {
        BlockPos.Mutable playerPos = new BlockPos((player.getPos())).mutableCopy();
        playerPos.setY(clampY(player, playerPos.getY()));
        if (canPlaceGrave(player, playerPos)) {
            return playerPos;
        }
        BlockPos.Mutable gravePos = new BlockPos.Mutable();
        for (int x = playerPos.getX() + SEARCH_RANGE; x >= playerPos.getX() - SEARCH_RANGE; x--) {
            gravePos.setX(x);
            int minY = clampY(player, playerPos.getY() - SEARCH_RANGE);
            for (int y = clampY(player, playerPos.getY() + SEARCH_RANGE); y >= minY; y--) {
                gravePos.setY(y);
                for (int z = playerPos.getZ() + SEARCH_RANGE; z >= playerPos.getZ() - SEARCH_RANGE; z--) {
                    gravePos.setZ(z);
                    if (canPlaceGrave(player, gravePos)) {
                        return drop(player, gravePos);
                    }
                }
            }
        }

        // search up
        gravePos.set(playerPos);
        while (player.world.getBlockState(gravePos).getBlock() == Blocks.BEDROCK) {
            gravePos.setY(gravePos.getY() + 1);
        }
        return gravePos;
    }

    // make sure to spawn graves on the suitable place
    public static int clampY(ServerPlayerEntity player, int y) {
        //don't spawn on nether ceiling, unless the player is already there.
        if (player.world.getRegistryKey() == World.NETHER && y < NETHER_BEDROCK_MAX_Y) {
            //clamp to 1 -- don't spawn graves the layer right above the void, so players can actually recover their items.
            return MathHelper.clamp(y, BLOCK_MIN_Y + 1, NETHER_BEDROCK_MAX_Y - 1);
        } else {
            return MathHelper.clamp(y, BLOCK_MIN_Y + 1, player.server.getWorldHeight() - 1);
        }
    }


    public static boolean canPlaceGrave(ServerPlayerEntity player, BlockPos pos) {

        BlockState state = player.world.getBlockState(pos);
        //try {
        if (pos.getY() <= BLOCK_MIN_Y || pos.getY() >= player.server.getWorldHeight())
            return false;
        else if (state.isAir())
            return true;
        // block can replace
        else if (state.canReplace(
                new AutomaticItemPlacementContext(player.world, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)))
            return true;
        // replace uncollidable block; 可能会覆盖红石电路
        // else if (!((AbstractBlockAccessor) state.getBlock()).getCollidable())
        //     return true;
        else
            return false;

//        } catch (NullPointerException e) {
//            return false;
//        }
    }

    // players are blown up
    // reduce y pos
    public static BlockPos drop(ServerPlayerEntity player, BlockPos pos) {
        BlockPos.Mutable searchPos = new BlockPos.Mutable().set(pos);
        int i = 0;
        for (int y = pos.getY() - 1; y > BLOCK_MIN_Y && i < 10; y--) {
            i++;
            searchPos.setY(clampY(player, y));
            if (!player.world.getBlockState(searchPos).isAir()) {
                searchPos.setY(clampY(player, y + 1));
                return searchPos;
            }
        }
        return pos;
    }

    public static Runnable placeGraveRunnable(World world, BlockPos pos, DeathInfo deathInfo) {
        return () -> {
            BlockState graveBlock = GRAVESTONE_BLOCK.getDefaultState();
            world.setBlockState(pos, graveBlock);
            GravestoneBlockEntity graveEntity = (GravestoneBlockEntity)world.getBlockEntity(pos);
            graveEntity.setDeathInfo(deathInfo);
            graveEntity.markDirty();

        };
    }
    public static String timeToString(long time) {
        if (time == 0L)
            return "";
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(c.getTime());
    }
}
