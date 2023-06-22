package com.yanisbft.mooblooms.entity;

import com.yanisbft.mooblooms.config.MoobloomConfigCategory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;

public interface AnimalWithBlockState {

	public static boolean canSpawn(EntityType<? extends AnimalWithBlockState> type, WorldAccess world,
			SpawnReason spawnReason,
			BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.DIRT) && world.getBaseLightLevel(pos, 0) > 8;
	}

	public static boolean canSpawnBadlands(EntityType<? extends AnimalWithBlockState> type, WorldAccess world,
			SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isOf(Blocks.RED_SAND) && world.getBaseLightLevel(pos, 0) > 8;
	}

	public static boolean canSpawnNether(EntityType<? extends AnimalWithBlockState> type, WorldAccess world,
			SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
	}

	public static boolean canSpawnEnd(EntityType<? extends AnimalWithBlockState> type, WorldAccess world,
			SpawnReason spawnReason, BlockPos pos, Random random) {
		int height = world.getChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE, pos.getX() & 15, pos.getY() & 15);
		return height > 0 && pos.getY() >= height;
	}

	public default void placeBlocks(AnimalEntity entity, BlockState state) {
		if (state.getBlock() instanceof TallPlantBlock) {
			entity.getWorld().setBlockState(entity.getBlockPos(), state.cycle(Properties.DOUBLE_BLOCK_HALF));
			entity.getWorld().setBlockState(entity.getBlockPos().up(), state);
		} else {
			entity.getWorld().setBlockState(entity.getBlockPos(), state);
		}
	}

	public default boolean canSpawnBlocks(MoobloomConfigCategory configCategory) {
		boolean enabled = true;

		if (configCategory != null) {
			try {
				enabled = configCategory.getClass().getDeclaredField("spawnBlocks").getBoolean(configCategory);
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}

		return enabled;
	}
}
