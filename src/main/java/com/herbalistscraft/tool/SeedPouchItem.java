package com.herbalistscraft.tool;

import com.herbalistscraft.registry.ModTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A pouch worn at the belt. While carried it nudges wild herbs into giving up extra seeds,
 * and it can gather a handful of seeds in one click so pockets stay tidy.
 */
public class SeedPouchItem extends Item {
    private static final int CAPACITY = 64;

    public SeedPouchItem(Properties properties) {
        super(properties);
    }

    /** The pouch in the player's inventory, or {@link ItemStack#EMPTY}. */
    public static ItemStack findWorn(Player player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.getItem() instanceof SeedPouchItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int stored(ItemStack pouch) {
        return pouch.getOrDefault(com.herbalistscraft.registry.ModDataComponents.HARVEST_DAY.get(), 0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack pouch = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(pouch);
        }
        List<ItemStack> gathered = new ArrayList<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && stack.is(ModTags.Items.SEEDS) && stack != pouch) {
                gathered.add(stack.copy());
                player.getInventory().setItem(slot, ItemStack.EMPTY);
            }
        }
        int total = gathered.stream().mapToInt(ItemStack::getCount).sum();
        if (total == 0) {
            player.displayClientMessage(Component.translatable("gui.herbalistscraft.seed_pouch.empty")
                    .withStyle(ChatFormatting.GRAY), true);
            return InteractionResultHolder.fail(pouch);
        }
        for (ItemStack seed : gathered) {
            if (!player.getInventory().add(seed)) {
                player.drop(seed, false);
            }
        }
        level.playSound(null, player.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.8F, 1.0F);
        player.displayClientMessage(Component.translatable("gui.herbalistscraft.seed_pouch.count", total)
                .withStyle(ChatFormatting.GREEN), true);
        return InteractionResultHolder.success(pouch);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.herbalistscraft.tool.seed_pouch").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gui.herbalistscraft.seed_pouch.capacity", CAPACITY)
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
