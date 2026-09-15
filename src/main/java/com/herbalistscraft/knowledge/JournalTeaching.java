package com.herbalistscraft.knowledge;

import com.herbalistscraft.registry.ModMedicines;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Reading a journal page teaches real recipes: the page kind decides which slice of the
 * medicine list it covers, and only medicines the player has not recorded yet are taught.
 */
public final class JournalTeaching {
    private JournalTeaching() {}

    public static int teach(ServerPlayer player, JournalPageKind kind) {
        List<ResourceLocation> candidates = new ArrayList<>();
        var registry = player.server.registryAccess().registryOrThrow(ModMedicines.REGISTRY);
        registry.forEach(definition -> {
            ResourceLocation id = registry.getKey(definition);
            if (id != null && matches(kind, id)) {
                candidates.add(id);
            }
        });
        Knowledge knowledge = Discovery.knowledge(player);
        int taught = 0;
        for (ResourceLocation id : candidates) {
            if (taught >= kind.recipes()) {
                break;
            }
            if (knowledge.learnRecipe(id)) {
                taught++;
            }
        }
        if (taught > 0) {
            Discovery.sync(player, knowledge);
        }
        return taught;
    }

    private static boolean matches(JournalPageKind kind, ResourceLocation id) {
        String path = id.getPath();
        return switch (kind) {
            case TOXIC -> path.contains("toxic") || path.contains("venom") || path.contains("poison")
                    || path.contains("venom") || path.contains("sleep") || path.contains("weak");
            case ANCIENT -> path.contains("ancient") || path.contains("dragon") || path.contains("vitalis")
                    || path.contains("moonwater") || path.contains("essence");
            case MEDICINAL -> path.contains("healing") || path.contains("antiseptic") || path.contains("salve")
                    || path.contains("hemostatic");
            case HERBAL -> !path.contains("toxic");
        };
    }
}
