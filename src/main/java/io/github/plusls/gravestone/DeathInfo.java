package io.github.plusls.gravestone;

import com.mojang.authlib.GameProfile;
import io.github.plusls.gravestone.util.GravestoneUtil;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DeathInfo {
    public final GameProfile owner;
    public final Text deathMessage;
    public final long deathTime;
    public final int xp;
    public final List<ItemStack> inventory;

    public DeathInfo(GameProfile owner, Text deathMessage, long deathTime, int xp, List<ItemStack> inv) {
        this.owner = owner;
        this.deathMessage = deathMessage;
        this.deathTime = deathTime;
        this.xp = xp;
        this.inventory = inv;
    }

    public CompoundTag toTag(CompoundTag tag) {

        tag.put("Owner", NbtHelper.fromGameProfile(new CompoundTag(), this.owner));
        tag.putString("DeathMessage", Text.Serializer.toJson(this.deathMessage));
        tag.putLong("DeathTime", this.deathTime);
        tag.putInt("XP", this.xp);

        ListTag list = new ListTag();
        for(ItemStack s : this.inventory) {
            if(!s.isEmpty()) {
                list.add(s.toTag(new CompoundTag()));
            }
        }
        tag.put("Items", list);
        return tag;
    }

    public static DeathInfo fromTag(CompoundTag tag) {
        GameProfile owner = NbtHelper.toGameProfile((CompoundTag) tag.get("Owner"));
        Text deathMessage = Text.Serializer.fromJson(tag.getString("DeathMessage"));
        long deathTime = tag.getLong("DeathTime");
        int xp = tag.getInt("XP");

        List<ItemStack> inventory = new ArrayList<>();
        ListTag stacks = (ListTag)tag.get("Items");
        if(stacks != null) {
            for(Tag t : stacks) {
                inventory.add(ItemStack.fromTag((CompoundTag)t));
            }
        }

        return new DeathInfo(owner, deathMessage, deathTime, xp, inventory);
    }

    public void sendDeathInfo(PlayerEntity player) {
        player.sendMessage(new LiteralText(this.getDeathInfo()), false);
    }
    public String getDeathInfo() {
        ListTag list = new ListTag();
        for(ItemStack s : this.inventory) {
            if(!s.isEmpty()) {
                list.add(s.toTag(new CompoundTag()));
            }
        }
        return String.format("Owner: %s\nDeathMessage: %s\nDeathTime: %s\n Items: %s",
                this.owner.getName(), this.deathMessage.getString(),
                GravestoneUtil.timeToString(this.deathTime), list.toText().getString());
    }
}
