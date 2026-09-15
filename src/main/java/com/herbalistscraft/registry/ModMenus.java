package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.menu.HerbalMillMenu;
import com.herbalistscraft.menu.HerbalistTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Menu types; the factories carry the block position so both sides agree on the target. */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, HerbalistsCraft.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<HerbalMillMenu>> HERBAL_MILL =
            MENUS.register("herbal_mill", () -> IMenuTypeExtension.create(
                    (id, inventory, data) -> new HerbalMillMenu(id, inventory, data.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<HerbalistTableMenu>> HERBALISTS_TABLE =
            MENUS.register("herbalists_table", () -> IMenuTypeExtension.create(
                    (id, inventory, data) -> new HerbalistTableMenu(id, inventory, data.readBlockPos())));

    private ModMenus() {}
}
