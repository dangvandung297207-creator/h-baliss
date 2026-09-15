package com.herbalistscraft.mixing;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.Freshness;
import com.herbalistscraft.herb.HerbDefinition;
import com.herbalistscraft.herb.HerbForm;
import com.herbalistscraft.herb.HerbForms;
import com.herbalistscraft.herb.HerbItem;
import com.herbalistscraft.herb.HerbProperty;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.knowledge.Knowledge;
import com.herbalistscraft.recipe.TableRecipe;
import com.herbalistscraft.registry.ModItems;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The Herbalist's Table's calculator.
 *
 * <p>First the data-driven recipes get their chance: if a recipe matches, the written medicine
 * is the answer. Otherwise - when experimentation is enabled - the inputs are computed instead:
 * the base decides how well the herb dissolves, an extract concentrates it, and a catalyst
 * steers it, so Healing plus Cooling reliably becomes a cooling healing brew. Two identical
 * experiments always give the same mixture, and nothing is ever registered at runtime: unknown
 * results are ordinary bottles carrying a {@link MixtureData} component.
 */
public final class MixingCalculator {
    private MixingCalculator() {}

    public static MixingResult calculate(Level level, ItemStack base, ItemStack herb, ItemStack extract,
                                         ItemStack catalyst, Knowledge knowledge) {
        List<Component> lines = new java.util.ArrayList<>();
        if (base.isEmpty() || herb.isEmpty()) {
            lines.add(Component.translatable("gui.herbalistscraft.table.needs_input").withStyle(ChatFormatting.GRAY));
            return MixingResult.failure(lines);
        }

        TableRecipe recipe = TableRecipe.find(level, base, herb, extract, catalyst);
        if (recipe != null) {
            ResourceLocation recipeId = recipe.medicine().orElseGet(() ->
                    ResourceLocation.fromNamespaceAndPath("herbalistscraft", "unknown_recipe"));
            boolean known = knowledge == null || knowledge.knowsRecipe(recipeId);
            Map<HerbProperty, Float> properties = propertiesOf(level, herb);
            lines.add(Component.translatable("gui.herbalistscraft.table.result")
                    .withStyle(ChatFormatting.GRAY));
            lines.add(recipe.getResultItem(level.registryAccess()).getHoverName().copy()
                    .withStyle(ChatFormatting.GOLD));
            if (!known) {
                lines.add(Component.translatable("gui.herbalistscraft.table.unknown_recipe")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else {
                lines.add(PropertyEffects.summary(properties).isEmpty()
                        ? Component.empty()
                        : Component.literal(PropertyEffects.summary(properties)).withStyle(ChatFormatting.AQUA));
            }
            return new MixingResult(recipeId, recipeId.getPath(), potencyOf(level, herb, extract),
                    properties, known, false, false, lines);
        }

        if (!Config.experimentation()) {
            lines.add(Component.translatable("gui.herbalistscraft.table.no_recipe").withStyle(ChatFormatting.GRAY));
            return MixingResult.failure(lines);
        }
        return experiment(level, base, herb, extract, catalyst, knowledge, lines);
    }

    private static MixingResult experiment(Level level, ItemStack base, ItemStack herb, ItemStack extract,
                                           ItemStack catalyst, Knowledge knowledge, List<Component> lines) {
        Map<HerbProperty, Float> properties = propertiesOf(level, herb);
        float potency = potencyOf(level, herb, extract);
        int toxin = toxinOf(level, herb);
        boolean spoiled = spoiledHerb(level, herb);

        potency *= baseMultiplier(base.getDescriptionId());
        toxin = Math.round(toxin * toxinMultiplier(base.getDescriptionId()));
        applyCatalyst(catalyst, properties, lines);

        boolean known = knowledge != null && HerbForms.asHerb(herb) != null
                && knowledge.knowsHerb(HerbForms.asHerb(herb).herb().location());

        if (spoiled) {
            lines.add(Component.translatable("gui.herbalistscraft.table.spoiled").withStyle(ChatFormatting.DARK_RED));
            return new MixingResult(ResourceLocation.fromNamespaceAndPath("herbalistscraft", "failed_mixture"),
                    "failed_mixture", 0.0F, Map.of(), false, true, false, lines);
        }
        if (properties.values().stream().noneMatch(value -> value > 0.0F) && toxin <= 0) {
            lines.add(Component.translatable("gui.herbalistscraft.table.inert").withStyle(ChatFormatting.GRAY));
            return new MixingResult(ResourceLocation.fromNamespaceAndPath("herbalistscraft", "failed_mixture"),
                    "failed_mixture", 0.0F, Map.of(), false, true, false, lines);
        }

        boolean tea = base.is(ModItems.SPRING_WATER_CUP.get()) || base.is(ModItems.CLAY_CUP.get());
        ResourceLocation output = ResourceLocation.fromNamespaceAndPath("herbalistscraft",
                tea ? "experimental_tea" : "experimental_tonic");
        MixtureData data = new MixtureData(potency, (int) Math.round(toxin * Config.experimentToxinScale()),
                toStringMap(properties), false, PropertyEffects.summary(properties));

        lines.add(Component.translatable("gui.herbalistscraft.table.experimental").withStyle(ChatFormatting.YELLOW));
        if (known) {
            lines.add(PropertyEffects.summary(properties).isEmpty()
                    ? Component.translatable("gui.herbalistscraft.table.inert").withStyle(ChatFormatting.GRAY)
                    : Component.literal(PropertyEffects.summary(properties)).withStyle(ChatFormatting.AQUA));
        } else {
            lines.add(Component.translatable("gui.herbalistscraft.table.unknown_herb").withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(Component.translatable("gui.herbalistscraft.table.potency",
                Math.round(potency * 100.0F)).withStyle(ChatFormatting.GRAY));
        if (data.toxin() > 0) {
            lines.add(Component.translatable("gui.herbalistscraft.table.toxin", data.toxin())
                    .withStyle(ChatFormatting.RED));
        }
        return new MixingResult(output, output.getPath(), potency, properties, known, false, true, lines);
    }

    /** Properties of the herb in the slot, scaled by its form and freshness. */
    public static Map<HerbProperty, Float> propertiesOf(Level level, ItemStack stack) {
        Map<HerbProperty, Float> result = new EnumMap<>(HerbProperty.class);
        HerbItem item = HerbForms.asHerb(stack);
        if (item == null) {
            return result;
        }
        HerbDefinition definition = HerbRegistry.get(level.registryAccess(), item.herb()).orElse(null);
        if (definition == null) {
            return result;
        }
        float scale = potency(level, stack, item);
        for (Map.Entry<HerbProperty, Float> entry : definition.properties().entrySet()) {
            float value = entry.getValue() * scale;
            if (value > 0.0F) {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }

    /** Potency of the herb slot, plus half as much again for an extract in the middle slot. */
    public static float potencyOf(Level level, ItemStack herb, ItemStack extract) {
        float potency = potency(level, herb, null);
        if (!extract.isEmpty() && HerbForms.asHerb(extract) != null) {
            potency += 0.5F * potency(level, extract, null);
        }
        return Math.min(2.5F, potency);
    }

    private static float potency(Level level, ItemStack stack, HerbItem item) {
        HerbItem herb = item != null ? item : HerbForms.asHerb(stack);
        if (herb == null) {
            return 0.0F;
        }
        long day = level.getDayTime() / 24000L;
        float freshness = Config.freshnessEffect()
                ? Freshness.state(stack, herb.form(), day).potency()
                : 1.0F;
        return herb.form().potency() * freshness * herb.quality();
    }

    private static int toxinOf(Level level, ItemStack stack) {
        HerbItem item = HerbForms.asHerb(stack);
        if (item == null) {
            return 0;
        }
        HerbDefinition definition = HerbRegistry.get(level.registryAccess(), item.herb()).orElse(null);
        if (definition == null) {
            return 0;
        }
        int points = definition.toxicity().severity() * 6;
        if (item.form() == HerbForm.EXTRACT) {
            points = Math.round(points * 1.5F);
        }
        return points;
    }

    private static boolean spoiledHerb(Level level, ItemStack stack) {
        HerbItem item = HerbForms.asHerb(stack);
        if (item == null || !Config.freshnessEnabled()) {
            return false;
        }
        return Freshness.isSpoiled(stack, item.form(), level.getDayTime() / 24000L);
    }

    /** How well this base carries a herb: water is honest, spirits pull harder, oil holds poison. */
    private static float baseMultiplier(String descriptionId) {
        if (descriptionId.endsWith(".herbal_alcohol")) {
            return 1.15F;
        }
        if (descriptionId.endsWith(".moonwater")) {
            return 1.3F;
        }
        if (descriptionId.endsWith(".salve_base") || descriptionId.endsWith(".herbal_oil")) {
            return 0.9F;
        }
        return 1.0F;
    }

    private static float toxinMultiplier(String descriptionId) {
        return descriptionId.endsWith(".herbal_oil") ? 1.25F : 1.0F;
    }

    /** Catalysts bend the mixture, deterministically and visibly. */
    private static void applyCatalyst(ItemStack catalyst, Map<HerbProperty, Float> properties,
                                      List<Component> lines) {
        if (catalyst.isEmpty()) {
            return;
        }
        if (catalyst.is(ModItems.PURITY_SALT.get())) {
            properties.remove(HerbProperty.TOXIC);
            lines.add(Component.translatable("gui.herbalistscraft.table.catalyst_purity")
                    .withStyle(ChatFormatting.WHITE));
        } else if (catalyst.is(ModItems.LIFE_ESSENCE.get())) {
            properties.compute(HerbProperty.HEALING, (key, value) -> (value == null ? 0.4F : value + 0.4F));
            properties.compute(HerbProperty.REGENERATION, (key, value) -> (value == null ? 0.3F : value + 0.3F));
            lines.add(Component.translatable("gui.herbalistscraft.table.catalyst_life")
                    .withStyle(ChatFormatting.GREEN));
        } else if (catalyst.is(ModItems.FROST_CRYSTAL.get())) {
            properties.compute(HerbProperty.COOLING, (key, value) -> (value == null ? 0.6F : value + 0.6F));
            properties.remove(HerbProperty.WARMING);
            lines.add(Component.translatable("gui.herbalistscraft.table.catalyst_frost")
                    .withStyle(ChatFormatting.AQUA));
        } else if (catalyst.is(ModItems.EMBER_ASH.get())) {
            properties.compute(HerbProperty.WARMING, (key, value) -> (value == null ? 0.6F : value + 0.6F));
            properties.remove(HerbProperty.COOLING);
            lines.add(Component.translatable("gui.herbalistscraft.table.catalyst_ember")
                    .withStyle(ChatFormatting.GOLD));
        } else if (catalyst.is(ModItems.SHADOW_ICHOR.get())) {
            properties.compute(HerbProperty.TOXIC, (key, value) -> (value == null ? 0.8F : value + 0.8F));
            lines.add(Component.translatable("gui.herbalistscraft.table.catalyst_shadow")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    private static Map<String, Float> toStringMap(Map<HerbProperty, Float> properties) {
        Map<String, Float> out = new java.util.LinkedHashMap<>();
        for (HerbProperty property : HerbProperty.VALUES) {
            Float value = properties.get(property);
            if (value != null && value > 0.0F) {
                out.put(property.name(), value);
            }
        }
        return out;
    }
}
