package com.herbalistscraft.medicine;

import com.herbalistscraft.Config;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Weapon oil. Right-click to smear it on the weapon in your other hand: a limited number of
 * hits, a visible glint, and then it is gone. Nothing here touches the weapon permanently.
 */
public class WeaponOilItem extends Item {
    private final ResourceKey<MedicineDefinition> medicine;

    public WeaponOilItem(Properties properties, ResourceKey<MedicineDefinition> medicine) {
        super(properties);
        this.medicine = medicine;
    }

    public ResourceKey<MedicineDefinition> medicine() {
        return medicine;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack oil = player.getItemInHand(hand);
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack target = player.getItemInHand(other);
        if (target.isEmpty() || !isCoatable(target)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.herbalistscraft.oil.no_weapon")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResultHolder.fail(oil);
        }
        if (target.has(ModDataComponents.COATING.get())) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.herbalistscraft.oil.too_many")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResultHolder.fail(oil);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(oil);
        }
        MedicineDefinition definition = MedicineApplier.lookup(level.registryAccess(), medicine.location()).orElse(null);
        if (definition == null || definition.coating().isEmpty()) {
            return InteractionResultHolder.fail(oil);
        }
        MedicineDefinition.Coating coating = definition.coating().get();
        int charges = Math.max(1, (int) Math.round(coating.charges() * Config.coatingCharges()));
        int duration = Math.max(20, (int) Math.round(coating.durationTicks() * Config.coatingDuration()));
        target.set(ModDataComponents.COATING.get(), new CoatingData(medicine.location(), charges, charges,
                level.getGameTime(), duration));
        target.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        level.playSound(null, player.blockPosition(), com.herbalistscraft.registry.ModSounds.POUR.get(),
                SoundSource.PLAYERS, 0.7F, 1.3F);
        player.displayClientMessage(Component.translatable("message.herbalistscraft.oil.applied",
                Component.translatable("item.herbalistscraft." + medicine.location().getPath()))
                .withStyle(ChatFormatting.GREEN), true);
        if (player instanceof ServerPlayer serverPlayer) {
            Discovery.onCoating(serverPlayer);
        }
        if (!player.getAbilities().instabuild) {
            oil.shrink(1);
        }
        return InteractionResultHolder.success(oil);
    }

    /** Anything with an edge, per the coatable_weapons item tag plus the vanilla tag fallbacks. */
    public static boolean isCoatable(ItemStack stack) {
        return stack.is(com.herbalistscraft.registry.ModTags.CoatableWeapons.ITEMS)
                || stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.AXES)
                || stack.is(ItemTags.HOES)
                || stack.is(ItemTags.PICKAXES)
                || stack.is(ItemTags.SHOVELS);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MedicineUse.appendTooltip(stack, context, tooltip, medicine);
        MedicineDefinition definition = context.registries() == null ? null
                : MedicineApplier.lookup(context.registries(), medicine.location()).orElse(null);
        if (definition != null && definition.coating().isPresent()) {
            MedicineDefinition.Coating coating = definition.coating().get();
            tooltip.add(Component.translatable("tooltip.herbalistscraft.coating.charges",
                    Math.round(coating.charges() * Config.coatingCharges())).withStyle(ChatFormatting.GRAY));
        }
    }
}
