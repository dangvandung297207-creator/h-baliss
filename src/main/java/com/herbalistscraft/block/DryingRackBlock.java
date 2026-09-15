package com.herbalistscraft.block;

import com.herbalistscraft.block.entity.DryingRackBlockEntity;
import com.herbalistscraft.registry.ModBlockEntities;
import com.herbalistscraft.registry.ModSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Drying Rack: hang a fresh herb, wait, take a dried one that keeps its strength for good.
 * The stored herb is drawn on the rack by the block entity renderer, so you can see the work in progress.
 */
public class DryingRackBlock extends BaseEntityBlock {
    public static final MapCodec<DryingRackBlock> CODEC = simpleCodec(DryingRackBlock::new);
    private static final VoxelShape POST_A = Block.box(1.0D, 0.0D, 1.0D, 3.0D, 14.0D, 3.0D);
    private static final VoxelShape POST_B = Block.box(13.0D, 0.0D, 1.0D, 15.0D, 14.0D, 3.0D);
    private static final VoxelShape POST_C = Block.box(1.0D, 0.0D, 13.0D, 3.0D, 14.0D, 15.0D);
    private static final VoxelShape POST_D = Block.box(13.0D, 0.0D, 13.0D, 15.0D, 14.0D, 15.0D);
    private static final VoxelShape TOP = Block.box(0.0D, 11.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    private static final VoxelShape SHELF = Block.box(2.0D, 5.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    private static final VoxelShape SHAPE = Shapes.or(POST_A, POST_B, POST_C, POST_D, TOP, SHELF);

    public DryingRackBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DryingRackBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DryingRackBlockEntity rack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.sidedSuccess(true);
        }
        if (rack.isEmpty()) {
            if (stack.isEmpty()) {
                return ItemInteractionResult.FAIL;
            }
            ItemStack single = stack.copyWithCount(1);
            if (!rack.startDrying(single)) {
                player.displayClientMessage(Component.translatable("message.herbalistscraft.rack.full")
                        .withStyle(ChatFormatting.GRAY), true);
                return ItemInteractionResult.FAIL;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, pos, ModSounds.DRY.get(), SoundSource.BLOCKS, 0.7F, 1.0F);
            return ItemInteractionResult.sidedSuccess(false);
        }
        if (!rack.isDone()) {
            player.displayClientMessage(Component.translatable("message.herbalistscraft.rack.not_dry")
                    .withStyle(ChatFormatting.GRAY), true);
            return ItemInteractionResult.FAIL;
        }
        ItemStack dried = rack.take();
        if (!player.getInventory().add(dried)) {
            Block.popResource(level, pos.above(), dried);
        }
        level.playSound(null, pos, ModSounds.DRY.get(), SoundSource.BLOCKS, 0.7F, 1.3F);
        player.displayClientMessage(Component.translatable("message.herbalistscraft.rack.collected")
                .withStyle(ChatFormatting.GREEN), true);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            com.herbalistscraft.knowledge.Discovery.onDry(serverPlayer);
        }
        return ItemInteractionResult.sidedSuccess(false);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ServerLevel level = params.getLevel();
        if (level.getBlockEntity(params.getOptionalParameter(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN) instanceof DryingRackBlockEntity rack
                && !rack.isEmpty()) {
            return List.of(new ItemStack(this.asItem()), rack.stored());
        }
        return List.of(new ItemStack(this.asItem()));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof DryingRackBlockEntity rack) {
            Containers.dropContents(level, pos, rack);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.DRYING_RACK.get(),
                (tickLevel, pos, tickState, entity) -> entity.serverTick(tickLevel, pos, tickState));
    }
}
