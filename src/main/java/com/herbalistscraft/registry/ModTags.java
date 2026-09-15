package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Tags the mod both reads and generates, in one place. */
public final class ModTags {
    private ModTags() {}

    private static TagKey<Item> item(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, path));
    }

    private static TagKey<Block> block(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, path));
    }

    public static final class Items {
        public static final TagKey<Item> HERBS = item("herbs");
        public static final TagKey<Item> SEEDS = item("herb_seeds");
        public static final TagKey<Item> DRIED = item("dried_herbs");
        public static final TagKey<Item> POWDERS = item("herb_powders");
        public static final TagKey<Item> EXTRACTS = item("herb_extracts");
        public static final TagKey<Item> BASES = item("herbal_bases");
        public static final TagKey<Item> CATALYSTS = item("catalysts");
        public static final TagKey<Item> MATERIALS = item("herbal_materials");
        public static final TagKey<Item> TOXIC = item("toxic_herbs");

        private Items() {}
    }

    /** The coatable_weapons tag, as ItemTags does not expose mod tags. */
    public static final class CoatableWeapons {
        public static final TagKey<Item> ITEMS = item("coatable_weapons");

        private CoatableWeapons() {}
    }

    public static final class Blocks {
        public static final TagKey<Block> CROPS = block("herb_crops");

        private Blocks() {}
    }
}
