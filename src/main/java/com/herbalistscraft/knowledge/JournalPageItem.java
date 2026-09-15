package com.herbalistscraft.knowledge;

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

/** A single journal page. Reading it teaches the reader a handful of recipes permanently. */
public class JournalPageItem extends Item {
    private final JournalPageKind kind;

    public JournalPageItem(Properties properties, JournalPageKind kind) {
        super(properties);
        this.kind = kind;
    }

    public JournalPageKind kind() {
        return kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            int learned = JournalTeaching.teach(serverPlayer, kind);
            if (learned > 0) {
                level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS,
                        0.9F, 1.0F);
                player.displayClientMessage(Component.translatable("message.herbalistscraft.recipe.discovered",
                        Component.translatable(kind.translationKey())).withStyle(ChatFormatting.GOLD), false);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(kind.translationKey()).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.herbalistscraft.journal.hint").withStyle(ChatFormatting.GRAY));
    }
}
