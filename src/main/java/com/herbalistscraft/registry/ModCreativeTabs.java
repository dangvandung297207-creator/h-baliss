package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The single, ordered creative tab: herbs, seeds, processing, medicines, teas, salves,
 *  oils, tools, knowledge, workstations. */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HerbalistsCraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HERBALISTS_CRAFT = TABS.register(
            "herbalists_craft", () -> CreativeModeTab.builder()
                    .title(Component.translatable("key." + HerbalistsCraft.MODID + ".category"))
                    .icon(() -> new ItemStack(ModItems.HERBALISTS_JOURNAL.get()))
                    .displayItems((parameters, output) -> CreativeTabContent.fill(parameters, output))
                    .build());

    private ModCreativeTabs() {}
}
