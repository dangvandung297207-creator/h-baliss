package com.herbalistscraft.medicine;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** Human-readable one-liners for the effect vocabulary, used by tooltips and the journal. */
public final class EffectNames {
    private EffectNames() {}

    public static MutableComponent describe(MedicineEffect effect) {
        String seconds = effect.duration() > 0 ? (effect.duration() / 20) + "s" : null;
        return switch (effect.type()) {
            case INSTANT_HEALTH -> Component.translatable("effect.herbalistscraft.instant_health")
                    .append(Component.literal(" " + trim(effect.amount())).withStyle(ChatFormatting.WHITE));
            case HUNGER -> effect.amount() < 0.0F
                    ? Component.translatable("effect.herbalistscraft.nourishment")
                    : Component.translatable("effect.herbalistscraft.hunger");
            case HYDRATE -> Component.translatable("effect.herbalistscraft.hydration");
            case TEMP -> effect.warmth() >= effect.chill()
                    ? Component.translatable("effect.herbalistscraft.warmth")
                    : Component.translatable("effect.herbalistscraft.chill");
            case TEMPERATURE_RELIEF -> Component.translatable("effect.herbalistscraft.temperature_relief");
            case TOXIN -> Component.translatable("effect.herbalistscraft.toxin").withStyle(ChatFormatting.DARK_RED);
            case TOXIN_CLEAR -> Component.translatable("effect.herbalistscraft.detox").withStyle(ChatFormatting.GREEN);
            case CLEAR_BLEEDING -> Component.translatable("effect.herbalistscraft.hemostasis");
            case CURE -> Component.translatable("effect.herbalistscraft.cure");
            case EXTINGUISH -> Component.translatable("effect.herbalistscraft.extinguish");
            case HERBAL -> effect.effect()
                    .map(id -> (MutableComponent) Component.translatable("effect." + id.toLanguageKey()))
                    .orElseGet(() -> Component.literal("herbal"));
            case VANILLA -> effect.effect()
                    .map(id -> {
                        MutableComponent name = Component.translatable(
                                net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(
                                        net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.get(id))
                                        .toLanguageKey("effect"));
                        return name;
                    })
                    .orElseGet(() -> Component.literal("effect"));
        };
    }

    private static String trim(float value) {
        return value == Math.round(value) ? String.valueOf(Math.round(value)) : String.valueOf(value);
    }
}
