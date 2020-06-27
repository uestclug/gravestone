package io.github.plusls.gravestone.block.entity;


import io.github.plusls.gravestone.DeathInfo;
import io.github.plusls.gravestone.GravestoneMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;


public class GravestoneBlockEntity extends FakeBlockEntity {

    private DeathInfo deathInfo;
    private static final int SKULL_BLOCK_ENTITY_TYPE_ID = 4;

    public GravestoneBlockEntity() {
        super(BlockEntityType.SKULL, GravestoneMod.GRAVESTONE_BLOCK_ENTITY_TYPE);

    }

    public void setDeathInfo(DeathInfo deathInfo) {
        this.deathInfo = deathInfo;
    }


    public void drop() {
        // Drop item
        for(ItemStack s : this.deathInfo.inventory) {
            Block.dropStack(this.world, this.pos, s);
        }

        // Drop xp
        int xp = this.deathInfo.xp;
        while (xp > 0) {
            int spawnedXp = ExperienceOrbEntity.roundToOrbSize(xp);
            xp -= spawnedXp;
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), spawnedXp));
        }
    }


    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.deathInfo = DeathInfo.fromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        this.deathInfo.toTag(tag);
        return tag;
    }

    @Override
    public CompoundTag toInitialClientChunkDataTag() {
        CompoundTag tag = super.toInitialChunkDataTag();
        CompoundTag compoundTag = new CompoundTag();
        NbtHelper.fromGameProfile(compoundTag, this.deathInfo.owner);
        tag.put("SkullOwner", compoundTag);
        tag.putString("id", BlockEntityType.getId(this.clientBlockEntityType).toString());
        return tag;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, SKULL_BLOCK_ENTITY_TYPE_ID, this.toInitialClientChunkDataTag());
    }

    public void sendDeathInfo(PlayerEntity player) {
        this.deathInfo.sendDeathInfo(player);
    }
}
