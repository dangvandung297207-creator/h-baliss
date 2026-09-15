package com.herbalistscraft.block;

import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.recipe.MortarRecipe;
import com.herbalistscraft.registry.ModParticles;
import com.herbalistscraft.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Mortar and Pestle. Right-click with a herb to grind it where it stands - no crafting
 * table, no GUI, just a bowl, a stone and a satisfying noise.
 */
public class MortarBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);

    public MortarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
                                  CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        MortarRecipe recipe = MortarRecipe.find(level, stack);
        if (recipe == null || !recipe.matches(new SingleRecipeInput(stack), level)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("gui.herbalistscraft.mill.no_recipe")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            return ItemInteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.sidedSuccess(true);
        }
        ItemStack result = recipe.assemble(new SingleRecipeInput(stack), level.registryAccess());
        ItemStack ingredient = stack.copyWithCount(1);
        level.playSound(null, pos, ModSounds.GRIND.get(), SoundSource.BLOCKS, 0.9F, 1.0F);
        Block.popResource(level, pos.above(), result);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.HERBAL_SPARK.get(), pos.getX() + 0.5D, pos.getY() + 0.7D,
                    pos.getZ() + 0.5D, 6, 0.25D, 0.15D, 0.25D, 0.01D);
            if (player instanceof ServerPlayer serverPlayer) {
                Discovery.onGround(serverPlayer, ingredient);
                Discovery.onCraft(serverPlayer);
            }
        }
        player.swing(hand, true);
        player.getCooldowns().addCooldown(stack.getItem(), 8);
        return ItemInteractionResult.sidedSuccess(false);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            level.playSound(null, pos, ModSounds.GRIND.get(), SoundSource.BLOCKS, 0.4F, 0.7F);
        }
        super.attack(state, level, pos, player);
    }
}
