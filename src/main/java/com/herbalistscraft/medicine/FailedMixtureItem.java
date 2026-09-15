package com.herbalistscraft.medicine;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** The mixture curdled. Drinking it is an act of pure optimism, and it is punished. */
public class FailedMixtureItem extends Item {
    public FailedMixtureItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 0));
            ToxicityManager.add(serverPlayer, 10.0F);
            serverPlayer.displayClientMessage(Component.translatable("message.herbalistscraft.mixture.failed")
                    .withStyle(ChatFormatting.DARK_RED), true);
        }
        return MedicineUse.onConsumed(stack, level, entity, false);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.herbalistscraft.mixture.failed").withStyle(ChatFormatting.DARK_RED));
    }
}
