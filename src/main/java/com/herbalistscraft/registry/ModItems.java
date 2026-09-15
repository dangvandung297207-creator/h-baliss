/*
 * GENERATED FILE - do not edit by hand.
 * Produced by tools/gen/java.py from tools/content/*.json; run `python3 tools/generate.py`.
 */
package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbForm;
import com.herbalistscraft.herb.HerbItem;
import com.herbalistscraft.herb.HerbSeedItem;
import com.herbalistscraft.knowledge.JournalItem;
import com.herbalistscraft.knowledge.JournalPageItem;
import com.herbalistscraft.knowledge.JournalPageKind;
import com.herbalistscraft.medicine.ExperimentalTeaItem;
import com.herbalistscraft.medicine.ExperimentalTonicItem;
import com.herbalistscraft.medicine.FailedMixtureItem;
import com.herbalistscraft.medicine.SalveItem;
import com.herbalistscraft.medicine.TeaItem;
import com.herbalistscraft.medicine.TonicItem;
import com.herbalistscraft.medicine.WeaponOilItem;
import com.herbalistscraft.tool.FillTarget;
import com.herbalistscraft.tool.FillableContainerItem;
import com.herbalistscraft.tool.PruningShearsItem;
import com.herbalistscraft.tool.SeedPouchItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Every item in Herbalist's Craft. Workstation block items live in {@link ModBlocks}. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HerbalistsCraft.MODID);

    private ModItems() {}
    public static final DeferredItem<Item> BLOODROOT = ITEMS.registerItem("bloodroot",
            p -> new HerbItem(p, ModHerbs.BLOODROOT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> BLOODROOT_SEEDS = ITEMS.registerItem("bloodroot_seeds",
            p -> new HerbSeedItem(p, ModHerbs.BLOODROOT), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_BLOODROOT = ITEMS.registerItem("dried_bloodroot",
            p -> new HerbItem(p, ModHerbs.BLOODROOT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> BLOODROOT_POWDER = ITEMS.registerItem("bloodroot_powder",
            p -> new HerbItem(p, ModHerbs.BLOODROOT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> BLOODROOT_EXTRACT = ITEMS.registerItem("bloodroot_extract",
            p -> new HerbItem(p, ModHerbs.BLOODROOT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> HEARTLEAF = ITEMS.registerItem("heartleaf",
            p -> new HerbItem(p, ModHerbs.HEARTLEAF, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HEARTLEAF_SEEDS = ITEMS.registerItem("heartleaf_seeds",
            p -> new HerbSeedItem(p, ModHerbs.HEARTLEAF), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_HEARTLEAF = ITEMS.registerItem("dried_heartleaf",
            p -> new HerbItem(p, ModHerbs.HEARTLEAF, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HEARTLEAF_POWDER = ITEMS.registerItem("heartleaf_powder",
            p -> new HerbItem(p, ModHerbs.HEARTLEAF, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HEARTLEAF_EXTRACT = ITEMS.registerItem("heartleaf_extract",
            p -> new HerbItem(p, ModHerbs.HEARTLEAF, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> RED_CLOVER = ITEMS.registerItem("red_clover",
            p -> new HerbItem(p, ModHerbs.RED_CLOVER, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> RED_CLOVER_SEEDS = ITEMS.registerItem("red_clover_seeds",
            p -> new HerbSeedItem(p, ModHerbs.RED_CLOVER), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_RED_CLOVER = ITEMS.registerItem("dried_red_clover",
            p -> new HerbItem(p, ModHerbs.RED_CLOVER, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> RED_CLOVER_POWDER = ITEMS.registerItem("red_clover_powder",
            p -> new HerbItem(p, ModHerbs.RED_CLOVER, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> RED_CLOVER_EXTRACT = ITEMS.registerItem("red_clover_extract",
            p -> new HerbItem(p, ModHerbs.RED_CLOVER, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> VITALIS_ROOT = ITEMS.registerItem("vitalis_root",
            p -> new HerbItem(p, ModHerbs.VITALIS_ROOT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> VITALIS_ROOT_SEEDS = ITEMS.registerItem("vitalis_root_seeds",
            p -> new HerbSeedItem(p, ModHerbs.VITALIS_ROOT), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_VITALIS_ROOT = ITEMS.registerItem("dried_vitalis_root",
            p -> new HerbItem(p, ModHerbs.VITALIS_ROOT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> VITALIS_ROOT_POWDER = ITEMS.registerItem("vitalis_root_powder",
            p -> new HerbItem(p, ModHerbs.VITALIS_ROOT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> VITALIS_ROOT_EXTRACT = ITEMS.registerItem("vitalis_root_extract",
            p -> new HerbItem(p, ModHerbs.VITALIS_ROOT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> YARROW = ITEMS.registerItem("yarrow",
            p -> new HerbItem(p, ModHerbs.YARROW, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> YARROW_SEEDS = ITEMS.registerItem("yarrow_seeds",
            p -> new HerbSeedItem(p, ModHerbs.YARROW), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_YARROW = ITEMS.registerItem("dried_yarrow",
            p -> new HerbItem(p, ModHerbs.YARROW, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> YARROW_POWDER = ITEMS.registerItem("yarrow_powder",
            p -> new HerbItem(p, ModHerbs.YARROW, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> YARROW_EXTRACT = ITEMS.registerItem("yarrow_extract",
            p -> new HerbItem(p, ModHerbs.YARROW, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> COMFREY = ITEMS.registerItem("comfrey",
            p -> new HerbItem(p, ModHerbs.COMFREY, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> COMFREY_SEEDS = ITEMS.registerItem("comfrey_seeds",
            p -> new HerbSeedItem(p, ModHerbs.COMFREY), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_COMFREY = ITEMS.registerItem("dried_comfrey",
            p -> new HerbItem(p, ModHerbs.COMFREY, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> COMFREY_POWDER = ITEMS.registerItem("comfrey_powder",
            p -> new HerbItem(p, ModHerbs.COMFREY, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> COMFREY_EXTRACT = ITEMS.registerItem("comfrey_extract",
            p -> new HerbItem(p, ModHerbs.COMFREY, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> SILVERLEAF = ITEMS.registerItem("silverleaf",
            p -> new HerbItem(p, ModHerbs.SILVERLEAF, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SILVERLEAF_SEEDS = ITEMS.registerItem("silverleaf_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SILVERLEAF), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_SILVERLEAF = ITEMS.registerItem("dried_silverleaf",
            p -> new HerbItem(p, ModHerbs.SILVERLEAF, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SILVERLEAF_POWDER = ITEMS.registerItem("silverleaf_powder",
            p -> new HerbItem(p, ModHerbs.SILVERLEAF, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SILVERLEAF_EXTRACT = ITEMS.registerItem("silverleaf_extract",
            p -> new HerbItem(p, ModHerbs.SILVERLEAF, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> FROSTMINT = ITEMS.registerItem("frostmint",
            p -> new HerbItem(p, ModHerbs.FROSTMINT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> FROSTMINT_SEEDS = ITEMS.registerItem("frostmint_seeds",
            p -> new HerbSeedItem(p, ModHerbs.FROSTMINT), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_FROSTMINT = ITEMS.registerItem("dried_frostmint",
            p -> new HerbItem(p, ModHerbs.FROSTMINT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> FROSTMINT_POWDER = ITEMS.registerItem("frostmint_powder",
            p -> new HerbItem(p, ModHerbs.FROSTMINT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> FROSTMINT_EXTRACT = ITEMS.registerItem("frostmint_extract",
            p -> new HerbItem(p, ModHerbs.FROSTMINT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> SNOWBELL = ITEMS.registerItem("snowbell",
            p -> new HerbItem(p, ModHerbs.SNOWBELL, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SNOWBELL_SEEDS = ITEMS.registerItem("snowbell_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SNOWBELL), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_SNOWBELL = ITEMS.registerItem("dried_snowbell",
            p -> new HerbItem(p, ModHerbs.SNOWBELL, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SNOWBELL_POWDER = ITEMS.registerItem("snowbell_powder",
            p -> new HerbItem(p, ModHerbs.SNOWBELL, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SNOWBELL_EXTRACT = ITEMS.registerItem("snowbell_extract",
            p -> new HerbItem(p, ModHerbs.SNOWBELL, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> ICELEAF = ITEMS.registerItem("iceleaf",
            p -> new HerbItem(p, ModHerbs.ICELEAF, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> ICELEAF_SEEDS = ITEMS.registerItem("iceleaf_seeds",
            p -> new HerbSeedItem(p, ModHerbs.ICELEAF), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_ICELEAF = ITEMS.registerItem("dried_iceleaf",
            p -> new HerbItem(p, ModHerbs.ICELEAF, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> ICELEAF_POWDER = ITEMS.registerItem("iceleaf_powder",
            p -> new HerbItem(p, ModHerbs.ICELEAF, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> ICELEAF_EXTRACT = ITEMS.registerItem("iceleaf_extract",
            p -> new HerbItem(p, ModHerbs.ICELEAF, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> WINTER_SAGE = ITEMS.registerItem("winter_sage",
            p -> new HerbItem(p, ModHerbs.WINTER_SAGE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> WINTER_SAGE_SEEDS = ITEMS.registerItem("winter_sage_seeds",
            p -> new HerbSeedItem(p, ModHerbs.WINTER_SAGE), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_WINTER_SAGE = ITEMS.registerItem("dried_winter_sage",
            p -> new HerbItem(p, ModHerbs.WINTER_SAGE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> WINTER_SAGE_POWDER = ITEMS.registerItem("winter_sage_powder",
            p -> new HerbItem(p, ModHerbs.WINTER_SAGE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> WINTER_SAGE_EXTRACT = ITEMS.registerItem("winter_sage_extract",
            p -> new HerbItem(p, ModHerbs.WINTER_SAGE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> GLACIER_LOTUS = ITEMS.registerItem("glacier_lotus",
            p -> new HerbItem(p, ModHerbs.GLACIER_LOTUS, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> GLACIER_LOTUS_SEEDS = ITEMS.registerItem("glacier_lotus_seeds",
            p -> new HerbSeedItem(p, ModHerbs.GLACIER_LOTUS), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> DRIED_GLACIER_LOTUS = ITEMS.registerItem("dried_glacier_lotus",
            p -> new HerbItem(p, ModHerbs.GLACIER_LOTUS, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> GLACIER_LOTUS_EXTRACT = ITEMS.registerItem("glacier_lotus_extract",
            p -> new HerbItem(p, ModHerbs.GLACIER_LOTUS, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> MOONFROST_BERRY = ITEMS.registerItem("moonfrost_berry",
            p -> new HerbItem(p, ModHerbs.MOONFROST_BERRY, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> MOONFROST_BERRY_SEEDS = ITEMS.registerItem("moonfrost_berry_seeds",
            p -> new HerbSeedItem(p, ModHerbs.MOONFROST_BERRY), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_MOONFROST_BERRY = ITEMS.registerItem("dried_moonfrost_berry",
            p -> new HerbItem(p, ModHerbs.MOONFROST_BERRY, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> MOONFROST_BERRY_EXTRACT = ITEMS.registerItem("moonfrost_berry_extract",
            p -> new HerbItem(p, ModHerbs.MOONFROST_BERRY, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> EMBERROOT = ITEMS.registerItem("emberroot",
            p -> new HerbItem(p, ModHerbs.EMBERROOT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> EMBERROOT_SEEDS = ITEMS.registerItem("emberroot_seeds",
            p -> new HerbSeedItem(p, ModHerbs.EMBERROOT), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_EMBERROOT = ITEMS.registerItem("dried_emberroot",
            p -> new HerbItem(p, ModHerbs.EMBERROOT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> EMBERROOT_POWDER = ITEMS.registerItem("emberroot_powder",
            p -> new HerbItem(p, ModHerbs.EMBERROOT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> EMBERROOT_EXTRACT = ITEMS.registerItem("emberroot_extract",
            p -> new HerbItem(p, ModHerbs.EMBERROOT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> SUNLEAF = ITEMS.registerItem("sunleaf",
            p -> new HerbItem(p, ModHerbs.SUNLEAF, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> SUNLEAF_SEEDS = ITEMS.registerItem("sunleaf_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SUNLEAF), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_SUNLEAF = ITEMS.registerItem("dried_sunleaf",
            p -> new HerbItem(p, ModHerbs.SUNLEAF, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> SUNLEAF_POWDER = ITEMS.registerItem("sunleaf_powder",
            p -> new HerbItem(p, ModHerbs.SUNLEAF, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> SUNLEAF_EXTRACT = ITEMS.registerItem("sunleaf_extract",
            p -> new HerbItem(p, ModHerbs.SUNLEAF, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> CINNAMON_BARK = ITEMS.registerItem("cinnamon_bark",
            p -> new HerbItem(p, ModHerbs.CINNAMON_BARK, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> CINNAMON_BARK_SEEDS = ITEMS.registerItem("cinnamon_bark_seeds",
            p -> new HerbSeedItem(p, ModHerbs.CINNAMON_BARK), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_CINNAMON_BARK = ITEMS.registerItem("dried_cinnamon_bark",
            p -> new HerbItem(p, ModHerbs.CINNAMON_BARK, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> CINNAMON_BARK_POWDER = ITEMS.registerItem("cinnamon_bark_powder",
            p -> new HerbItem(p, ModHerbs.CINNAMON_BARK, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> FIRE_SAGE = ITEMS.registerItem("fire_sage",
            p -> new HerbItem(p, ModHerbs.FIRE_SAGE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FIRE_SAGE_SEEDS = ITEMS.registerItem("fire_sage_seeds",
            p -> new HerbSeedItem(p, ModHerbs.FIRE_SAGE), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_FIRE_SAGE = ITEMS.registerItem("dried_fire_sage",
            p -> new HerbItem(p, ModHerbs.FIRE_SAGE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FIRE_SAGE_POWDER = ITEMS.registerItem("fire_sage_powder",
            p -> new HerbItem(p, ModHerbs.FIRE_SAGE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FIRE_SAGE_EXTRACT = ITEMS.registerItem("fire_sage_extract",
            p -> new HerbItem(p, ModHerbs.FIRE_SAGE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> BLAZE_PEPPER = ITEMS.registerItem("blaze_pepper",
            p -> new HerbItem(p, ModHerbs.BLAZE_PEPPER, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> BLAZE_PEPPER_SEEDS = ITEMS.registerItem("blaze_pepper_seeds",
            p -> new HerbSeedItem(p, ModHerbs.BLAZE_PEPPER), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_BLAZE_PEPPER = ITEMS.registerItem("dried_blaze_pepper",
            p -> new HerbItem(p, ModHerbs.BLAZE_PEPPER, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> BLAZE_PEPPER_POWDER = ITEMS.registerItem("blaze_pepper_powder",
            p -> new HerbItem(p, ModHerbs.BLAZE_PEPPER, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> BLAZE_PEPPER_EXTRACT = ITEMS.registerItem("blaze_pepper_extract",
            p -> new HerbItem(p, ModHerbs.BLAZE_PEPPER, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> CINDER_BLOOM = ITEMS.registerItem("cinder_bloom",
            p -> new HerbItem(p, ModHerbs.CINDER_BLOOM, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> CINDER_BLOOM_SEEDS = ITEMS.registerItem("cinder_bloom_seeds",
            p -> new HerbSeedItem(p, ModHerbs.CINDER_BLOOM), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_CINDER_BLOOM = ITEMS.registerItem("dried_cinder_bloom",
            p -> new HerbItem(p, ModHerbs.CINDER_BLOOM, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> CINDER_BLOOM_POWDER = ITEMS.registerItem("cinder_bloom_powder",
            p -> new HerbItem(p, ModHerbs.CINDER_BLOOM, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> CINDER_BLOOM_EXTRACT = ITEMS.registerItem("cinder_bloom_extract",
            p -> new HerbItem(p, ModHerbs.CINDER_BLOOM, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> NIGHTSHADE = ITEMS.registerItem("nightshade",
            p -> new HerbItem(p, ModHerbs.NIGHTSHADE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> NIGHTSHADE_SEEDS = ITEMS.registerItem("nightshade_seeds",
            p -> new HerbSeedItem(p, ModHerbs.NIGHTSHADE), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_NIGHTSHADE = ITEMS.registerItem("dried_nightshade",
            p -> new HerbItem(p, ModHerbs.NIGHTSHADE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> NIGHTSHADE_POWDER = ITEMS.registerItem("nightshade_powder",
            p -> new HerbItem(p, ModHerbs.NIGHTSHADE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> NIGHTSHADE_EXTRACT = ITEMS.registerItem("nightshade_extract",
            p -> new HerbItem(p, ModHerbs.NIGHTSHADE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> VIPERWEED = ITEMS.registerItem("viperweed",
            p -> new HerbItem(p, ModHerbs.VIPERWEED, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VIPERWEED_SEEDS = ITEMS.registerItem("viperweed_seeds",
            p -> new HerbSeedItem(p, ModHerbs.VIPERWEED), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_VIPERWEED = ITEMS.registerItem("dried_viperweed",
            p -> new HerbItem(p, ModHerbs.VIPERWEED, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VIPERWEED_POWDER = ITEMS.registerItem("viperweed_powder",
            p -> new HerbItem(p, ModHerbs.VIPERWEED, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VIPERWEED_EXTRACT = ITEMS.registerItem("viperweed_extract",
            p -> new HerbItem(p, ModHerbs.VIPERWEED, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> BLACKTHORN = ITEMS.registerItem("blackthorn",
            p -> new HerbItem(p, ModHerbs.BLACKTHORN, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BLACKTHORN_SEEDS = ITEMS.registerItem("blackthorn_seeds",
            p -> new HerbSeedItem(p, ModHerbs.BLACKTHORN), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_BLACKTHORN = ITEMS.registerItem("dried_blackthorn",
            p -> new HerbItem(p, ModHerbs.BLACKTHORN, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BLACKTHORN_POWDER = ITEMS.registerItem("blackthorn_powder",
            p -> new HerbItem(p, ModHerbs.BLACKTHORN, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BLACKTHORN_EXTRACT = ITEMS.registerItem("blackthorn_extract",
            p -> new HerbItem(p, ModHerbs.BLACKTHORN, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> DEADLY_CAP = ITEMS.registerItem("deadly_cap",
            p -> new HerbItem(p, ModHerbs.DEADLY_CAP, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DEADLY_CAP_SEEDS = ITEMS.registerItem("deadly_cap_seeds",
            p -> new HerbSeedItem(p, ModHerbs.DEADLY_CAP), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_DEADLY_CAP = ITEMS.registerItem("dried_deadly_cap",
            p -> new HerbItem(p, ModHerbs.DEADLY_CAP, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DEADLY_CAP_POWDER = ITEMS.registerItem("deadly_cap_powder",
            p -> new HerbItem(p, ModHerbs.DEADLY_CAP, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DEADLY_CAP_EXTRACT = ITEMS.registerItem("deadly_cap_extract",
            p -> new HerbItem(p, ModHerbs.DEADLY_CAP, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> WOLFSBANE = ITEMS.registerItem("wolfsbane",
            p -> new HerbItem(p, ModHerbs.WOLFSBANE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WOLFSBANE_SEEDS = ITEMS.registerItem("wolfsbane_seeds",
            p -> new HerbSeedItem(p, ModHerbs.WOLFSBANE), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_WOLFSBANE = ITEMS.registerItem("dried_wolfsbane",
            p -> new HerbItem(p, ModHerbs.WOLFSBANE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WOLFSBANE_POWDER = ITEMS.registerItem("wolfsbane_powder",
            p -> new HerbItem(p, ModHerbs.WOLFSBANE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WOLFSBANE_EXTRACT = ITEMS.registerItem("wolfsbane_extract",
            p -> new HerbItem(p, ModHerbs.WOLFSBANE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> HEMLOCK = ITEMS.registerItem("hemlock",
            p -> new HerbItem(p, ModHerbs.HEMLOCK, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HEMLOCK_SEEDS = ITEMS.registerItem("hemlock_seeds",
            p -> new HerbSeedItem(p, ModHerbs.HEMLOCK), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_HEMLOCK = ITEMS.registerItem("dried_hemlock",
            p -> new HerbItem(p, ModHerbs.HEMLOCK, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HEMLOCK_POWDER = ITEMS.registerItem("hemlock_powder",
            p -> new HerbItem(p, ModHerbs.HEMLOCK, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HEMLOCK_EXTRACT = ITEMS.registerItem("hemlock_extract",
            p -> new HerbItem(p, ModHerbs.HEMLOCK, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> GRAVE_MOSS = ITEMS.registerItem("grave_moss",
            p -> new HerbItem(p, ModHerbs.GRAVE_MOSS, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> GRAVE_MOSS_SEEDS = ITEMS.registerItem("grave_moss_seeds",
            p -> new HerbSeedItem(p, ModHerbs.GRAVE_MOSS), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_GRAVE_MOSS = ITEMS.registerItem("dried_grave_moss",
            p -> new HerbItem(p, ModHerbs.GRAVE_MOSS, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> GRAVE_MOSS_POWDER = ITEMS.registerItem("grave_moss_powder",
            p -> new HerbItem(p, ModHerbs.GRAVE_MOSS, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> GRAVE_MOSS_EXTRACT = ITEMS.registerItem("grave_moss_extract",
            p -> new HerbItem(p, ModHerbs.GRAVE_MOSS, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> CLEANSAGE = ITEMS.registerItem("cleansage",
            p -> new HerbItem(p, ModHerbs.CLEANSAGE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> CLEANSAGE_SEEDS = ITEMS.registerItem("cleansage_seeds",
            p -> new HerbSeedItem(p, ModHerbs.CLEANSAGE), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_CLEANSAGE = ITEMS.registerItem("dried_cleansage",
            p -> new HerbItem(p, ModHerbs.CLEANSAGE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> CLEANSAGE_POWDER = ITEMS.registerItem("cleansage_powder",
            p -> new HerbItem(p, ModHerbs.CLEANSAGE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> CLEANSAGE_EXTRACT = ITEMS.registerItem("cleansage_extract",
            p -> new HerbItem(p, ModHerbs.CLEANSAGE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> BITTERROOT = ITEMS.registerItem("bitterroot",
            p -> new HerbItem(p, ModHerbs.BITTERROOT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> BITTERROOT_SEEDS = ITEMS.registerItem("bitterroot_seeds",
            p -> new HerbSeedItem(p, ModHerbs.BITTERROOT), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_BITTERROOT = ITEMS.registerItem("dried_bitterroot",
            p -> new HerbItem(p, ModHerbs.BITTERROOT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> BITTERROOT_POWDER = ITEMS.registerItem("bitterroot_powder",
            p -> new HerbItem(p, ModHerbs.BITTERROOT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> BITTERROOT_EXTRACT = ITEMS.registerItem("bitterroot_extract",
            p -> new HerbItem(p, ModHerbs.BITTERROOT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> GOLDEN_CHAMOMILE = ITEMS.registerItem("golden_chamomile",
            p -> new HerbItem(p, ModHerbs.GOLDEN_CHAMOMILE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> GOLDEN_CHAMOMILE_SEEDS = ITEMS.registerItem("golden_chamomile_seeds",
            p -> new HerbSeedItem(p, ModHerbs.GOLDEN_CHAMOMILE), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_GOLDEN_CHAMOMILE = ITEMS.registerItem("dried_golden_chamomile",
            p -> new HerbItem(p, ModHerbs.GOLDEN_CHAMOMILE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> GOLDEN_CHAMOMILE_POWDER = ITEMS.registerItem("golden_chamomile_powder",
            p -> new HerbItem(p, ModHerbs.GOLDEN_CHAMOMILE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> GOLDEN_CHAMOMILE_EXTRACT = ITEMS.registerItem("golden_chamomile_extract",
            p -> new HerbItem(p, ModHerbs.GOLDEN_CHAMOMILE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> MARSHROOT = ITEMS.registerItem("marshroot",
            p -> new HerbItem(p, ModHerbs.MARSHROOT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> MARSHROOT_SEEDS = ITEMS.registerItem("marshroot_seeds",
            p -> new HerbSeedItem(p, ModHerbs.MARSHROOT), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_MARSHROOT = ITEMS.registerItem("dried_marshroot",
            p -> new HerbItem(p, ModHerbs.MARSHROOT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> MARSHROOT_POWDER = ITEMS.registerItem("marshroot_powder",
            p -> new HerbItem(p, ModHerbs.MARSHROOT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> MARSHROOT_EXTRACT = ITEMS.registerItem("marshroot_extract",
            p -> new HerbItem(p, ModHerbs.MARSHROOT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> WITCH_HAZEL = ITEMS.registerItem("witch_hazel",
            p -> new HerbItem(p, ModHerbs.WITCH_HAZEL, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> WITCH_HAZEL_SEEDS = ITEMS.registerItem("witch_hazel_seeds",
            p -> new HerbSeedItem(p, ModHerbs.WITCH_HAZEL), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_WITCH_HAZEL = ITEMS.registerItem("dried_witch_hazel",
            p -> new HerbItem(p, ModHerbs.WITCH_HAZEL, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> WITCH_HAZEL_POWDER = ITEMS.registerItem("witch_hazel_powder",
            p -> new HerbItem(p, ModHerbs.WITCH_HAZEL, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> WITCH_HAZEL_EXTRACT = ITEMS.registerItem("witch_hazel_extract",
            p -> new HerbItem(p, ModHerbs.WITCH_HAZEL, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> FEVERFEW = ITEMS.registerItem("feverfew",
            p -> new HerbItem(p, ModHerbs.FEVERFEW, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FEVERFEW_SEEDS = ITEMS.registerItem("feverfew_seeds",
            p -> new HerbSeedItem(p, ModHerbs.FEVERFEW), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_FEVERFEW = ITEMS.registerItem("dried_feverfew",
            p -> new HerbItem(p, ModHerbs.FEVERFEW, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FEVERFEW_POWDER = ITEMS.registerItem("feverfew_powder",
            p -> new HerbItem(p, ModHerbs.FEVERFEW, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FEVERFEW_EXTRACT = ITEMS.registerItem("feverfew_extract",
            p -> new HerbItem(p, ModHerbs.FEVERFEW, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> LUNGWORT = ITEMS.registerItem("lungwort",
            p -> new HerbItem(p, ModHerbs.LUNGWORT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> LUNGWORT_SEEDS = ITEMS.registerItem("lungwort_seeds",
            p -> new HerbSeedItem(p, ModHerbs.LUNGWORT), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_LUNGWORT = ITEMS.registerItem("dried_lungwort",
            p -> new HerbItem(p, ModHerbs.LUNGWORT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> LUNGWORT_POWDER = ITEMS.registerItem("lungwort_powder",
            p -> new HerbItem(p, ModHerbs.LUNGWORT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> LUNGWORT_EXTRACT = ITEMS.registerItem("lungwort_extract",
            p -> new HerbItem(p, ModHerbs.LUNGWORT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> SPIRITWOOD = ITEMS.registerItem("spiritwood",
            p -> new HerbItem(p, ModHerbs.SPIRITWOOD, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SPIRITWOOD_SEEDS = ITEMS.registerItem("spiritwood_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SPIRITWOOD), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_SPIRITWOOD = ITEMS.registerItem("dried_spiritwood",
            p -> new HerbItem(p, ModHerbs.SPIRITWOOD, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SPIRITWOOD_POWDER = ITEMS.registerItem("spiritwood_powder",
            p -> new HerbItem(p, ModHerbs.SPIRITWOOD, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SPIRITWOOD_EXTRACT = ITEMS.registerItem("spiritwood_extract",
            p -> new HerbItem(p, ModHerbs.SPIRITWOOD, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> STORMHERB = ITEMS.registerItem("stormherb",
            p -> new HerbItem(p, ModHerbs.STORMHERB, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> STORMHERB_SEEDS = ITEMS.registerItem("stormherb_seeds",
            p -> new HerbSeedItem(p, ModHerbs.STORMHERB), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_STORMHERB = ITEMS.registerItem("dried_stormherb",
            p -> new HerbItem(p, ModHerbs.STORMHERB, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> STORMHERB_POWDER = ITEMS.registerItem("stormherb_powder",
            p -> new HerbItem(p, ModHerbs.STORMHERB, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> STORMHERB_EXTRACT = ITEMS.registerItem("stormherb_extract",
            p -> new HerbItem(p, ModHerbs.STORMHERB, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> WILD_GINSENG = ITEMS.registerItem("wild_ginseng",
            p -> new HerbItem(p, ModHerbs.WILD_GINSENG, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WILD_GINSENG_SEEDS = ITEMS.registerItem("wild_ginseng_seeds",
            p -> new HerbSeedItem(p, ModHerbs.WILD_GINSENG), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_WILD_GINSENG = ITEMS.registerItem("dried_wild_ginseng",
            p -> new HerbItem(p, ModHerbs.WILD_GINSENG, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WILD_GINSENG_POWDER = ITEMS.registerItem("wild_ginseng_powder",
            p -> new HerbItem(p, ModHerbs.WILD_GINSENG, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WILD_GINSENG_EXTRACT = ITEMS.registerItem("wild_ginseng_extract",
            p -> new HerbItem(p, ModHerbs.WILD_GINSENG, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> SWIFTLEAF = ITEMS.registerItem("swiftleaf",
            p -> new HerbItem(p, ModHerbs.SWIFTLEAF, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> SWIFTLEAF_SEEDS = ITEMS.registerItem("swiftleaf_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SWIFTLEAF), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_SWIFTLEAF = ITEMS.registerItem("dried_swiftleaf",
            p -> new HerbItem(p, ModHerbs.SWIFTLEAF, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> SWIFTLEAF_POWDER = ITEMS.registerItem("swiftleaf_powder",
            p -> new HerbItem(p, ModHerbs.SWIFTLEAF, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> SWIFTLEAF_EXTRACT = ITEMS.registerItem("swiftleaf_extract",
            p -> new HerbItem(p, ModHerbs.SWIFTLEAF, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> DAWNBLOOM = ITEMS.registerItem("dawnbloom",
            p -> new HerbItem(p, ModHerbs.DAWNBLOOM, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DAWNBLOOM_SEEDS = ITEMS.registerItem("dawnbloom_seeds",
            p -> new HerbSeedItem(p, ModHerbs.DAWNBLOOM), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_DAWNBLOOM = ITEMS.registerItem("dried_dawnbloom",
            p -> new HerbItem(p, ModHerbs.DAWNBLOOM, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DAWNBLOOM_POWDER = ITEMS.registerItem("dawnbloom_powder",
            p -> new HerbItem(p, ModHerbs.DAWNBLOOM, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DAWNBLOOM_EXTRACT = ITEMS.registerItem("dawnbloom_extract",
            p -> new HerbItem(p, ModHerbs.DAWNBLOOM, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> THUNDER_THISTLE = ITEMS.registerItem("thunder_thistle",
            p -> new HerbItem(p, ModHerbs.THUNDER_THISTLE, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> THUNDER_THISTLE_SEEDS = ITEMS.registerItem("thunder_thistle_seeds",
            p -> new HerbSeedItem(p, ModHerbs.THUNDER_THISTLE), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_THUNDER_THISTLE = ITEMS.registerItem("dried_thunder_thistle",
            p -> new HerbItem(p, ModHerbs.THUNDER_THISTLE, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> THUNDER_THISTLE_POWDER = ITEMS.registerItem("thunder_thistle_powder",
            p -> new HerbItem(p, ModHerbs.THUNDER_THISTLE, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> THUNDER_THISTLE_EXTRACT = ITEMS.registerItem("thunder_thistle_extract",
            p -> new HerbItem(p, ModHerbs.THUNDER_THISTLE, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> DREAMCAP = ITEMS.registerItem("dreamcap",
            p -> new HerbItem(p, ModHerbs.DREAMCAP, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DREAMCAP_SEEDS = ITEMS.registerItem("dreamcap_seeds",
            p -> new HerbSeedItem(p, ModHerbs.DREAMCAP), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_DREAMCAP = ITEMS.registerItem("dried_dreamcap",
            p -> new HerbItem(p, ModHerbs.DREAMCAP, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DREAMCAP_POWDER = ITEMS.registerItem("dreamcap_powder",
            p -> new HerbItem(p, ModHerbs.DREAMCAP, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DREAMCAP_EXTRACT = ITEMS.registerItem("dreamcap_extract",
            p -> new HerbItem(p, ModHerbs.DREAMCAP, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> MOONFLOWER = ITEMS.registerItem("moonflower",
            p -> new HerbItem(p, ModHerbs.MOONFLOWER, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> MOONFLOWER_SEEDS = ITEMS.registerItem("moonflower_seeds",
            p -> new HerbSeedItem(p, ModHerbs.MOONFLOWER), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_MOONFLOWER = ITEMS.registerItem("dried_moonflower",
            p -> new HerbItem(p, ModHerbs.MOONFLOWER, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> MOONFLOWER_POWDER = ITEMS.registerItem("moonflower_powder",
            p -> new HerbItem(p, ModHerbs.MOONFLOWER, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> MOONFLOWER_EXTRACT = ITEMS.registerItem("moonflower_extract",
            p -> new HerbItem(p, ModHerbs.MOONFLOWER, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> LAVENDER = ITEMS.registerItem("lavender",
            p -> new HerbItem(p, ModHerbs.LAVENDER, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> LAVENDER_SEEDS = ITEMS.registerItem("lavender_seeds",
            p -> new HerbSeedItem(p, ModHerbs.LAVENDER), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_LAVENDER = ITEMS.registerItem("dried_lavender",
            p -> new HerbItem(p, ModHerbs.LAVENDER, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> LAVENDER_POWDER = ITEMS.registerItem("lavender_powder",
            p -> new HerbItem(p, ModHerbs.LAVENDER, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> LAVENDER_EXTRACT = ITEMS.registerItem("lavender_extract",
            p -> new HerbItem(p, ModHerbs.LAVENDER, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> SLEEPWORT = ITEMS.registerItem("sleepwort",
            p -> new HerbItem(p, ModHerbs.SLEEPWORT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SLEEPWORT_SEEDS = ITEMS.registerItem("sleepwort_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SLEEPWORT), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_SLEEPWORT = ITEMS.registerItem("dried_sleepwort",
            p -> new HerbItem(p, ModHerbs.SLEEPWORT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SLEEPWORT_POWDER = ITEMS.registerItem("sleepwort_powder",
            p -> new HerbItem(p, ModHerbs.SLEEPWORT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SLEEPWORT_EXTRACT = ITEMS.registerItem("sleepwort_extract",
            p -> new HerbItem(p, ModHerbs.SLEEPWORT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> VELVET_BELL = ITEMS.registerItem("velvet_bell",
            p -> new HerbItem(p, ModHerbs.VELVET_BELL, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VELVET_BELL_SEEDS = ITEMS.registerItem("velvet_bell_seeds",
            p -> new HerbSeedItem(p, ModHerbs.VELVET_BELL), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_VELVET_BELL = ITEMS.registerItem("dried_velvet_bell",
            p -> new HerbItem(p, ModHerbs.VELVET_BELL, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VELVET_BELL_POWDER = ITEMS.registerItem("velvet_bell_powder",
            p -> new HerbItem(p, ModHerbs.VELVET_BELL, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VELVET_BELL_EXTRACT = ITEMS.registerItem("velvet_bell_extract",
            p -> new HerbItem(p, ModHerbs.VELVET_BELL, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> IRONBARK = ITEMS.registerItem("ironbark",
            p -> new HerbItem(p, ModHerbs.IRONBARK, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> IRONBARK_SEEDS = ITEMS.registerItem("ironbark_seeds",
            p -> new HerbSeedItem(p, ModHerbs.IRONBARK), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_IRONBARK = ITEMS.registerItem("dried_ironbark",
            p -> new HerbItem(p, ModHerbs.IRONBARK, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> IRONBARK_POWDER = ITEMS.registerItem("ironbark_powder",
            p -> new HerbItem(p, ModHerbs.IRONBARK, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> IRONBARK_EXTRACT = ITEMS.registerItem("ironbark_extract",
            p -> new HerbItem(p, ModHerbs.IRONBARK, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> ASHLEAF = ITEMS.registerItem("ashleaf",
            p -> new HerbItem(p, ModHerbs.ASHLEAF, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> ASHLEAF_SEEDS = ITEMS.registerItem("ashleaf_seeds",
            p -> new HerbSeedItem(p, ModHerbs.ASHLEAF), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> DRIED_ASHLEAF = ITEMS.registerItem("dried_ashleaf",
            p -> new HerbItem(p, ModHerbs.ASHLEAF, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> ASHLEAF_POWDER = ITEMS.registerItem("ashleaf_powder",
            p -> new HerbItem(p, ModHerbs.ASHLEAF, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> ASHLEAF_EXTRACT = ITEMS.registerItem("ashleaf_extract",
            p -> new HerbItem(p, ModHerbs.ASHLEAF, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> DRAGONSCALE_HERB = ITEMS.registerItem("dragonscale_herb",
            p -> new HerbItem(p, ModHerbs.DRAGONSCALE_HERB, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> DRAGONSCALE_HERB_SEEDS = ITEMS.registerItem("dragonscale_herb_seeds",
            p -> new HerbSeedItem(p, ModHerbs.DRAGONSCALE_HERB), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> DRIED_DRAGONSCALE_HERB = ITEMS.registerItem("dried_dragonscale_herb",
            p -> new HerbItem(p, ModHerbs.DRAGONSCALE_HERB, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> DRAGONSCALE_HERB_POWDER = ITEMS.registerItem("dragonscale_herb_powder",
            p -> new HerbItem(p, ModHerbs.DRAGONSCALE_HERB, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> DRAGONSCALE_HERB_EXTRACT = ITEMS.registerItem("dragonscale_herb_extract",
            p -> new HerbItem(p, ModHerbs.DRAGONSCALE_HERB, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> WARDROOT = ITEMS.registerItem("wardroot",
            p -> new HerbItem(p, ModHerbs.WARDROOT, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WARDROOT_SEEDS = ITEMS.registerItem("wardroot_seeds",
            p -> new HerbSeedItem(p, ModHerbs.WARDROOT), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRIED_WARDROOT = ITEMS.registerItem("dried_wardroot",
            p -> new HerbItem(p, ModHerbs.WARDROOT, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WARDROOT_POWDER = ITEMS.registerItem("wardroot_powder",
            p -> new HerbItem(p, ModHerbs.WARDROOT, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final DeferredItem<Item> WARDROOT_EXTRACT = ITEMS.registerItem("wardroot_extract",
            p -> new HerbItem(p, ModHerbs.WARDROOT, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final DeferredItem<Item> SUNPETAL = ITEMS.registerItem("sunpetal",
            p -> new HerbItem(p, ModHerbs.SUNPETAL, HerbForm.FRESH, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SUNPETAL_SEEDS = ITEMS.registerItem("sunpetal_seeds",
            p -> new HerbSeedItem(p, ModHerbs.SUNPETAL), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DRIED_SUNPETAL = ITEMS.registerItem("dried_sunpetal",
            p -> new HerbItem(p, ModHerbs.SUNPETAL, HerbForm.DRIED, 0.8f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SUNPETAL_POWDER = ITEMS.registerItem("sunpetal_powder",
            p -> new HerbItem(p, ModHerbs.SUNPETAL, HerbForm.POWDER, 1.0f), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SUNPETAL_EXTRACT = ITEMS.registerItem("sunpetal_extract",
            p -> new HerbItem(p, ModHerbs.SUNPETAL, HerbForm.EXTRACT, 1.5f), new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> MINOR_HEALING_TONIC = ITEMS.registerItem("minor_healing_tonic",
            p -> new TonicItem(p, ModMedicines.MINOR_HEALING_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> HEALING_TONIC = ITEMS.registerItem("healing_tonic",
            p -> new TonicItem(p, ModMedicines.HEALING_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> STRONG_HEALING_TONIC = ITEMS.registerItem("strong_healing_tonic",
            p -> new TonicItem(p, ModMedicines.STRONG_HEALING_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> HEMOSTATIC_TONIC = ITEMS.registerItem("hemostatic_tonic",
            p -> new TonicItem(p, ModMedicines.HEMOSTATIC_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> ANTISEPTIC_TONIC = ITEMS.registerItem("antiseptic_tonic",
            p -> new TonicItem(p, ModMedicines.ANTISEPTIC_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> ANTIDOTE_TONIC = ITEMS.registerItem("antidote_tonic",
            p -> new TonicItem(p, ModMedicines.ANTIDOTE_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> DETOX_TONIC = ITEMS.registerItem("detox_tonic",
            p -> new TonicItem(p, ModMedicines.DETOX_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> RESPIRATORY_TONIC = ITEMS.registerItem("respiratory_tonic",
            p -> new TonicItem(p, ModMedicines.RESPIRATORY_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FROST_LUNG_TONIC = ITEMS.registerItem("frost_lung_tonic",
            p -> new TonicItem(p, ModMedicines.FROST_LUNG_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> STAMINA_TONIC = ITEMS.registerItem("stamina_tonic",
            p -> new TonicItem(p, ModMedicines.STAMINA_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> FOCUS_TONIC = ITEMS.registerItem("focus_tonic",
            p -> new TonicItem(p, ModMedicines.FOCUS_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> RECUPERATION_TONIC = ITEMS.registerItem("recuperation_tonic",
            p -> new TonicItem(p, ModMedicines.RECUPERATION_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> VITALIS_TONIC = ITEMS.registerItem("vitalis_tonic",
            p -> new TonicItem(p, ModMedicines.VITALIS_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> EMBER_TONIC = ITEMS.registerItem("ember_tonic",
            p -> new TonicItem(p, ModMedicines.EMBER_TONIC), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> DRAGONSCALE_DRAUGHT = ITEMS.registerItem("dragonscale_draught",
            p -> new TonicItem(p, ModMedicines.DRAGONSCALE_DRAUGHT), new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> WARMING_TEA = ITEMS.registerItem("warming_tea",
            p -> new TeaItem(p, ModMedicines.WARMING_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> COOLING_TEA = ITEMS.registerItem("cooling_tea",
            p -> new TeaItem(p, ModMedicines.COOLING_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> HERBAL_TEA = ITEMS.registerItem("herbal_tea",
            p -> new TeaItem(p, ModMedicines.HERBAL_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> FOCUS_TEA = ITEMS.registerItem("focus_tea",
            p -> new TeaItem(p, ModMedicines.FOCUS_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> SLEEP_TEA = ITEMS.registerItem("sleep_tea",
            p -> new TeaItem(p, ModMedicines.SLEEP_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> HUSH_TEA = ITEMS.registerItem("hush_tea",
            p -> new TeaItem(p, ModMedicines.HUSH_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> RECOVERY_TEA = ITEMS.registerItem("recovery_tea",
            p -> new TeaItem(p, ModMedicines.RECOVERY_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BITTER_TEA = ITEMS.registerItem("bitter_tea",
            p -> new TeaItem(p, ModMedicines.BITTER_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> SUN_TEA = ITEMS.registerItem("sun_tea",
            p -> new TeaItem(p, ModMedicines.SUN_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> BERRY_TEA = ITEMS.registerItem("berry_tea",
            p -> new TeaItem(p, ModMedicines.BERRY_TEA), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> FROST_SALVE = ITEMS.registerItem("frost_salve",
            p -> new SalveItem(p, ModMedicines.FROST_SALVE), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> EMBER_SALVE = ITEMS.registerItem("ember_salve",
            p -> new SalveItem(p, ModMedicines.EMBER_SALVE), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BURN_SALVE = ITEMS.registerItem("burn_salve",
            p -> new SalveItem(p, ModMedicines.BURN_SALVE), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> ANTISEPTIC_SALVE = ITEMS.registerItem("antiseptic_salve",
            p -> new SalveItem(p, ModMedicines.ANTISEPTIC_SALVE), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> HEALING_SALVE = ITEMS.registerItem("healing_salve",
            p -> new SalveItem(p, ModMedicines.HEALING_SALVE), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> WARD_SALVE = ITEMS.registerItem("ward_salve",
            p -> new SalveItem(p, ModMedicines.WARD_SALVE), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> TOXIC_OIL = ITEMS.registerItem("toxic_oil",
            p -> new WeaponOilItem(p, ModMedicines.TOXIC_OIL), new Item.Properties().stacksTo(8).rarity(Rarity.COMMON));
    public static final DeferredItem<Item> POISON_EXTRACT = ITEMS.registerItem("poison_extract",
            p -> new WeaponOilItem(p, ModMedicines.POISON_EXTRACT), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BLEEDING_OIL = ITEMS.registerItem("bleeding_oil",
            p -> new WeaponOilItem(p, ModMedicines.BLEEDING_OIL), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> WEAKENING_OIL = ITEMS.registerItem("weakening_oil",
            p -> new WeaponOilItem(p, ModMedicines.WEAKENING_OIL), new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SLOWING_OIL = ITEMS.registerItem("slowing_oil",
            p -> new WeaponOilItem(p, ModMedicines.SLOWING_OIL), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VENOM_OIL = ITEMS.registerItem("venom_oil",
            p -> new WeaponOilItem(p, ModMedicines.VENOM_OIL), new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));

    // ---- materials, containers, tools and knowledge ---------------------------
    public static final DeferredItem<Item> GLASS_VIAL = ITEMS.registerItem("glass_vial",
            p -> new FillableContainerItem(p, FillTarget.WATER_VIAL), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> CLAY_CUP = ITEMS.registerItem("clay_cup",
            p -> new FillableContainerItem(p, FillTarget.SPRING_WATER_CUP), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> WATER_VIAL = ITEMS.registerItem("water_vial",
            p -> new Item(p), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SPRING_WATER_CUP = ITEMS.registerItem("spring_water_cup",
            p -> new Item(p), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> HERBAL_OIL = ITEMS.registerItem("herbal_oil",
            p -> new Item(p), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> HERBAL_ALCOHOL = ITEMS.registerItem("herbal_alcohol",
            p -> new Item(p), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SALVE_BASE = ITEMS.registerItem("salve_base",
            p -> new Item(p), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> MOONWATER = ITEMS.registerItem("moonwater",
            p -> new Item(p), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> PURITY_SALT = ITEMS.registerItem("purity_salt",
            p -> new Item(p), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> EMBER_ASH = ITEMS.registerItem("ember_ash",
            p -> new Item(p), new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> FROST_CRYSTAL = ITEMS.registerItem("frost_crystal",
            p -> new Item(p), new Item.Properties().stacksTo(32).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> LIFE_ESSENCE = ITEMS.registerItem("life_essence",
            p -> new Item(p), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));
    public static final DeferredItem<Item> SHADOW_ICHOR = ITEMS.registerItem("shadow_ichor",
            p -> new Item(p), new Item.Properties().stacksTo(16).rarity(Rarity.RARE));
    public static final DeferredItem<Item> PRUNING_SHEARS = ITEMS.registerItem("pruning_shears",
            p -> new PruningShearsItem(p.durability(238)), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> SEED_POUCH = ITEMS.registerItem("seed_pouch",
            p -> new SeedPouchItem(p), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> HERBALISTS_JOURNAL = ITEMS.registerItem("herbalists_journal",
            p -> new JournalItem(p), new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> JOURNAL_PAGE_HERBAL = ITEMS.registerItem("journal_page_herbal",
            p -> new JournalPageItem(p, JournalPageKind.HERBAL), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> JOURNAL_PAGE_MEDICINAL = ITEMS.registerItem("journal_page_medicinal",
            p -> new JournalPageItem(p, JournalPageKind.MEDICINAL), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> JOURNAL_PAGE_TOXIC = ITEMS.registerItem("journal_page_toxic",
            p -> new JournalPageItem(p, JournalPageKind.TOXIC), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> JOURNAL_PAGE_ANCIENT = ITEMS.registerItem("journal_page_ancient",
            p -> new JournalPageItem(p, JournalPageKind.ANCIENT), new Item.Properties().stacksTo(1));

    // ---- outputs of failed or unknown experiments -----------------------------
    public static final DeferredItem<Item> EXPERIMENTAL_TONIC = ITEMS.registerItem("experimental_tonic",
            p -> new ExperimentalTonicItem(p), new Item.Properties().stacksTo(8));
    public static final DeferredItem<Item> EXPERIMENTAL_TEA = ITEMS.registerItem("experimental_tea",
            p -> new ExperimentalTeaItem(p), new Item.Properties().stacksTo(8));
    public static final DeferredItem<Item> FAILED_MIXTURE = ITEMS.registerItem("failed_mixture",
            p -> new FailedMixtureItem(p), new Item.Properties().stacksTo(8));
}
