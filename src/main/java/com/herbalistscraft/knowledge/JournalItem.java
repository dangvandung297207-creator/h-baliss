package com.herbalistscraft.knowledge;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * The Herbalist's Journal: opens the mod's own six-page book, not a written book.
 * Sneak-use opens it straight to the seasonal pages.
 */
public class JournalItem extends Item {
    public JournalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            com.herbalistscraft.ClientBridge.openJournal(player.isShiftKeyDown());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.herbalistscraft.journal.hint").withStyle(ChatFormatting.GRAY));
        if (context.registries() != null) {
            Player player = com.herbalistscraft.ClientBridge.localPlayer();
            if (player != null) {
                Knowledge knowledge = Discovery.knowledge(player);
                tooltip.add(Component.translatable("gui.herbalistscraft.journal.herbs.known",
                        knowledge.herbCount(), 49).withStyle(ChatFormatting.DARK_GREEN));
                tooltip.add(Component.translatable("gui.herbalistscraft.journal.properties.known",
                        knowledge.propertyCount(), net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.size())
                        .withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }
}
