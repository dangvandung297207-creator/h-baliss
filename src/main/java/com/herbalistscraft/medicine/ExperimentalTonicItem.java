package com.herbalistscraft.medicine;

import com.herbalistscraft.Config;
import com.herbalistscraft.mixing.MixtureData;
import com.herbalistscraft.mixing.PropertyEffects;
import com.herbalistscraft.registry.ModDataComponents;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

/**
 * An unnamed tonic: whatever the table computed is stored on the bottle itself, so the drink
 * always does what the calculator said it would. Careless experiments hurt.
 */
public class ExperimentalTonicItem extends Item {
    public ExperimentalTonicItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            MixtureData data = stack.get(ModDataComponents.MIXTURE.get());
            if (data == null) {
                serverPlayer.displayClientMessage(Component.translatable("message.herbalistscraft.mixture.inert")
                        .withStyle(ChatFormatting.GRAY), true);
            } else {
                for (MedicineEffect effect : PropertyEffects.effects(properties(data), data.potency())) {
                    MedicineApplier.applyEffect(serverPlayer, effect, Config.medicineEffectiveness());
                }
                if (data.toxin() > 0) {
                    ToxicityManager.add(serverPlayer, data.toxin());
                }
                serverPlayer.displayClientMessage(Component.translatable("message.herbalistscraft.mixture.tasted",
                        Component.literal(data.note().isEmpty() ? "???" : data.note()))
                        .withStyle(ChatFormatting.YELLOW), true);
            }
            com.herbalistscraft.knowledge.Discovery.onCraft(serverPlayer);
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
        MixtureData data = stack.get(ModDataComponents.MIXTURE.get());
        tooltip.add(Component.translatable("tooltip.herbalistscraft.mixture.unnamed")
                .withStyle(ChatFormatting.DARK_GRAY));
        if (data == null) {
            return;
        }
        tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.properties")
                .withStyle(ChatFormatting.GRAY));
        for (MedicineEffect effect : PropertyEffects.effects(properties(data), data.potency())) {
            tooltip.add(Component.literal(" • ").append(EffectNames.describe(effect))
                    .withStyle(ChatFormatting.AQUA));
        }
        if (data.toxin() > 0) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.mixture.toxin", data.toxin())
                    .withStyle(ChatFormatting.RED));
        }
    }

    public static Map<com.herbalistscraft.herb.HerbProperty, Float> properties(MixtureData data) {
        Map<com.herbalistscraft.herb.HerbProperty, Float> out =
                new java.util.EnumMap<>(com.herbalistscraft.herb.HerbProperty.class);
        for (Map.Entry<String, Float> entry : data.properties().entrySet()) {
            com.herbalistscraft.herb.HerbProperty.byName(entry.getKey()).ifPresent(
                    property -> out.put(property, entry.getValue()));
        }
        return out;
    }
}
