package com.herbalistscraft.knowledge;

import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * All Herbalist's Craft advancements are data-driven files using {@code minecraft:impossible};
 * this class owns the real conditions and hands them out when the player earns them.
 */
public final class AdvancementGrants {
    private AdvancementGrants() {}

    public static final String FIRST_LEAF = "first_leaf";
    public static final String BITTER_BEGINNING = "bitter_beginning";
    public static final String DRYING_TIME = "drying_time";
    public static final String MILLWRIGHT = "millwright";
    public static final String FIELD_MEDICINE = "field_medicine";
    public static final String POISONERS_ART = "poisoners_art";
    public static final String BOTANICAL_SCHOLAR = "botanical_scholar";
    public static final String MASTER_HERBALIST = "master_herbalist";
    public static final String DEEP_KNOWLEDGE = "deep_knowledge";
    public static final String FOUR_SEASONS = "four_seasons";
    public static final String WANDERER = "wanderer";
    public static final String TOXIN_SURVIVOR = "toxin_survivor";
    public static final String TRADING_HERBS = "trading_herbs";
    public static final String ANCIENT_KNOWLEDGE = "ancient_knowledge";

    public static void grant(ServerPlayer player, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, path);
        for (AdvancementHolder holder : player.server.getAdvancements().getAllAdvancements()) {
            if (holder.id().equals(id)) {
                player.getAdvancements().award(holder, "granted");
                return;
            }
        }
    }
}
