package com.herbalistscraft.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** An empty vial or cup: fill it from water, and it becomes the base for a medicine. */
public class FillableContainerItem extends Item {
    private final FillTarget target;

    public FillableContainerItem(Properties properties, FillTarget target) {
        super(properties);
        this.target = target;
    }

    public FillTarget target() {
        return target;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = player.pick(4.5D, 0.0F, true);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }
        BlockPos pos = blockHit.getBlockPos();
        boolean water = level.getFluidState(pos).is(Fluids.WATER) || level.getFluidState(pos.above()).is(Fluids.WATER);
        if (!water) {
            return InteractionResultHolder.pass(stack);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        ItemStack filled = new ItemStack(target.filled());
        level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.8F, 1.0F);
        player.getCooldowns().addCooldown(this, 10);
        if (player.getAbilities().instabuild) {
            return InteractionResultHolder.success(stack);
        }
        stack.shrink(1);
        if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
        return InteractionResultHolder.success(stack);
    }
}
