package com.herbalistscraft;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/** Every knob the design calls for, split into a common (gameplay) and a client file. */
public final class Config {
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        Pair<Common, ModConfigSpec> common = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        COMMON_SPEC = common.getRight();
        Pair<Client, ModConfigSpec> client = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
    }

    private Config() {}

    /** Playback values that must never be null even before the config file is read. */
    private static boolean bool(ModConfigSpec.BooleanValue value, boolean fallback) {
        return value != null && value.get() != null ? value.get() : fallback;
    }

    private static double number(ModConfigSpec.DoubleValue value, double fallback) {
        return value != null && value.get() != null ? value.get() : fallback;
    }

    private static int integer(ModConfigSpec.IntValue value, int fallback) {
        return value != null && value.get() != null ? value.get() : fallback;
    }

    private static <E extends Enum<E>> E select(ModConfigSpec.EnumValue<E> value, E fallback) {
        return value != null && value.get() != null ? value.get() : fallback;
    }

    public static final class Common {
        // world generation
        public final ModConfigSpec.BooleanValue enableWildHerbs;
        public final ModConfigSpec.DoubleValue herbSpawnRate;
        public final ModConfigSpec.DoubleValue rareHerbSpawnRate;
        // growth
        public final ModConfigSpec.DoubleValue plantGrowthSpeed;
        public final ModConfigSpec.BooleanValue regrowth;
        public final ModConfigSpec.BooleanValue seasonalGrowth;
        // seasons
        public final ModConfigSpec.EnumValue<SeasonSource> seasonProvider;
        public final ModConfigSpec.IntValue seasonLengthDays;
        public final ModConfigSpec.BooleanValue seasonsAffectWorldgen;
        // medicine
        public final ModConfigSpec.DoubleValue medicineEffectiveness;
        public final ModConfigSpec.DoubleValue cooldownMultiplier;
        public final ModConfigSpec.BooleanValue freshnessEffect;
        // toxicity
        public final ModConfigSpec.BooleanValue toxicityEnabled;
        public final ModConfigSpec.DoubleValue toxicityMultiplier;
        public final ModConfigSpec.IntValue toxinDecayPerMinute;
        // freshness
        public final ModConfigSpec.BooleanValue freshnessEnabled;
        public final ModConfigSpec.IntValue freshDays;
        public final ModConfigSpec.IntValue agingDays;
        public final ModConfigSpec.IntValue dryingTicks;
        // crafting
        public final ModConfigSpec.BooleanValue experimentation;
        public final ModConfigSpec.DoubleValue coatingCharges;
        public final ModConfigSpec.DoubleValue coatingDuration;
        public final ModConfigSpec.DoubleValue experimentToxinScale;
        // structures
        // villagers
        public final ModConfigSpec.BooleanValue villagersEnabled;
        public final ModConfigSpec.BooleanValue biomeTrades;
        // integrations
        public final ModConfigSpec.BooleanValue toughAsNails;
        public final ModConfigSpec.BooleanValue toughAsNailsSeasons;
        public final ModConfigSpec.BooleanValue sereneSeasons;
        public final ModConfigSpec.BooleanValue debugLogging;

        Common(ModConfigSpec.Builder builder) {
            builder.comment("Where wild herbs generate and how often.").push("worldgen");
            enableWildHerbs = builder.comment("Generate wild herbs in the world.").define("enable_wild_herbs", true);
            herbSpawnRate = builder.comment("Herb spawn rate multiplier.").defineInRange("herb_spawn_rate", 1.0D, 0.0D, 8.0D);
            rareHerbSpawnRate = builder.comment("Extra multiplier applied to rare and very rare herbs.")
                    .defineInRange("rare_herb_spawn_rate", 1.0D, 0.0D, 8.0D);
            builder.pop();

            builder.comment("How herbs grow once planted.").push("growth");
            plantGrowthSpeed = builder.comment("Plant growth speed multiplier.")
                    .defineInRange("plant_growth_speed", 1.0D, 0.05D, 10.0D);
            regrowth = builder.comment("Plants regrow after harvesting instead of being destroyed.")
                    .define("regrowth", true);
            seasonalGrowth = builder.comment("Herbs respond to seasons.").define("seasonal_growth", true);
            builder.pop();

            builder.comment("Season tracking and its effect on the world.").push("season");
            seasonProvider = builder.comment("Season source (INTERNAL, TOUGH_AS_NAILS, SERENE_SEASONS or AUTO).")
                    .defineEnum("provider", SeasonSource.AUTO);
            seasonLengthDays = builder.comment("Length of one season in days (internal seasons).")
                    .defineInRange("internal_length_days", 24, 1, 365);
            seasonsAffectWorldgen = builder.comment("Seasons affect which herbs spawn.")
                    .define("affects_worldgen", true);
            builder.pop();

            builder.comment("Medicine strength and reuse.").push("medicine");
            medicineEffectiveness = builder.comment("Medicine effectiveness multiplier.")
                    .defineInRange("effectiveness", 1.0D, 0.1D, 4.0D);
            cooldownMultiplier = builder.comment("Medicine cooldown multiplier.")
                    .defineInRange("cooldown_multiplier", 1.0D, 0.0D, 8.0D);
            freshnessEffect = builder.comment("Fresh herbs are stronger than dried.")
                    .define("freshness_effect", true);
            builder.pop();

            builder.comment("Accumulated toxicity.").push("toxicity");
            toxicityEnabled = builder.comment("Toxic medicine builds up in the body.").define("enabled", true);
            toxicityMultiplier = builder.comment("Toxin buildup multiplier.")
                    .defineInRange("multiplier", 1.0D, 0.0D, 8.0D);
            toxinDecayPerMinute = builder.comment("Toxin decay per minute.")
                    .defineInRange("decay_rate", 2, 0, 200);
            builder.pop();

            builder.comment("Shelf life of harvested herbs.").push("freshness");
            freshnessEnabled = builder.comment("Fresh herbs spoil over time.").define("enabled", true);
            freshDays = builder.comment("Days a fresh herb stays fresh.").defineInRange("days_fresh", 3, 1, 60);
            agingDays = builder.comment("Additional days before a fresh herb spoils.").defineInRange("days_aging", 3, 1, 60);
            dryingTicks = builder.comment("Ticks to dry a herb on a rack.").defineInRange("drying_ticks", 2400, 20, 72000);
            builder.pop();

            builder.comment("Workstation behaviour.").push("crafting");
            experimentation = builder.comment("Allow experimental brewing at the Herbalist's Table.")
                    .define("enable_experimentation", true);
            coatingCharges = builder.comment("Weapon coating charge multiplier.")
                    .defineInRange("coating_charges", 1.0D, 0.1D, 4.0D);
            coatingDuration = builder.comment("Weapon coating duration multiplier.")
                    .defineInRange("coating_duration", 1.0D, 0.1D, 4.0D);
            experimentToxinScale = builder.comment("How dangerous failed experiments are.")
                    .defineInRange("experiment_toxin_scale", 1.0D, 0.0D, 4.0D);
            builder.pop();

            builder.comment("The Herbalist villager.").push("villagers");
            villagersEnabled = builder.comment("Herbalist villagers spawn in villages.").define("enabled", true);
            biomeTrades = builder.comment("Trades vary with the villager's biome.").define("biome_trades", true);
            builder.pop();

            builder.comment("Optional integrations are detected at runtime and never required.")
                    .push("integration");
            toughAsNails = builder.comment("Integrate with Tough As Nails temperature and hydration.")
                    .define("tough_as_nails", true);
            toughAsNailsSeasons = builder.comment("Use Tough As Nails seasons if present.")
                    .define("tough_as_nails_seasons", true);
            sereneSeasons = builder.comment("Use Serene Seasons if present.").define("serene_seasons", true);
            debugLogging = builder.comment("Log integration detection details.").define("debug_logging", false);
            builder.pop();
        }
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue journalAnimations;
        public final ModConfigSpec.BooleanValue showPropertyIcons;
        public final ModConfigSpec.BooleanValue showHudIndicators;

        Client(ModConfigSpec.Builder builder) {
            builder.comment("Client-side presentation options.").push("client");
            journalAnimations = builder.comment("Animate journal page turns.").define("journal_animations", true);
            showPropertyIcons = builder.comment("Show property icons in tooltips.")
                    .define("show_property_icons", true);
            showHudIndicators = builder.comment("Show toxin and temperature indicators on the HUD.")
                    .define("show_hud_indicators", true);
            builder.pop();
        }
    }

    /** Where season information comes from. */
    public enum SeasonSource {
        INTERNAL,
        TOUGH_AS_NAILS,
        SERENE_SEASONS,
        AUTO
    }

    // ---- accessors used by gameplay code ------------------------------------

    public static boolean spawnWildHerbs() {
        return bool(COMMON.enableWildHerbs, true);
    }

    public static double herbSpawnRate() {
        return number(COMMON.herbSpawnRate, 1.0D);
    }

    public static double rareHerbSpawnRate() {
        return number(COMMON.rareHerbSpawnRate, 1.0D);
    }

    public static double growthSpeed() {
        return number(COMMON.plantGrowthSpeed, 1.0D);
    }

    public static boolean regrowth() {
        return bool(COMMON.regrowth, true);
    }

    public static boolean seasonalGrowth() {
        return bool(COMMON.seasonalGrowth, true);
    }

    public static SeasonSource seasonSource() {
        return select(COMMON.seasonProvider, SeasonSource.AUTO);
    }

    public static int seasonLengthDays() {
        return integer(COMMON.seasonLengthDays, 24);
    }

    public static boolean seasonsAffectWorldgen() {
        return bool(COMMON.seasonsAffectWorldgen, true);
    }

    public static double medicineEffectiveness() {
        return number(COMMON.medicineEffectiveness, 1.0D);
    }

    public static double cooldownMultiplier() {
        return number(COMMON.cooldownMultiplier, 1.0D);
    }

    public static boolean freshnessEffect() {
        return bool(COMMON.freshnessEffect, true);
    }

    public static boolean toxicityEnabled() {
        return bool(COMMON.toxicityEnabled, true);
    }

    public static double toxicityMultiplier() {
        return number(COMMON.toxicityMultiplier, 1.0D);
    }

    public static int toxinDecayPerMinute() {
        return integer(COMMON.toxinDecayPerMinute, 2);
    }

    public static boolean freshnessEnabled() {
        return bool(COMMON.freshnessEnabled, true);
    }

    public static int freshDays() {
        return integer(COMMON.freshDays, 3);
    }

    public static int agingDays() {
        return integer(COMMON.agingDays, 3);
    }

    public static int dryingTicks() {
        return integer(COMMON.dryingTicks, 2400);
    }

    public static boolean experimentation() {
        return bool(COMMON.experimentation, true);
    }

    public static double coatingCharges() {
        return number(COMMON.coatingCharges, 1.0D);
    }

    public static double coatingDuration() {
        return number(COMMON.coatingDuration, 1.0D);
    }

    public static double experimentToxinScale() {
        return number(COMMON.experimentToxinScale, 1.0D);
    }

    public static boolean villagersEnabled() {
        return bool(COMMON.villagersEnabled, true);
    }

    public static boolean biomeTrades() {
        return bool(COMMON.biomeTrades, true);
    }

    public static boolean toughAsNails() {
        return bool(COMMON.toughAsNails, true);
    }

    public static boolean toughAsNailsSeasons() {
        return bool(COMMON.toughAsNailsSeasons, true);
    }

    public static boolean sereneSeasons() {
        return bool(COMMON.sereneSeasons, true);
    }

    public static boolean debugLogging() {
        return bool(COMMON.debugLogging, false);
    }

    public static boolean showPropertyIcons() {
        return bool(CLIENT.showPropertyIcons, true);
    }

    public static boolean showHudIndicators() {
        return bool(CLIENT.showHudIndicators, true);
    }

    public static boolean journalAnimations() {
        return bool(CLIENT.journalAnimations, true);
    }
}
