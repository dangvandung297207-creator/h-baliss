package com.herbalistscraft.herb;

import com.herbalistscraft.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** A herb in one of its forms: fresh, dried, powder or extract. */
public class HerbItem extends Item {
    private final ResourceKey<HerbDefinition> herb;
    private final HerbForm form;
    private final float quality;

    public HerbItem(Properties properties, ResourceKey<HerbDefinition> herb, HerbForm form, float quality) {
        super(properties);
        this.herb = herb;
        this.form = form;
        this.quality = quality;
    }

    public ResourceKey<HerbDefinition> herb() {
        return herb;
    }

    public HerbForm form() {
        return form;
    }

    /** Potency multiplier of this form, before freshness and quality are applied. */
    public float quality() {
        return quality;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        HerbDefinition definition = context.registries() == null
                ? null
                : HerbRegistry.get(context.registries(), herb).orElse(null);
        if (definition == null) {
            return;
        }
        tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.category",
                definition.category().displayName()).withStyle(ChatFormatting.GRAY));
        long day = context.level() == null ? -1L : context.level().getDayTime() / 24000L;
        Freshness.appendTooltip(stack, form, tooltip, day);
        tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.toxicity",
                definition.toxicity().displayName()).withStyle(definition.toxicity().severity() > 0
                        ? ChatFormatting.RED
                        : ChatFormatting.DARK_GREEN));
        if (!definition.uses().isEmpty()) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.uses").withStyle(ChatFormatting.GRAY));
            for (String use : definition.uses()) {
                tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.uses_line", use)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return super.getDescriptionId(stack);
    }

    /** Stamps the harvest day so fresh herbs can age. */
    public void stampHarvest(ItemStack stack, long gameDay) {
        if (form == HerbForm.FRESH) {
            stack.set(ModDataComponents.HARVEST_DAY.get(), (int) gameDay);
        }
    }
}
