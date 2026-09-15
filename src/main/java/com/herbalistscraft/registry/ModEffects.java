package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.medicine.HerbalEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The mod's own effects. None of them grant combat invulnerability. */
public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, HerbalistsCraft.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> HEMOSTASIS = EFFECTS.register("hemostasis",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0xB5302C, HerbalEffect.Behaviour.HEAL_TICK));
    public static final DeferredHolder<MobEffect, MobEffect> ANTISEPTIC = EFFECTS.register("antiseptic",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0x7FC7A0, HerbalEffect.Behaviour.TOXIN_GUARD));
    public static final DeferredHolder<MobEffect, MobEffect> COLD_WARD = EFFECTS.register("cold_ward",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0x8FD2E8, HerbalEffect.Behaviour.COLD_WARD));
    public static final DeferredHolder<MobEffect, MobEffect> HEAT_WARD = EFFECTS.register("heat_ward",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0xE0A23F, HerbalEffect.Behaviour.HEAT_WARD));
    public static final DeferredHolder<MobEffect, MobEffect> FIRE_WARD = EFFECTS.register("fire_ward",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0xE06A2A, HerbalEffect.Behaviour.FIRE_WARD));
    public static final DeferredHolder<MobEffect, MobEffect> FOCUS = EFFECTS.register("focus",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0xE0B23F, HerbalEffect.Behaviour.FOCUS));
    public static final DeferredHolder<MobEffect, MobEffect> RESPIRATORY = EFFECTS.register("respiratory",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0x9FD8D8, HerbalEffect.Behaviour.RESPITE));
    public static final DeferredHolder<MobEffect, MobEffect> SEDATION = EFFECTS.register("sedation",
            () -> new HerbalEffect(MobEffectCategory.NEUTRAL, 0x8B7FC7, HerbalEffect.Behaviour.CALM));
    public static final DeferredHolder<MobEffect, MobEffect> VIGOR = EFFECTS.register("vigor",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0xE8D24A, HerbalEffect.Behaviour.VIGOR));
    public static final DeferredHolder<MobEffect, MobEffect> VITALITY = EFFECTS.register("vitality",
            () -> new HerbalEffect(MobEffectCategory.BENEFICIAL, 0xE07AB0, HerbalEffect.Behaviour.VITALITY));

    private ModEffects() {}
}
