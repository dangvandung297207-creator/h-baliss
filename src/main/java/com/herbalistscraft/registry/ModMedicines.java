/*
 * GENERATED FILE - do not edit by hand.
 * Produced by tools/gen/java.py from tools/content/*.json; run `python3 tools/generate.py`.
 */
package com.herbalistscraft.registry;

import com.herbalistscraft.medicine.MedicineDefinition;
import com.herbalistscraft.medicine.MedicineKind;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/** Every medicine id in Herbalist's Craft, as registry keys into the synced medicine datapack registry. */
public final class ModMedicines {
    public static final ResourceKey<Registry<MedicineDefinition>> REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("herbalistscraft", "medicine"));

    private ModMedicines() {}

    public static ResourceKey<MedicineDefinition> key(String path) {
        return ResourceKey.create(REGISTRY, ResourceLocation.fromNamespaceAndPath("herbalistscraft", path));
    }
    public static final ResourceKey<MedicineDefinition> MINOR_HEALING_TONIC = key("minor_healing_tonic"); // Minor Healing Tonic
    public static final ResourceKey<MedicineDefinition> HEALING_TONIC = key("healing_tonic"); // Healing Tonic
    public static final ResourceKey<MedicineDefinition> STRONG_HEALING_TONIC = key("strong_healing_tonic"); // Strong Healing Tonic
    public static final ResourceKey<MedicineDefinition> HEMOSTATIC_TONIC = key("hemostatic_tonic"); // Hemostatic Tonic
    public static final ResourceKey<MedicineDefinition> ANTISEPTIC_TONIC = key("antiseptic_tonic"); // Antiseptic Tonic
    public static final ResourceKey<MedicineDefinition> ANTIDOTE_TONIC = key("antidote_tonic"); // Antidote Tonic
    public static final ResourceKey<MedicineDefinition> DETOX_TONIC = key("detox_tonic"); // Detox Tonic
    public static final ResourceKey<MedicineDefinition> RESPIRATORY_TONIC = key("respiratory_tonic"); // Respiratory Tonic
    public static final ResourceKey<MedicineDefinition> FROST_LUNG_TONIC = key("frost_lung_tonic"); // Frost Lung Tonic
    public static final ResourceKey<MedicineDefinition> STAMINA_TONIC = key("stamina_tonic"); // Stamina Tonic
    public static final ResourceKey<MedicineDefinition> FOCUS_TONIC = key("focus_tonic"); // Focus Tonic
    public static final ResourceKey<MedicineDefinition> RECUPERATION_TONIC = key("recuperation_tonic"); // Recuperation Tonic
    public static final ResourceKey<MedicineDefinition> VITALIS_TONIC = key("vitalis_tonic"); // Vitalis Tonic
    public static final ResourceKey<MedicineDefinition> EMBER_TONIC = key("ember_tonic"); // Ember Tonic
    public static final ResourceKey<MedicineDefinition> DRAGONSCALE_DRAUGHT = key("dragonscale_draught"); // Dragonscale Draught
    public static final ResourceKey<MedicineDefinition> WARMING_TEA = key("warming_tea"); // Warming Tea
    public static final ResourceKey<MedicineDefinition> COOLING_TEA = key("cooling_tea"); // Cooling Tea
    public static final ResourceKey<MedicineDefinition> HERBAL_TEA = key("herbal_tea"); // Herbal Tea
    public static final ResourceKey<MedicineDefinition> FOCUS_TEA = key("focus_tea"); // Focus Tea
    public static final ResourceKey<MedicineDefinition> SLEEP_TEA = key("sleep_tea"); // Sleep Tea
    public static final ResourceKey<MedicineDefinition> HUSH_TEA = key("hush_tea"); // Hush Tea
    public static final ResourceKey<MedicineDefinition> RECOVERY_TEA = key("recovery_tea"); // Recovery Tea
    public static final ResourceKey<MedicineDefinition> BITTER_TEA = key("bitter_tea"); // Bitter Tea
    public static final ResourceKey<MedicineDefinition> SUN_TEA = key("sun_tea"); // Sun Tea
    public static final ResourceKey<MedicineDefinition> BERRY_TEA = key("berry_tea"); // Sweet Berry Tea
    public static final ResourceKey<MedicineDefinition> FROST_SALVE = key("frost_salve"); // Frost Salve
    public static final ResourceKey<MedicineDefinition> EMBER_SALVE = key("ember_salve"); // Ember Salve
    public static final ResourceKey<MedicineDefinition> BURN_SALVE = key("burn_salve"); // Burn Salve
    public static final ResourceKey<MedicineDefinition> ANTISEPTIC_SALVE = key("antiseptic_salve"); // Antiseptic Salve
    public static final ResourceKey<MedicineDefinition> HEALING_SALVE = key("healing_salve"); // Healing Salve
    public static final ResourceKey<MedicineDefinition> WARD_SALVE = key("ward_salve"); // Ward Salve
    public static final ResourceKey<MedicineDefinition> TOXIC_OIL = key("toxic_oil"); // Toxic Oil
    public static final ResourceKey<MedicineDefinition> POISON_EXTRACT = key("poison_extract"); // Poison Extract
    public static final ResourceKey<MedicineDefinition> BLEEDING_OIL = key("bleeding_oil"); // Bleeding Oil
    public static final ResourceKey<MedicineDefinition> WEAKENING_OIL = key("weakening_oil"); // Weakening Oil
    public static final ResourceKey<MedicineDefinition> SLOWING_OIL = key("slowing_oil"); // Slowing Oil
    public static final ResourceKey<MedicineDefinition> VENOM_OIL = key("venom_oil"); // Venom Oil

    public static final List<ResourceKey<MedicineDefinition>> ALL = List.of(
            MINOR_HEALING_TONIC,
            HEALING_TONIC,
            STRONG_HEALING_TONIC,
            HEMOSTATIC_TONIC,
            ANTISEPTIC_TONIC,
            ANTIDOTE_TONIC,
            DETOX_TONIC,
            RESPIRATORY_TONIC,
            FROST_LUNG_TONIC,
            STAMINA_TONIC,
            FOCUS_TONIC,
            RECUPERATION_TONIC,
            VITALIS_TONIC,
            EMBER_TONIC,
            DRAGONSCALE_DRAUGHT,
            WARMING_TEA,
            COOLING_TEA,
            HERBAL_TEA,
            FOCUS_TEA,
            SLEEP_TEA,
            HUSH_TEA,
            RECOVERY_TEA,
            BITTER_TEA,
            SUN_TEA,
            BERRY_TEA,
            FROST_SALVE,
            EMBER_SALVE,
            BURN_SALVE,
            ANTISEPTIC_SALVE,
            HEALING_SALVE,
            WARD_SALVE,
            TOXIC_OIL,
            POISON_EXTRACT,
            BLEEDING_OIL,
            WEAKENING_OIL,
            SLOWING_OIL,
            VENOM_OIL
    );

    /** Medicines grouped by kind, used by the journal and the trades. */
    public static final Map<MedicineKind, List<ResourceKey<MedicineDefinition>>> BY_KIND = Map.ofEntries(
            Map.entry(MedicineKind.TONIC, List.of(MINOR_HEALING_TONIC, HEALING_TONIC, STRONG_HEALING_TONIC, HEMOSTATIC_TONIC, ANTISEPTIC_TONIC, ANTIDOTE_TONIC, DETOX_TONIC, RESPIRATORY_TONIC, FROST_LUNG_TONIC, STAMINA_TONIC, FOCUS_TONIC, RECUPERATION_TONIC, VITALIS_TONIC, EMBER_TONIC, DRAGONSCALE_DRAUGHT)),
            Map.entry(MedicineKind.TEA, List.of(WARMING_TEA, COOLING_TEA, HERBAL_TEA, FOCUS_TEA, SLEEP_TEA, HUSH_TEA, RECOVERY_TEA, BITTER_TEA, SUN_TEA, BERRY_TEA)),
            Map.entry(MedicineKind.SALVE, List.of(FROST_SALVE, EMBER_SALVE, BURN_SALVE, ANTISEPTIC_SALVE, HEALING_SALVE, WARD_SALVE)),
            Map.entry(MedicineKind.OIL, List.of(TOXIC_OIL, POISON_EXTRACT, BLEEDING_OIL, WEAKENING_OIL, SLOWING_OIL, VENOM_OIL))
    );
}
