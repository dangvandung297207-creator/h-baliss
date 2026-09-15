package com.herbalistscraft.medicine;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** A tea drunk from a clay cup; the empty cup is handed back when the last sip is gone. */
public class TeaItem extends Item {
    private final ResourceKey<MedicineDefinition> medicine;

    public TeaItem(Properties properties, ResourceKey<MedicineDefinition> medicine) {
        super(properties);
        this.medicine = medicine;
    }

    public ResourceKey<MedicineDefinition> medicine() {
        return medicine;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (MedicineUse.blockedByCooldown(player, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            MedicineUse.drink(serverPlayer, stack, medicine.location(), true);
        }
        return MedicineUse.onConsumed(stack, level, entity, true);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MedicineUse.appendTooltip(stack, context, tooltip, medicine);
    }
}
