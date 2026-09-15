package com.herbalistscraft.herb;

import com.herbalistscraft.Config;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.registry.ModHerbs;
import com.herbalistscraft.registry.ModItems;
import com.herbalistscraft.season.Seasons;
import com.herbalistscraft.tool.SeedPouchItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A wild or replanted herb. Four visible stages, seasonal growth, a light preference, and a
 * regrowth roll so harvesting normally leaves the plant alive and productive.
 */
public class HerbCropBlock extends BushBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;
    private static final VoxelShape[] SHAPES = new VoxelShape[] {
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D),
            Block.box(3.0D, 0.0D, 3.0D, 13.0D, 7.0D, 13.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D),
    };

    private final ResourceKey<HerbDefinition> herb;

    public HerbCropBlock(Properties properties, ResourceKey<HerbDefinition> herb) {
        super(properties);
        this.herb = herb;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    public ResourceKey<HerbDefinition> herbKey() {
        return herb;
    }

    public static ResourceKey<HerbDefinition> herbOf(Block block) {
        return block instanceof HerbCropBlock crop ? crop.herbKey() : ModHerbs.BLOODROOT;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        HerbDefinition definition = definition(level);
        return definition == null || state.is(definition.soil().tag());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return mayPlaceOn(level.getBlockState(below), level, below);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) >= MAX_AGE) {
            return;
        }
        HerbDefinition definition = definition(level);
        if (definition == null) {
            return;
        }
        int light = level.getMaxLocalRawBrightness(pos);
        if (!definition.light().accepts(light)) {
            return;
        }
        float chance = growthChance(definition, level, pos);
        if (chance <= 0.0F || random.nextFloat() > chance) {
            return;
        }
        level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), Block.UPDATE_CLIENTS);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.5D,
                2, 0.25D, 0.2D, 0.25D, 0.0D);
    }

    private float growthChance(HerbDefinition definition, ServerLevel level, BlockPos pos) {
        double base = 0.12D * definition.growth().speed() * Config.growthSpeed()
                / definition.rarity().growthPenalty();
        if (Config.seasonalGrowth()) {
            float seasonal = definition.seasonGrowth(Seasons.current(level));
            if (seasonal <= 0.0F) {
                return 0.0F;
            }
            base *= seasonal;
        }
        if (level.isRainingAt(pos.above())) {
            base *= 1.15D;
        }
        return (float) Math.min(0.9D, base);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(AGE) < MAX_AGE) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return harvest(level, pos, state, player)
                ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (state.getValue(AGE) < MAX_AGE) {
            return InteractionResult.PASS;
        }
        return harvest(level, pos, state, player)
                ? InteractionResult.sidedSuccess(level.isClientSide)
                : InteractionResult.PASS;
    }

    private boolean harvest(Level level, BlockPos pos, BlockState state, Player player) {
        HerbDefinition definition = definition(level);
        if (definition == null) {
            return false;
        }
        if (level.isClientSide) {
            return true;
        }
        RandomSource random = level.getRandom();
        boolean shears = player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.PRUNING_SHEARS.get());
        long day = level.getDayTime() / 24000L;
        List<ItemStack> drops = dropsFor(definition, random, shears, player);
        for (ItemStack drop : drops) {
            if (drop.getItem() instanceof HerbItem herbItem && herbItem.form() == HerbForm.FRESH) {
                herbItem.stampHarvest(drop, day);
            }
            Block.popResource(level, pos, drop);
        }
        if (level instanceof ServerLevel serverLevel) {
            Discovery.onHarvest(serverLevel, player, herb, definition, drops);
        }
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.7F, 1.15F);
        boolean survives = shears || (Config.regrowth() && random.nextFloat() < definition.growth().regrowChance());
        if (survives) {
            level.setBlock(pos, state.setValue(AGE, 1), Block.UPDATE_ALL);
        } else {
            level.destroyBlock(pos, false);
        }
        return true;
    }

    private List<ItemStack> dropsFor(HerbDefinition definition, RandomSource random, boolean shears, Player player) {
        List<ItemStack> drops = new ArrayList<>();
        int yield = definition.growth().produce().roll(random);
        ItemStack pouch = SeedPouchItem.findWorn(player);
        if (!pouch.isEmpty()) {
            yield += 1;
        }
        drops.add(new ItemStack(HerbForms.item(herb, HerbForm.FRESH), Math.max(1, yield)));
        if (definition.hasForm(HerbForm.SEED)) {
            int seeds = definition.growth().seeds().roll(random);
            if (shears) {
                seeds += 1;
            }
            if (seeds > 0) {
                drops.add(new ItemStack(HerbForms.seed(herb), seeds));
            }
        }
        return drops;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ServerLevel level = params.getLevel();
        HerbDefinition definition = definition(level);
        if (definition == null || state.getValue(AGE) < MAX_AGE) {
            return List.of();
        }
        RandomSource random = level.getRandom();
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(HerbForms.item(herb, HerbForm.FRESH),
                Math.max(1, definition.growth().produce().roll(random))));
        if (definition.hasForm(HerbForm.SEED) && random.nextFloat() < 0.7F) {
            drops.add(new ItemStack(HerbForms.seed(herb), definition.growth().seeds().roll(random)));
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(HerbForms.seed(herb));
    }

    private HerbDefinition definition(BlockGetter level) {
        if (level instanceof ServerLevel serverLevel) {
            return HerbRegistry.get(serverLevel.registryAccess(), herb).orElse(null);
        }
        if (level instanceof LevelReader reader) {
            return HerbRegistry.get(reader.registryAccess(), herb).orElse(null);
        }
        return null;
    }
}
