/*
 * GENERATED FILE - do not edit by hand.
 * Produced by tools/gen/java.py from tools/content/*.json; run `python3 tools/generate.py`.
 */
package com.herbalistscraft.registry;

import com.herbalistscraft.herb.HerbCategory;
import com.herbalistscraft.herb.HerbDefinition;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/** Every herb id in Herbalist's Craft, as registry keys into the synced herb datapack registry. */
public final class ModHerbs {
    public static final ResourceKey<Registry<HerbDefinition>> REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("herbalistscraft", "herb"));

    private ModHerbs() {}

    public static ResourceKey<HerbDefinition> key(String path) {
        return ResourceKey.create(REGISTRY, ResourceLocation.fromNamespaceAndPath("herbalistscraft", path));
    }
    public static final ResourceKey<HerbDefinition> BLOODROOT = key("bloodroot"); // Bloodroot
    public static final ResourceKey<HerbDefinition> HEARTLEAF = key("heartleaf"); // Heartleaf
    public static final ResourceKey<HerbDefinition> RED_CLOVER = key("red_clover"); // Red Clover
    public static final ResourceKey<HerbDefinition> VITALIS_ROOT = key("vitalis_root"); // Vitalis Root
    public static final ResourceKey<HerbDefinition> YARROW = key("yarrow"); // Yarrow
    public static final ResourceKey<HerbDefinition> COMFREY = key("comfrey"); // Comfrey
    public static final ResourceKey<HerbDefinition> SILVERLEAF = key("silverleaf"); // Silverleaf
    public static final ResourceKey<HerbDefinition> FROSTMINT = key("frostmint"); // Frostmint
    public static final ResourceKey<HerbDefinition> SNOWBELL = key("snowbell"); // Snowbell
    public static final ResourceKey<HerbDefinition> ICELEAF = key("iceleaf"); // Iceleaf
    public static final ResourceKey<HerbDefinition> WINTER_SAGE = key("winter_sage"); // Winter Sage
    public static final ResourceKey<HerbDefinition> GLACIER_LOTUS = key("glacier_lotus"); // Glacier Lotus
    public static final ResourceKey<HerbDefinition> MOONFROST_BERRY = key("moonfrost_berry"); // Moonfrost Berry
    public static final ResourceKey<HerbDefinition> EMBERROOT = key("emberroot"); // Emberroot
    public static final ResourceKey<HerbDefinition> SUNLEAF = key("sunleaf"); // Sunleaf
    public static final ResourceKey<HerbDefinition> CINNAMON_BARK = key("cinnamon_bark"); // Cinnamon Bark
    public static final ResourceKey<HerbDefinition> FIRE_SAGE = key("fire_sage"); // Fire Sage
    public static final ResourceKey<HerbDefinition> BLAZE_PEPPER = key("blaze_pepper"); // Blaze Pepper
    public static final ResourceKey<HerbDefinition> CINDER_BLOOM = key("cinder_bloom"); // Cinder Bloom
    public static final ResourceKey<HerbDefinition> NIGHTSHADE = key("nightshade"); // Nightshade
    public static final ResourceKey<HerbDefinition> VIPERWEED = key("viperweed"); // Viperweed
    public static final ResourceKey<HerbDefinition> BLACKTHORN = key("blackthorn"); // Blackthorn
    public static final ResourceKey<HerbDefinition> DEADLY_CAP = key("deadly_cap"); // Deadly Cap
    public static final ResourceKey<HerbDefinition> WOLFSBANE = key("wolfsbane"); // Wolfsbane
    public static final ResourceKey<HerbDefinition> HEMLOCK = key("hemlock"); // Hemlock
    public static final ResourceKey<HerbDefinition> GRAVE_MOSS = key("grave_moss"); // Grave Moss
    public static final ResourceKey<HerbDefinition> CLEANSAGE = key("cleansage"); // Cleansage
    public static final ResourceKey<HerbDefinition> BITTERROOT = key("bitterroot"); // Bitterroot
    public static final ResourceKey<HerbDefinition> GOLDEN_CHAMOMILE = key("golden_chamomile"); // Golden Chamomile
    public static final ResourceKey<HerbDefinition> MARSHROOT = key("marshroot"); // Marshroot
    public static final ResourceKey<HerbDefinition> WITCH_HAZEL = key("witch_hazel"); // Witch Hazel
    public static final ResourceKey<HerbDefinition> FEVERFEW = key("feverfew"); // Feverfew
    public static final ResourceKey<HerbDefinition> LUNGWORT = key("lungwort"); // Lungwort
    public static final ResourceKey<HerbDefinition> SPIRITWOOD = key("spiritwood"); // Spiritwood
    public static final ResourceKey<HerbDefinition> STORMHERB = key("stormherb"); // Stormherb
    public static final ResourceKey<HerbDefinition> WILD_GINSENG = key("wild_ginseng"); // Wild Ginseng
    public static final ResourceKey<HerbDefinition> SWIFTLEAF = key("swiftleaf"); // Swiftleaf
    public static final ResourceKey<HerbDefinition> DAWNBLOOM = key("dawnbloom"); // Dawnbloom
    public static final ResourceKey<HerbDefinition> THUNDER_THISTLE = key("thunder_thistle"); // Thunder Thistle
    public static final ResourceKey<HerbDefinition> DREAMCAP = key("dreamcap"); // Dreamcap
    public static final ResourceKey<HerbDefinition> MOONFLOWER = key("moonflower"); // Moonflower
    public static final ResourceKey<HerbDefinition> LAVENDER = key("lavender"); // Lavender
    public static final ResourceKey<HerbDefinition> SLEEPWORT = key("sleepwort"); // Sleepwort
    public static final ResourceKey<HerbDefinition> VELVET_BELL = key("velvet_bell"); // Velvet Bell
    public static final ResourceKey<HerbDefinition> IRONBARK = key("ironbark"); // Ironbark
    public static final ResourceKey<HerbDefinition> ASHLEAF = key("ashleaf"); // Ashleaf
    public static final ResourceKey<HerbDefinition> DRAGONSCALE_HERB = key("dragonscale_herb"); // Dragonscale Herb
    public static final ResourceKey<HerbDefinition> WARDROOT = key("wardroot"); // Wardroot
    public static final ResourceKey<HerbDefinition> SUNPETAL = key("sunpetal"); // Sunpetal

    /** All herbs, in content-file order. */
    public static final List<ResourceKey<HerbDefinition>> ALL = List.of(
            BLOODROOT,
            HEARTLEAF,
            RED_CLOVER,
            VITALIS_ROOT,
            YARROW,
            COMFREY,
            SILVERLEAF,
            FROSTMINT,
            SNOWBELL,
            ICELEAF,
            WINTER_SAGE,
            GLACIER_LOTUS,
            MOONFROST_BERRY,
            EMBERROOT,
            SUNLEAF,
            CINNAMON_BARK,
            FIRE_SAGE,
            BLAZE_PEPPER,
            CINDER_BLOOM,
            NIGHTSHADE,
            VIPERWEED,
            BLACKTHORN,
            DEADLY_CAP,
            WOLFSBANE,
            HEMLOCK,
            GRAVE_MOSS,
            CLEANSAGE,
            BITTERROOT,
            GOLDEN_CHAMOMILE,
            MARSHROOT,
            WITCH_HAZEL,
            FEVERFEW,
            LUNGWORT,
            SPIRITWOOD,
            STORMHERB,
            WILD_GINSENG,
            SWIFTLEAF,
            DAWNBLOOM,
            THUNDER_THISTLE,
            DREAMCAP,
            MOONFLOWER,
            LAVENDER,
            SLEEPWORT,
            VELVET_BELL,
            IRONBARK,
            ASHLEAF,
            DRAGONSCALE_HERB,
            WARDROOT,
            SUNPETAL
    );

    /** Herbs grouped by category, used for the journal and the creative tab. */
    public static final Map<HerbCategory, List<ResourceKey<HerbDefinition>>> BY_CATEGORY = Map.ofEntries(
            Map.entry(HerbCategory.HEALING, List.of(BLOODROOT, HEARTLEAF, RED_CLOVER, VITALIS_ROOT, YARROW, COMFREY, SILVERLEAF)),
            Map.entry(HerbCategory.COOLING, List.of(FROSTMINT, SNOWBELL, ICELEAF, WINTER_SAGE, GLACIER_LOTUS, MOONFROST_BERRY)),
            Map.entry(HerbCategory.WARMING, List.of(EMBERROOT, SUNLEAF, CINNAMON_BARK, FIRE_SAGE, BLAZE_PEPPER, CINDER_BLOOM)),
            Map.entry(HerbCategory.TOXIC, List.of(NIGHTSHADE, VIPERWEED, BLACKTHORN, DEADLY_CAP, WOLFSBANE, HEMLOCK, GRAVE_MOSS)),
            Map.entry(HerbCategory.MEDICINAL, List.of(CLEANSAGE, BITTERROOT, GOLDEN_CHAMOMILE, MARSHROOT, WITCH_HAZEL, FEVERFEW, LUNGWORT, SPIRITWOOD)),
            Map.entry(HerbCategory.STIMULANT, List.of(STORMHERB, WILD_GINSENG, SWIFTLEAF, DAWNBLOOM, THUNDER_THISTLE)),
            Map.entry(HerbCategory.SEDATIVE, List.of(DREAMCAP, MOONFLOWER, LAVENDER, SLEEPWORT, VELVET_BELL)),
            Map.entry(HerbCategory.PROTECTIVE, List.of(IRONBARK, ASHLEAF, DRAGONSCALE_HERB, WARDROOT, SUNPETAL))
    );
}
