/*
 * GENERATED FILE - do not edit by hand.
 * Produced by tools/gen/java.py from tools/content/*.json; run `python3 tools/generate.py`.
 */
package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.block.DryingRackBlock;
import com.herbalistscraft.block.HerbalMillBlock;
import com.herbalistscraft.block.HerbalistTableBlock;
import com.herbalistscraft.block.MortarBlock;
import com.herbalistscraft.herb.HerbCropBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Herb crops and the four workstations, plus their block items. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HerbalistsCraft.MODID);

    private ModBlocks() {}

    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
    public static final DeferredBlock<HerbCropBlock> BLOODROOT_CROP = BLOCKS.registerBlock("bloodroot_crop",
            p -> new HerbCropBlock(p, ModHerbs.BLOODROOT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> HEARTLEAF_CROP = BLOCKS.registerBlock("heartleaf_crop",
            p -> new HerbCropBlock(p, ModHerbs.HEARTLEAF), cropProperties());
    public static final DeferredBlock<HerbCropBlock> RED_CLOVER_CROP = BLOCKS.registerBlock("red_clover_crop",
            p -> new HerbCropBlock(p, ModHerbs.RED_CLOVER), cropProperties());
    public static final DeferredBlock<HerbCropBlock> VITALIS_ROOT_CROP = BLOCKS.registerBlock("vitalis_root_crop",
            p -> new HerbCropBlock(p, ModHerbs.VITALIS_ROOT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> YARROW_CROP = BLOCKS.registerBlock("yarrow_crop",
            p -> new HerbCropBlock(p, ModHerbs.YARROW), cropProperties());
    public static final DeferredBlock<HerbCropBlock> COMFREY_CROP = BLOCKS.registerBlock("comfrey_crop",
            p -> new HerbCropBlock(p, ModHerbs.COMFREY), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SILVERLEAF_CROP = BLOCKS.registerBlock("silverleaf_crop",
            p -> new HerbCropBlock(p, ModHerbs.SILVERLEAF), cropProperties());
    public static final DeferredBlock<HerbCropBlock> FROSTMINT_CROP = BLOCKS.registerBlock("frostmint_crop",
            p -> new HerbCropBlock(p, ModHerbs.FROSTMINT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SNOWBELL_CROP = BLOCKS.registerBlock("snowbell_crop",
            p -> new HerbCropBlock(p, ModHerbs.SNOWBELL), cropProperties());
    public static final DeferredBlock<HerbCropBlock> ICELEAF_CROP = BLOCKS.registerBlock("iceleaf_crop",
            p -> new HerbCropBlock(p, ModHerbs.ICELEAF), cropProperties());
    public static final DeferredBlock<HerbCropBlock> WINTER_SAGE_CROP = BLOCKS.registerBlock("winter_sage_crop",
            p -> new HerbCropBlock(p, ModHerbs.WINTER_SAGE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> GLACIER_LOTUS_CROP = BLOCKS.registerBlock("glacier_lotus_crop",
            p -> new HerbCropBlock(p, ModHerbs.GLACIER_LOTUS), cropProperties());
    public static final DeferredBlock<HerbCropBlock> MOONFROST_BERRY_CROP = BLOCKS.registerBlock("moonfrost_berry_crop",
            p -> new HerbCropBlock(p, ModHerbs.MOONFROST_BERRY), cropProperties());
    public static final DeferredBlock<HerbCropBlock> EMBERROOT_CROP = BLOCKS.registerBlock("emberroot_crop",
            p -> new HerbCropBlock(p, ModHerbs.EMBERROOT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SUNLEAF_CROP = BLOCKS.registerBlock("sunleaf_crop",
            p -> new HerbCropBlock(p, ModHerbs.SUNLEAF), cropProperties());
    public static final DeferredBlock<HerbCropBlock> CINNAMON_BARK_CROP = BLOCKS.registerBlock("cinnamon_bark_crop",
            p -> new HerbCropBlock(p, ModHerbs.CINNAMON_BARK), cropProperties());
    public static final DeferredBlock<HerbCropBlock> FIRE_SAGE_CROP = BLOCKS.registerBlock("fire_sage_crop",
            p -> new HerbCropBlock(p, ModHerbs.FIRE_SAGE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> BLAZE_PEPPER_CROP = BLOCKS.registerBlock("blaze_pepper_crop",
            p -> new HerbCropBlock(p, ModHerbs.BLAZE_PEPPER), cropProperties());
    public static final DeferredBlock<HerbCropBlock> CINDER_BLOOM_CROP = BLOCKS.registerBlock("cinder_bloom_crop",
            p -> new HerbCropBlock(p, ModHerbs.CINDER_BLOOM), cropProperties());
    public static final DeferredBlock<HerbCropBlock> NIGHTSHADE_CROP = BLOCKS.registerBlock("nightshade_crop",
            p -> new HerbCropBlock(p, ModHerbs.NIGHTSHADE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> VIPERWEED_CROP = BLOCKS.registerBlock("viperweed_crop",
            p -> new HerbCropBlock(p, ModHerbs.VIPERWEED), cropProperties());
    public static final DeferredBlock<HerbCropBlock> BLACKTHORN_CROP = BLOCKS.registerBlock("blackthorn_crop",
            p -> new HerbCropBlock(p, ModHerbs.BLACKTHORN), cropProperties());
    public static final DeferredBlock<HerbCropBlock> DEADLY_CAP_CROP = BLOCKS.registerBlock("deadly_cap_crop",
            p -> new HerbCropBlock(p, ModHerbs.DEADLY_CAP), cropProperties());
    public static final DeferredBlock<HerbCropBlock> WOLFSBANE_CROP = BLOCKS.registerBlock("wolfsbane_crop",
            p -> new HerbCropBlock(p, ModHerbs.WOLFSBANE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> HEMLOCK_CROP = BLOCKS.registerBlock("hemlock_crop",
            p -> new HerbCropBlock(p, ModHerbs.HEMLOCK), cropProperties());
    public static final DeferredBlock<HerbCropBlock> GRAVE_MOSS_CROP = BLOCKS.registerBlock("grave_moss_crop",
            p -> new HerbCropBlock(p, ModHerbs.GRAVE_MOSS), cropProperties());
    public static final DeferredBlock<HerbCropBlock> CLEANSAGE_CROP = BLOCKS.registerBlock("cleansage_crop",
            p -> new HerbCropBlock(p, ModHerbs.CLEANSAGE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> BITTERROOT_CROP = BLOCKS.registerBlock("bitterroot_crop",
            p -> new HerbCropBlock(p, ModHerbs.BITTERROOT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> GOLDEN_CHAMOMILE_CROP = BLOCKS.registerBlock("golden_chamomile_crop",
            p -> new HerbCropBlock(p, ModHerbs.GOLDEN_CHAMOMILE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> MARSHROOT_CROP = BLOCKS.registerBlock("marshroot_crop",
            p -> new HerbCropBlock(p, ModHerbs.MARSHROOT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> WITCH_HAZEL_CROP = BLOCKS.registerBlock("witch_hazel_crop",
            p -> new HerbCropBlock(p, ModHerbs.WITCH_HAZEL), cropProperties());
    public static final DeferredBlock<HerbCropBlock> FEVERFEW_CROP = BLOCKS.registerBlock("feverfew_crop",
            p -> new HerbCropBlock(p, ModHerbs.FEVERFEW), cropProperties());
    public static final DeferredBlock<HerbCropBlock> LUNGWORT_CROP = BLOCKS.registerBlock("lungwort_crop",
            p -> new HerbCropBlock(p, ModHerbs.LUNGWORT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SPIRITWOOD_CROP = BLOCKS.registerBlock("spiritwood_crop",
            p -> new HerbCropBlock(p, ModHerbs.SPIRITWOOD), cropProperties());
    public static final DeferredBlock<HerbCropBlock> STORMHERB_CROP = BLOCKS.registerBlock("stormherb_crop",
            p -> new HerbCropBlock(p, ModHerbs.STORMHERB), cropProperties());
    public static final DeferredBlock<HerbCropBlock> WILD_GINSENG_CROP = BLOCKS.registerBlock("wild_ginseng_crop",
            p -> new HerbCropBlock(p, ModHerbs.WILD_GINSENG), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SWIFTLEAF_CROP = BLOCKS.registerBlock("swiftleaf_crop",
            p -> new HerbCropBlock(p, ModHerbs.SWIFTLEAF), cropProperties());
    public static final DeferredBlock<HerbCropBlock> DAWNBLOOM_CROP = BLOCKS.registerBlock("dawnbloom_crop",
            p -> new HerbCropBlock(p, ModHerbs.DAWNBLOOM), cropProperties());
    public static final DeferredBlock<HerbCropBlock> THUNDER_THISTLE_CROP = BLOCKS.registerBlock("thunder_thistle_crop",
            p -> new HerbCropBlock(p, ModHerbs.THUNDER_THISTLE), cropProperties());
    public static final DeferredBlock<HerbCropBlock> DREAMCAP_CROP = BLOCKS.registerBlock("dreamcap_crop",
            p -> new HerbCropBlock(p, ModHerbs.DREAMCAP), cropProperties());
    public static final DeferredBlock<HerbCropBlock> MOONFLOWER_CROP = BLOCKS.registerBlock("moonflower_crop",
            p -> new HerbCropBlock(p, ModHerbs.MOONFLOWER), cropProperties());
    public static final DeferredBlock<HerbCropBlock> LAVENDER_CROP = BLOCKS.registerBlock("lavender_crop",
            p -> new HerbCropBlock(p, ModHerbs.LAVENDER), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SLEEPWORT_CROP = BLOCKS.registerBlock("sleepwort_crop",
            p -> new HerbCropBlock(p, ModHerbs.SLEEPWORT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> VELVET_BELL_CROP = BLOCKS.registerBlock("velvet_bell_crop",
            p -> new HerbCropBlock(p, ModHerbs.VELVET_BELL), cropProperties());
    public static final DeferredBlock<HerbCropBlock> IRONBARK_CROP = BLOCKS.registerBlock("ironbark_crop",
            p -> new HerbCropBlock(p, ModHerbs.IRONBARK), cropProperties());
    public static final DeferredBlock<HerbCropBlock> ASHLEAF_CROP = BLOCKS.registerBlock("ashleaf_crop",
            p -> new HerbCropBlock(p, ModHerbs.ASHLEAF), cropProperties());
    public static final DeferredBlock<HerbCropBlock> DRAGONSCALE_HERB_CROP = BLOCKS.registerBlock("dragonscale_herb_crop",
            p -> new HerbCropBlock(p, ModHerbs.DRAGONSCALE_HERB), cropProperties());
    public static final DeferredBlock<HerbCropBlock> WARDROOT_CROP = BLOCKS.registerBlock("wardroot_crop",
            p -> new HerbCropBlock(p, ModHerbs.WARDROOT), cropProperties());
    public static final DeferredBlock<HerbCropBlock> SUNPETAL_CROP = BLOCKS.registerBlock("sunpetal_crop",
            p -> new HerbCropBlock(p, ModHerbs.SUNPETAL), cropProperties());

    public static final DeferredBlock<MortarBlock> MORTAR_AND_PESTLE = BLOCKS.registerBlock("mortar_and_pestle",
            p -> new MortarBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
                    .sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<HerbalMillBlock> HERBAL_MILL = BLOCKS.registerBlock("herbal_mill",
            p -> new HerbalMillBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.5F, 6.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<HerbalistTableBlock> HERBALISTS_TABLE = BLOCKS.registerBlock("herbalists_table",
            p -> new HerbalistTableBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.0F, 4.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = BLOCKS.registerBlock("drying_rack",
            p -> new DryingRackBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(1.2F, 3.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredItem<BlockItem> MORTAR_AND_PESTLE_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("mortar_and_pestle", MORTAR_AND_PESTLE);
    public static final DeferredItem<BlockItem> HERBAL_MILL_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("herbal_mill", HERBAL_MILL);
    public static final DeferredItem<BlockItem> HERBALISTS_TABLE_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("herbalists_table", HERBALISTS_TABLE);
    public static final DeferredItem<BlockItem> DRYING_RACK_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("drying_rack", DRYING_RACK);
}
