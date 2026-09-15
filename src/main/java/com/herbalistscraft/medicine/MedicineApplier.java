package com.herbalistscraft.medicine;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.integration.tan.TanIntegration;
import com.herbalistscraft.registry.ModEffects;
import com.herbalistscraft.registry.ModMedicines;
import com.herbalistscraft.registry.ModSounds;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

/**
 * Turns a medicine definition into what actually happens to the player: instant numbers,
 * timed effects, temperature and hydration nudges, cures and toxins.
 */
public final class MedicineApplier {
    private MedicineApplier() {}

    public static Optional<MedicineDefinition> lookup(RegistryAccess access, ResourceLocation id) {
        return access.registry(ModMedicines.REGISTRY).flatMap(registry -> registry.getOptional(id));
    }

    public static void drink(ServerPlayer player, ResourceLocation id, boolean tea) {
        apply(player, id, false, true);
    }

    public static void apply(ServerPlayer player, ResourceLocation id, boolean sideEffectsOnly, boolean playSound) {
        MedicineDefinition definition = lookup(player.server.registryAccess(), id).orElse(null);
        if (definition == null) {
            return;
        }
        double effectiveness = Config.medicineEffectiveness();
        if (!sideEffectsOnly) {
            for (MedicineEffect effect : definition.effects()) {
                applyEffect(player, effect, effectiveness);
            }
        }
        for (MedicineEffect effect : definition.sideEffects()) {
            applyEffect(player, effect, 1.0D);
        }
        if (definition.toxinPoints() > 0) {
            ToxicityManager.add(player, definition.toxinPoints() * ToxicityManager.guardFactor(player));
        }
        if (playSound) {
            player.level().playSound(null, player.blockPosition(),
                    tea ? ModSounds.SIP.get() : ModSounds.POUR.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
        }
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0D, player.getZ(),
                    6, 0.3D, 0.3D, 0.3D, 0.01D);
        }
    }

    public static void applyEffect(Player player, MedicineEffect effect, double effectiveness) {
        if (player.getRandom().nextFloat() > effect.chance()) {
            return;
        }
        switch (effect.type()) {
            case INSTANT_HEALTH -> player.heal((float) (effect.amount() * effectiveness));
            case HUNGER -> {
                float amount = (float) (effect.amount() * effectiveness);
                if (amount < 0.0F) {
                    player.getFoodData().eat(Math.round(-amount), 0.3F);
                } else {
                    player.getFoodData().addExhaustion(amount);
                }
            }
            case HYDRATE -> TanIntegration.hydrate(player, (float) (effect.amount() * effectiveness));
            case TEMP -> TanIntegration.adjustTemperature(player,
                    (float) ((effect.warmth() - effect.chill()) * effectiveness));
            case TEMPERATURE_RELIEF -> {
                TanIntegration.adjustTemperature(player, (float) (effect.amount() * 0.25D));
                applyHerbal(player, ModEffects.HEAT_WARD.get(), 600, 0);
                applyHerbal(player, ModEffects.COLD_WARD.get(), 600, 0);
            }
            case TOXIN -> ToxicityManager.add(player, effect.amount() * ToxicityManager.guardFactor(player));
            case TOXIN_CLEAR -> ToxicityManager.flush(player, (float) (effect.amount() * effectiveness));
            case CLEAR_BLEEDING -> {
                player.removeEffect(net.minecraft.world.effect.MobEffects.POISON);
                player.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
                applyHerbal(player, ModEffects.HEMOSTASIS.get(), 400, 0);
            }
            case CURE -> {
                for (ResourceLocation target : effect.targets()) {
                    holder(player, target).ifPresent(player::removeEffect);
                }
            }
            case EXTINGUISH -> player.clearFire();
            case HERBAL -> effect.effect().ifPresent(name -> {
                Holder<MobEffect> holder = herbalHolder(player, name);
                if (holder != null) {
                    applyHolder(player, holder, effect.duration(), effect.amplifier());
                }
            });
            case VANILLA -> effect.effect().ifPresent(name ->
                    holder(player, name).ifPresent(holder ->
                            applyHolder(player, holder, effect.duration(), effect.amplifier())));
        }
    }

    private static void applyHerbal(Player player, MobEffect effect, int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(holderOf(player, effect), duration, amplifier, false, true));
    }

    private static void applyHolder(Player player, Holder<MobEffect> holder, int duration, int amplifier) {
        if (duration <= 0) {
            player.addEffect(new MobEffectInstance(holder, 200, amplifier, false, true));
        } else {
            MobEffectInstance current = player.getEffect(holder);
            if (current == null || current.getDuration() < duration) {
                player.addEffect(new MobEffectInstance(holder, duration, amplifier, false, true));
            }
        }
    }

    private static Holder<MobEffect> holderOf(Player player, MobEffect effect) {
        return player.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).wrapAsHolder(effect);
    }

    private static Optional<Holder.Reference<MobEffect>> holder(Player player, ResourceLocation id) {
        return player.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getHolder(id);
    }

    private static Holder<MobEffect> herbalHolder(Player player, ResourceLocation id) {
        return player.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getHolder(id).orElse(null);
    }
}
