package com.herbalistscraft.medicine;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.registry.ModItems;
import com.herbalistscraft.registry.ModMedicines;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Shared behaviour for the four medicine item classes: cooldowns, discovery and tooltips. */
public final class MedicineUse {
    private MedicineUse() {}

    public static boolean blockedByCooldown(Player player, ItemStack stack) {
        return player.getCooldowns().isOnCooldown(stack.getItem());
    }

    public static int cooldownTicks(Player player, ResourceKey<MedicineDefinition> key) {
        MedicineDefinition definition = definition(player.level(), key);
        int base = definition == null ? 200 : definition.cooldown();
        return (int) Math.round(base * Config.cooldownMultiplier());
    }

    public static int tier(Player player, ResourceKey<MedicineDefinition> key) {
        MedicineDefinition definition = definition(player.level(), key);
        return definition == null ? 1 : definition.tier();
    }

    public static void drink(ServerPlayer player, ItemStack stack, net.minecraft.resources.ResourceLocation id,
                             boolean tea) {
        MedicineDefinition definition = MedicineApplier.lookup(player.server.registryAccess(), id).orElse(null);
        if (definition == null) {
            return;
        }
        MedicineApplier.apply(player, id, false, true);
        player.getCooldowns().addCooldown(stack.getItem(),
                (int) Math.round(definition.cooldown() * Config.cooldownMultiplier()));
        Discovery.discoverMedicine(player, id,
                Component.translatable("item.herbalistscraft." + id.getPath()), definition.tier());
    }

    /** Container behaviour: a drunk tonic leaves a glass vial, a tea leaves its clay cup. */
    public static ItemStack onConsumed(ItemStack stack, Level level, LivingEntity entity, boolean tea) {
        ItemStack result = stack.copy();
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return result;
        }
        result.shrink(1);
        ItemStack container = new ItemStack(tea ? ModItems.CLAY_CUP.get() : ModItems.GLASS_VIAL.get());
        if (result.isEmpty()) {
            return container;
        }
        if (entity instanceof Player player && !player.getInventory().add(container)) {
            player.drop(container, false);
        }
        return result;
    }

    public static void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip,
                                     ResourceKey<MedicineDefinition> key) {
        if (context.registries() == null) {
            return;
        }
        MedicineDefinition definition = MedicineApplier.lookup(context.registries(), key.location()).orElse(null);
        if (definition == null) {
            return;
        }
        Player player = com.herbalistscraft.ClientBridge.localPlayer();
        boolean known = player == null || Discovery.knowledge(player).knowsMedicine(key.location());
        tooltip.add(Component.translatable("tooltip.herbalistscraft.item.lore",
                Component.translatable("medicine.herbalistscraft." + key.location().getPath() + ".description"))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.toxicity",
                definition.toxicity().displayName()).withStyle(definition.toxicity().severity() > 1
                        ? ChatFormatting.RED
                        : ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.cooldown",
                Math.round(definition.cooldown() * Config.cooldownMultiplier() / 20.0F)).withStyle(ChatFormatting.GRAY));
        if (!known) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.undiscovered")
                    .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.not_discovered_hint")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.discovered")
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.properties")
                .withStyle(ChatFormatting.GRAY));
        for (MedicineEffect effect : definition.effects()) {
            tooltip.add(Component.literal(" • ").append(EffectNames.describe(effect)).withStyle(ChatFormatting.AQUA));
        }
        if (!definition.sideEffects().isEmpty()) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.medicine.side_effects")
                    .withStyle(ChatFormatting.GRAY));
            for (MedicineEffect effect : definition.sideEffects()) {
                tooltip.add(Component.literal(" • ").append(EffectNames.describe(effect))
                        .withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    private static MedicineDefinition definition(Level level, ResourceKey<MedicineDefinition> key) {
        return level.registryAccess().registry(ModMedicines.REGISTRY)
                .flatMap(registry -> registry.getOptional(key.location())).orElse(null);
    }
}
