package com.herbalistscraft.medicine;

import com.herbalistscraft.registry.ModAttachments;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The mod's own effects. Each one nudges the body in a small, temporary way - faster healing,
 * slower toxin build-up, resistance to cold or heat - and none of them grants invulnerability.
 */
public class HerbalEffect extends MobEffect {
    public enum Behaviour {
        HEAL_TICK,
        TOXIN_GUARD,
        COLD_WARD,
        HEAT_WARD,
        FIRE_WARD,
        FOCUS,
        RESPITE,
        CALM,
        VIGOR,
        VITALITY
    }

    private final Behaviour behaviour;

    public HerbalEffect(MobEffectCategory category, int color, Behaviour behaviour) {
        super(category, color);
        this.behaviour = behaviour;
    }

    public Behaviour behaviour() {
        return behaviour;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return switch (behaviour) {
            case HEAL_TICK -> duration % 60 == 0;
            case VIGOR -> duration % 40 == 0;
            default -> duration % 20 == 0;
        };
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        switch (behaviour) {
            case HEAL_TICK -> {
                if (entity.getHealth() < entity.getMaxHealth()) {
                    entity.heal(0.5F + 0.5F * amplifier);
                }
            }
            case VIGOR -> {
                if (entity instanceof Player player) {
                    player.getFoodData().addExhaustion(-0.02F * (amplifier + 1));
                }
            }
            case RESPITE -> {
                if (entity instanceof Player player && player.isOnFire()) {
                    player.clearFire();
                }
            }
            case CALM -> entity.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
            case TOXIN_GUARD -> {
                if (entity instanceof Player player && player.tickCount % 200 == 0) {
                    com.herbalistscraft.medicine.ToxicityManager.decay(player, 1);
                }
            }
            default -> {
                // Wards and focus are read by the handlers that need them.
            }
        }
        return true;
    }
}
