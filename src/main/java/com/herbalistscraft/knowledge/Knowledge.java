package com.herbalistscraft.knowledge;

import com.herbalistscraft.herb.HerbProperty;
import com.herbalistscraft.herb.HerbSeason;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Everything a player has learned: herbs identified, properties understood, recipes recorded,
 * medicines discovered, biomes visited and seasons seen. Permanent, saved with the player and
 * synced to their client so tooltips and the journal can be honest about what is unknown.
 */
public class Knowledge {
    public static final Codec<Knowledge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("herbs").forGetter(k -> List.copyOf(k.herbs)),
            Codec.STRING.listOf().fieldOf("properties").forGetter(k -> List.copyOf(k.properties)),
            ResourceLocation.CODEC.listOf().fieldOf("recipes").forGetter(k -> List.copyOf(k.recipes)),
            ResourceLocation.CODEC.listOf().fieldOf("medicines").forGetter(k -> List.copyOf(k.medicines)),
            Codec.STRING.listOf().fieldOf("biomes").forGetter(k -> List.copyOf(k.biomeGroups)),
            HerbSeason.CODEC.listOf().fieldOf("seasons").forGetter(k -> List.copyOf(k.seasons))
    ).apply(instance, Knowledge::new));

    /** Explicit, hand-rolled stream codec: no registry access required, no hidden allocations. */
    public static final StreamCodec<RegistryFriendlyByteBuf, Knowledge> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Knowledge decode(RegistryFriendlyByteBuf buffer) {
            Knowledge knowledge = new Knowledge();
            int herbs = buffer.readVarInt();
            for (int i = 0; i < herbs; i++) {
                knowledge.herbs.add(buffer.readResourceLocation());
            }
            int properties = buffer.readVarInt();
            for (int i = 0; i < properties; i++) {
                knowledge.properties.add(buffer.readUtf());
            }
            int recipes = buffer.readVarInt();
            for (int i = 0; i < recipes; i++) {
                knowledge.recipes.add(buffer.readResourceLocation());
            }
            int medicines = buffer.readVarInt();
            for (int i = 0; i < medicines; i++) {
                knowledge.medicines.add(buffer.readResourceLocation());
            }
            int biomes = buffer.readVarInt();
            for (int i = 0; i < biomes; i++) {
                knowledge.biomeGroups.add(buffer.readUtf());
            }
            int seasons = buffer.readVarInt();
            for (int i = 0; i < seasons; i++) {
                knowledge.seasons.add(buffer.readEnum(HerbSeason.class));
            }
            return knowledge;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, Knowledge value) {
            buffer.writeVarInt(value.herbs.size());
            for (ResourceLocation herb : value.herbs) {
                buffer.writeResourceLocation(herb);
            }
            buffer.writeVarInt(value.properties.size());
            for (String property : value.properties) {
                buffer.writeUtf(property);
            }
            buffer.writeVarInt(value.recipes.size());
            for (ResourceLocation recipe : value.recipes) {
                buffer.writeResourceLocation(recipe);
            }
            buffer.writeVarInt(value.medicines.size());
            for (ResourceLocation medicine : value.medicines) {
                buffer.writeResourceLocation(medicine);
            }
            buffer.writeVarInt(value.biomeGroups.size());
            for (String biome : value.biomeGroups) {
                buffer.writeUtf(biome);
            }
            buffer.writeVarInt(value.seasons.size());
            for (HerbSeason season : value.seasons) {
                buffer.writeEnum(season);
            }
        }
    };

    private final Set<ResourceLocation> herbs = new LinkedHashSet<>();
    private final Set<String> properties = new LinkedHashSet<>();
    private final Set<ResourceLocation> recipes = new LinkedHashSet<>();
    private final Set<ResourceLocation> medicines = new LinkedHashSet<>();
    private final Set<String> biomeGroups = new LinkedHashSet<>();
    private final Set<HerbSeason> seasons = new LinkedHashSet<>();

    public Knowledge() {}

    Knowledge(List<ResourceLocation> herbs, List<String> properties, List<ResourceLocation> recipes,
              List<ResourceLocation> medicines, List<String> biomeGroups, List<HerbSeason> seasons) {
        this.herbs.addAll(herbs);
        this.properties.addAll(properties);
        this.recipes.addAll(recipes);
        this.medicines.addAll(medicines);
        this.biomeGroups.addAll(biomeGroups);
        this.seasons.addAll(seasons);
    }

    // ---- learning -----------------------------------------------------------

    public boolean learnHerb(ResourceLocation herb) {
        return herbs.add(herb);
    }

    public boolean learnProperty(HerbProperty property) {
        return properties.add(property.name());
    }

    public boolean learnRecipe(ResourceLocation recipe) {
        return recipes.add(recipe);
    }

    public boolean learnMedicine(ResourceLocation medicine) {
        return medicines.add(medicine);
    }

    public boolean visitBiome(String group) {
        return biomeGroups.add(group);
    }

    public boolean seeSeason(HerbSeason season) {
        return seasons.add(season);
    }

    // ---- questions ----------------------------------------------------------

    public boolean knowsHerb(ResourceLocation herb) {
        return herbs.contains(herb);
    }

    public boolean knowsProperty(HerbProperty property) {
        return properties.contains(property.name());
    }

    public boolean knowsRecipe(ResourceLocation recipe) {
        return recipes.contains(recipe);
    }

    public boolean knowsMedicine(ResourceLocation medicine) {
        return medicines.contains(medicine);
    }

    public Set<ResourceLocation> herbs() {
        return Set.copyOf(herbs);
    }

    public Set<ResourceLocation> recipes() {
        return Set.copyOf(recipes);
    }

    public Set<ResourceLocation> medicines() {
        return Set.copyOf(medicines);
    }

    public Set<String> biomeGroups() {
        return Set.copyOf(biomeGroups);
    }

    public Set<HerbSeason> seasons() {
        return Set.copyOf(seasons);
    }

    public List<HerbProperty> knownProperties() {
        List<HerbProperty> known = new ArrayList<>();
        for (HerbProperty property : HerbProperty.VALUES) {
            if (knowsProperty(property)) {
                known.add(property);
            }
        }
        return known;
    }

    public int herbCount() {
        return herbs.size();
    }

    public int recipeCount() {
        return recipes.size();
    }

    public int medicineCount() {
        return medicines.size();
    }

    public int propertyCount() {
        return properties.size();
    }

    public int biomeCount() {
        return biomeGroups.size();
    }

    public int seasonCount() {
        return seasons.size();
    }
}
