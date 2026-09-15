package com.herbalistscraft.world;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbCropBlock;
import com.herbalistscraft.herb.HerbDefinition;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.herb.HerbRarity;
import com.herbalistscraft.season.Seasons;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Plants a small patch of one herb. Placement filters live in the placed-feature JSON, so this
 * class only ever runs where the biome, height and soil rules already said yes.
 */
public class HerbPatchFeature extends Feature<HerbPatchConfiguration> {
    public HerbPatchFeature() {
        super(HerbPatchConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<HerbPatchConfiguration> context) {
        if (!Config.spawnWildHerbs()) {
            return false;
        }
        WorldGenLevel level = context.level();
        HerbPatchConfiguration config = context.config();
        ResourceLocation cropId = ResourceLocation.tryParse(config.herb());
        if (cropId == null) {
            return false;
        }
        Block block = level.registryAccess().registryOrThrow(Registries.BLOCK).get(cropId);
        if (!(block instanceof HerbCropBlock crop)) {
            return false;
        }
        HerbDefinition definition = HerbRegistry.get(level.registryAccess(), crop.herbKey()).orElse(null);
        if (definition == null) {
            return false;
        }
        double chance = Config.herbSpawnRate();
        if (definition.rarity() != HerbRarity.COMMON) {
            chance *= Config.rareHerbSpawnRate();
        }
        if (Config.seasonsAffectWorldgen() && Config.seasonalGrowth()) {
            float seasonal = definition.seasonGrowth(Seasons.current(level.getLevel()));
            if (seasonal <= 0.0F) {
                return false;
            }
            chance *= Math.min(1.5F, seasonal);
        }
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        boolean placed = false;
        for (int attempt = 0; attempt < config.tries(); attempt++) {
            BlockPos pos = origin.offset(random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread(),
                    random.nextInt(config.ySpread() * 2 + 1) - config.ySpread(),
                    random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread());
            for (int depth = 0; depth < 4; depth++) {
                BlockPos candidate = pos.below(depth);
                if (!level.getBlockState(candidate).isAir()) {
                    continue;
                }
                BlockState soil = level.getBlockState(candidate.below());
                if (!soil.is(definition.soil().tag()) || soil.is(Blocks.WATER)) {
                    continue;
                }
                int light = level.getMaxLocalRawBrightness(candidate);
                if (!definition.light().accepts(light)) {
                    continue;
                }
                if (random.nextDouble() > chance) {
                    continue;
                }
                BlockState state = block.defaultBlockState();
                int age = random.nextInt(2) + 1;
                state = state.setValue(HerbCropBlock.AGE, Math.min(age, HerbCropBlock.MAX_AGE));
                level.setBlock(candidate, state, Block.UPDATE_CLIENTS);
                placed = true;
                break;
            }
        }
        return placed;
    }

    /** Helper for the biome-group lookup used by discovery. */
    public static Holder<net.minecraft.world.level.biome.Biome> biomeAt(WorldGenLevel level, BlockPos pos) {
        return level.getBiome(pos);
    }
}
