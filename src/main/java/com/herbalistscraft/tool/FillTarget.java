package com.herbalistscraft.tool;

import com.herbalistscraft.registry.ModItems;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

/** What an empty container becomes when it is filled at a water source. */
public enum FillTarget {
    WATER_VIAL(() -> ModItems.WATER_VIAL.get(), "water_vial"),
    SPRING_WATER_CUP(() -> ModItems.SPRING_WATER_CUP.get(), "spring_water_cup");

    private final Supplier<Item> filled;
    private final String name;

    FillTarget(Supplier<Item> filled, String name) {
        this.filled = filled;
        this.name = name;
    }

    public Item filled() {
        return filled.get();
    }

    public String key() {
        return name;
    }
}
