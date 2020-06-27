package io.github.plusls.gravestone;

import com.google.common.collect.ImmutableSet;
import io.github.plusls.gravestone.block.GravestoneBlock;
import io.github.plusls.gravestone.block.entity.GravestoneBlockEntity;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravestoneMod implements ModInitializer, DedicatedServerModInitializer {


	public static final String MODID = "gravestone_mod";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final Block GRAVESTONE_BLOCK = new GravestoneBlock(Block.Settings.of(Material.PISTON).strength(0.5f, 1200));
	public static final BlockEntityType<GravestoneBlockEntity> GRAVESTONE_BLOCK_ENTITY_TYPE = new BlockEntityType<GravestoneBlockEntity>(GravestoneBlockEntity::new, ImmutableSet.of((Block) GRAVESTONE_BLOCK), null);

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, id("gravestone_block"), GRAVESTONE_BLOCK);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("gravestone_block"), GRAVESTONE_BLOCK_ENTITY_TYPE);

	}

	@Override
	public void onInitializeServer() {


	}
	public static Identifier id(String id) {
		return new Identifier(MODID, id);
	}
}
