package com.herbalistscraft.herb;

import com.herbalistscraft.registry.ModItems;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Item lookup by herb and form. The generated {@code ModItems} owns the registrations; this
 * class turns a herb key plus a form into the item, once, and remembers the answer. That keeps
 * processing code free of item-id strings and keeps the content files authoritative.
 */
public final class HerbForms {
    private static final Map<HerbForm, Map<ResourceKey<HerbDefinition>, Item>> BY_FORM =
            new EnumMap<>(HerbForm.class);
    private static final Map<ResourceKey<HerbDefinition>, Item> SEEDS = new HashMap<>();
    private static boolean built;

    private HerbForms() {}

    private static void build() {
        if (built) {
            return;
        }
        for (HerbForm form : HerbForm.values()) {
            BY_FORM.put(form, new HashMap<>());
        }
        for (var holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (item instanceof HerbItem herb) {
                BY_FORM.get(herb.form()).put(herb.herb(), item);
            } else if (item instanceof HerbSeedItem seed) {
                SEEDS.put(seed.herbKey(), item);
            }
        }
        built = true;
    }

    /** The item for a herb in one form, or {@code Items.AIR} when the herb has no such form. */
    public static Item item(ResourceKey<HerbDefinition> herb, HerbForm form) {
        build();
        return BY_FORM.getOrDefault(form, Map.of()).getOrDefault(herb, Items.AIR);
    }

    public static Item seed(ResourceKey<HerbDefinition> herb) {
        build();
        return SEEDS.getOrDefault(herb, Items.AIR);
    }

    /** The herb and form an item belongs to, or null when it is not a herb item. */
    public static HerbItem asHerb(ItemStack stack) {
        return stack.getItem() instanceof HerbItem herb ? herb : null;
    }

    /** All items in one form, used by the journal and the creative tab. */
    public static Map<ResourceKey<HerbDefinition>, Item> all(HerbForm form) {
        build();
        return Map.copyOf(BY_FORM.getOrDefault(form, Map.of()));
    }
}
