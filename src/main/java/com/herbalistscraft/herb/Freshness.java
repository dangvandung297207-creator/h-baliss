package com.herbalistscraft.herb;

import com.herbalistscraft.Config;
import com.herbalistscraft.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Fresh herbs are strong but short lived; dried and processed forms keep forever. */
public final class Freshness {
    private Freshness() {}

    public enum State {
        FRESH(1.0F),
        AGING(0.7F),
        SPOILED(0.35F),
        STABLE(1.0F);

        private final float potency;

        State(float potency) {
            this.potency = potency;
        }

        public float potency() {
            return potency;
        }

        public String translationKey() {
            return "freshness.herbalistscraft." + name();
        }

        public Component displayName() {
            return Component.translatable(translationKey());
        }
    }

    public static State state(ItemStack stack, HerbForm form, long currentDay) {
        if (!Config.freshnessEnabled() || form.isStable()) {
            return State.STABLE;
        }
        Integer harvested = stack.get(ModDataComponents.HARVEST_DAY.get());
        if (harvested == null) {
            return State.FRESH;
        }
        long age = currentDay - harvested;
        if (age <= Config.freshDays()) {
            return State.FRESH;
        }
        if (age <= (long) Config.freshDays() + Config.agingDays()) {
            return State.AGING;
        }
        return State.SPOILED;
    }

    /** Multiplier applied to a herb's potency when it is worked into a medicine. */
    public static float potencyMultiplier(ItemStack stack, HerbForm form, long currentDay) {
        if (!Config.freshnessEffect()) {
            return form.potency();
        }
        return state(stack, form, currentDay).potency() * form.potency();
    }

    /** Adds the freshness line; {@code currentDay} is -1 when the caller has no level to hand. */
    public static void appendTooltip(ItemStack stack, HerbForm form, List<Component> tooltip, long currentDay) {
        Integer harvested = stack.get(ModDataComponents.HARVEST_DAY.get());
        if (form.isStable() || harvested == null || currentDay < 0L) {
            return;
        }
        State state = state(stack, form, currentDay);
        ChatFormatting colour = switch (state) {
            case FRESH -> ChatFormatting.GREEN;
            case AGING -> ChatFormatting.YELLOW;
            case SPOILED -> ChatFormatting.DARK_RED;
            case STABLE -> ChatFormatting.GRAY;
        };
        tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.freshness", state.displayName())
                .withStyle(colour));
    }

    public static boolean isSpoiled(ItemStack stack, HerbForm form, long currentDay) {
        return state(stack, form, currentDay) == State.SPOILED;
    }
}
