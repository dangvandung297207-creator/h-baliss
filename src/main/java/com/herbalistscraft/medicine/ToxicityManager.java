package com.herbalistscraft.medicine;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.Toxicity;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.registry.ModAttachments;
import com.herbalistscraft.registry.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * Accumulated toxicity: every toxic dose adds up, and the body pays for it with nausea,
 * weakness, poison, slowness, hunger, dizziness and finally a temporary loss of heart.
 * It decays on its own and can be flushed with a detox tonic.
 */
public final class ToxicityManager {
    private static final ResourceLocation PENALTY_ID =
            ResourceLocation.fromNamespaceAndPath("herbalistscraft", "toxin_weakness");

    public static final int NAUSEA = 18;
    public static final int WEAKNESS = 30;
    public static final int POISON = 45;
    public static final int SLOWNESS = 60;
    public static final int HUNGER = 75;
    public static final int DIZZINESS = 85;
    public static final int HEART_LOSS = 95;
    public static final int EXTREME = 120;

    private ToxicityManager() {}

    public static int load(Player player) {
        return player.getData(ModAttachments.TOXIN);
    }

    public static void set(Player player, int value) {
        int clamped = Math.max(0, Math.min(400, value));
        player.setData(ModAttachments.TOXIN, clamped);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.syncData(ModAttachments.TOXIN);
        }
    }

    public static void add(Player player, float amount) {
        if (!Config.toxicityEnabled() || amount <= 0.0F) {
            return;
        }
        addRaw(player, amount * (float) Config.toxicityMultiplier());
    }

    public static void addRaw(Player player, float amount) {
        if (!Config.toxicityEnabled() || amount <= 0.0F) {
            return;
        }
        int before = load(player);
        set(player, before + Math.round(amount));
        int after = load(player);
        if (after != before) {
            applyPenalties(player, after);
            if (before < NAUSEA && after >= NAUSEA && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.translatable("message.herbalistscraft.toxicity.rising")
                        .withStyle(ChatFormatting.DARK_RED), true);
            }
        }
    }

    public static void decay(Player player, int amount) {
        int load = load(player);
        if (load <= 0) {
            return;
        }
        int updated = Math.max(0, load - amount);
        set(player, updated);
        if (updated < NAUSEA) {
            clearPenalties(player);
        }
    }

    /** Flushes toxin points; returns how much was actually removed. */
    public static int flush(Player player, float amount) {
        int load = load(player);
        if (load <= 0) {
            return 0;
        }
        int removed = Math.min(load, Math.round(amount));
        set(player, load - removed);
        if (load(player) < NAUSEA) {
            clearPenalties(player);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable("message.herbalistscraft.toxicity.cleared")
                    .withStyle(ChatFormatting.GREEN), true);
            Discovery.onToxinPurged(serverPlayer);
        }
        return removed;
    }

    /** Slows the next dose down when the antiseptic effect is running. */
    public static float guardFactor(Player player) {
        return player.hasEffect(ModEffects.ANTISEPTIC) ? 0.5F : 1.0F;
    }

    public static Toxicity band(Player player) {
        int load = load(player);
        if (load >= HEART_LOSS) {
            return Toxicity.EXTREME;
        }
        if (load >= SLOWNESS) {
            return Toxicity.HIGH;
        }
        if (load >= WEAKNESS) {
            return Toxicity.MEDIUM;
        }
        if (load >= NAUSEA) {
            return Toxicity.LOW;
        }
        return Toxicity.NONE;
    }

    public static void applyPenalties(Player player, int load) {
        if (load >= NAUSEA) {
            apply(player, MobEffects.CONFUSION, 200, 0);
        }
        if (load >= WEAKNESS) {
            apply(player, MobEffects.WEAKNESS, 200, 0);
        }
        if (load >= POISON) {
            apply(player, MobEffects.POISON, 160, 0);
        }
        if (load >= SLOWNESS) {
            apply(player, MobEffects.MOVEMENT_SLOWDOWN, 200, 0);
        }
        if (load >= HUNGER) {
            apply(player, MobEffects.HUNGER, 200, 0);
        }
        if (load >= DIZZINESS) {
            apply(player, MobEffects.CONFUSION, 200, 0);
        }
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            boolean shouldReduce = load >= HEART_LOSS;
            AttributeModifier existing = maxHealth.getModifier(PENALTY_ID);
            if (shouldReduce && existing == null) {
                maxHealth.addTransientModifier(new AttributeModifier(PENALTY_ID, -4.0D,
                        AttributeModifier.Operation.ADD_VALUE));
            } else if (!shouldReduce && existing != null) {
                maxHealth.removeModifier(PENALTY_ID);
            }
        }
    }

    public static void clearPenalties(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(PENALTY_ID) != null) {
            maxHealth.removeModifier(PENALTY_ID);
        }
    }

    private static void apply(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current == null || current.getDuration() < 60) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
        }
    }
}
