package com.herbalistscraft.tool;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Pruning shears: cut a herb without killing the plant. Harvesting with them in hand guarantees
 * the plant survives and yields an extra seed, at the cost of the shears' own durability.
 */
public class PruningShearsItem extends Item {
    public PruningShearsItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide && state.getBlock() instanceof com.herbalistscraft.herb.HerbCropBlock
                && stack.isDamageableItem()) {
            stack.hurtAndBreak(1, miner, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.getBlock() instanceof com.herbalistscraft.herb.HerbCropBlock;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.herbalistscraft.tool.pruning_shears")
                .withStyle(ChatFormatting.GRAY));
    }
}
