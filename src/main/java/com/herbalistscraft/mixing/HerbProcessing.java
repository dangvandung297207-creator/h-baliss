package com.herbalistscraft.mixing;

import com.herbalistscraft.herb.Freshness;
import com.herbalistscraft.herb.HerbForm;
import com.herbalistscraft.herb.HerbForms;
import com.herbalistscraft.herb.HerbItem;
import com.herbalistscraft.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The three transformations every herb goes through: drying, grinding to powder and steeping
 * into an extract. Each one trades potency against keeping quality - fresh is strong but ages,
 * dried keeps for ever, powder and extract are the concentrated forms used by the table.
 */
public final class HerbProcessing {
    private HerbProcessing() {}

    /** Fresh herb to dried herb: weaker, but it never spoils again. */
    public static ItemStack dry(ItemStack fresh) {
        HerbItem herb = HerbForms.asHerb(fresh);
        if (herb == null || herb.form() != HerbForm.FRESH) {
            return ItemStack.EMPTY;
        }
        Item dried = HerbForms.item(herb.herb(), HerbForm.DRIED);
        return dried == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(dried);
    }

    /** Herb or dried herb to powder, the form the mortar and the mill produce. */
    public static ItemStack powder(ItemStack stack) {
        HerbItem herb = HerbForms.asHerb(stack);
        if (herb == null || herb.form() == HerbForm.SEED || herb.form() == HerbForm.POWDER) {
            return ItemStack.EMPTY;
        }
        Item powder = HerbForms.item(herb.herb(), HerbForm.POWDER);
        return powder == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(powder);
    }

    /** Powder to extract: the concentrated form a catalyst can steer. */
    public static ItemStack extract(ItemStack stack) {
        HerbItem herb = HerbForms.asHerb(stack);
        if (herb == null || herb.form() == HerbForm.SEED || herb.form() == HerbForm.EXTRACT) {
            return ItemStack.EMPTY;
        }
        Item extract = HerbForms.item(herb.herb(), HerbForm.EXTRACT);
        return extract == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(extract);
    }

    /** Potency of a herb stack right now, freshness included. */
    public static float potency(ItemStack stack, long day) {
        HerbItem herb = HerbForms.asHerb(stack);
        if (herb == null) {
            return 0.0F;
        }
        return Freshness.potencyMultiplier(stack, herb.form(), day);
    }

    public static boolean isHerb(ItemStack stack) {
        return HerbForms.asHerb(stack) != null;
    }

    public static boolean isSpoiled(ItemStack stack, long day) {
        HerbItem herb = HerbForms.asHerb(stack);
        return herb != null && Freshness.isSpoiled(stack, herb.form(), day);
    }

    /** Name shown in the mill's hover info. */
    public static String describe(ItemStack stack) {
        return stack.isEmpty() ? "" : stack.getHoverName().getString();
    }
}
