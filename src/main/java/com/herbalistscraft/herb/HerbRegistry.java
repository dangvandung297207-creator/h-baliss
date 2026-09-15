package com.herbalistscraft.herb;

import com.herbalistscraft.registry.ModHerbs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

/**
 * Cached lookups into the synced herb registry. The cache is keyed on the registry instance,
 * so a datapack reload (or a client sync) drops it automatically; nothing is scanned per tick.
 */
public final class HerbRegistry {
    private static Registry<HerbDefinition> cachedRegistry;
    private static final Map<ResourceKey<HerbDefinition>, HerbDefinition> CACHE = new HashMap<>();

    private HerbRegistry() {}

    public static Registry<HerbDefinition> registry(RegistryAccess access) {
        return access.registryOrThrow(ModHerbs.REGISTRY);
    }

    public static Optional<HerbDefinition> get(RegistryAccess access, ResourceKey<HerbDefinition> key) {
        return Optional.ofNullable(lookup(registry(access), key));
    }

    public static Optional<HerbDefinition> get(HolderLookup.Provider provider, ResourceKey<HerbDefinition> key) {
        return provider.lookup(ModHerbs.REGISTRY)
                .flatMap(lookup -> lookup.get(key))
                .map(holder -> holder.value());
    }

    public static List<HerbDefinition> all(RegistryAccess access) {
        return registry(access).stream().toList();
    }

    private static HerbDefinition lookup(Registry<HerbDefinition> registry, ResourceKey<HerbDefinition> key) {
        if (registry != cachedRegistry) {
            cachedRegistry = registry;
            CACHE.clear();
        }
        HerbDefinition cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        HerbDefinition value = registry.get(key);
        if (value != null) {
            CACHE.put(key, value);
        }
        return value;
    }
}
