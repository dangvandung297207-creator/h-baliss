package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Restrained, readable particles: herbal green, frost, ember and toxic smoke. */
public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, HerbalistsCraft.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HERBAL_SPARK =
            PARTICLES.register("herbal_spark", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FROST_SPARK =
            PARTICLES.register("frost_spark", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMBER_SPARK =
            PARTICLES.register("ember_spark", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TOXIC_SMOKE =
            PARTICLES.register("toxic_smoke", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIED_LEAF =
            PARTICLES.register("dried_leaf", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
