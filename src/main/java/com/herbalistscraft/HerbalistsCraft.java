package com.herbalistscraft;

import com.herbalistscraft.herb.HerbDefinition;
import com.herbalistscraft.medicine.MedicineDefinition;
import com.herbalistscraft.registry.ModAttachments;
import com.herbalistscraft.registry.ModBlockEntities;
import com.herbalistscraft.registry.ModBlocks;
import com.herbalistscraft.registry.ModCreativeTabs;
import com.herbalistscraft.registry.ModCriteria;
import com.herbalistscraft.registry.ModDataComponents;
import com.herbalistscraft.registry.ModEffects;
import com.herbalistscraft.registry.ModFeatures;
import com.herbalistscraft.registry.ModHerbs;
import com.herbalistscraft.registry.ModItems;
import com.herbalistscraft.registry.ModLootModifiers;
import com.herbalistscraft.registry.ModMedicines;
import com.herbalistscraft.registry.ModMenus;
import com.herbalistscraft.registry.ModParticles;
import com.herbalistscraft.registry.ModRecipes;
import com.herbalistscraft.registry.ModSounds;
import com.herbalistscraft.registry.ModVillagers;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

/** Entry point: registers content, config and the synced datapack registries. */
@Mod(HerbalistsCraft.MODID)
public class HerbalistsCraft {
    public static final String MODID = "herbalistscraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HerbalistsCraft(IEventBus modBus, ModContainer container) {
        ModDataComponents.DATA_COMPONENTS.register(modBus);
        ModAttachments.ATTACHMENT_TYPES.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModCriteria.TRIGGERS.register(modBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModVillagers.POI_TYPES.register(modBus);
        ModVillagers.PROFESSIONS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        modBus.addListener(HerbalistsCraft::registerDatapackRegistries);
        container.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    private static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModHerbs.REGISTRY, HerbDefinition.CODEC, HerbDefinition.CODEC);
        event.dataPackRegistry(ModMedicines.REGISTRY, MedicineDefinition.CODEC, MedicineDefinition.CODEC);
    }
}
