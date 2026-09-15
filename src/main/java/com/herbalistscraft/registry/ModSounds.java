package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The seven hand-synthesised sounds the mod adds. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, HerbalistsCraft.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> GRIND = register("grind");
    public static final DeferredHolder<SoundEvent, SoundEvent> MILL = register("mill");
    public static final DeferredHolder<SoundEvent, SoundEvent> POUR = register("pour");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRY = register("dry");
    public static final DeferredHolder<SoundEvent, SoundEvent> SALVE = register("salve");
    public static final DeferredHolder<SoundEvent, SoundEvent> DISCOVERY = register("discovery");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIP = register("sip");

    private ModSounds() {}

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, name)));
    }
}
