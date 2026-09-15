package com.herbalistscraft.loot;

import com.herbalistscraft.Config;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Sprinkles herbal materials into vanilla loot tables without drowning them: one weighted roll,
 * a modest chance, and a count range per entry.
 */
public class HerbLootModifier extends LootModifier {
    public static final MapCodec<HerbLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).and(instance.group(
                    Entry.CODEC.codec().listOf().fieldOf("entries").forGetter(modifier -> modifier.entries),
                    Codec.intRange(0, 8).optionalFieldOf("rolls", 1).forGetter(modifier -> modifier.rolls),
                    Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 0.6F).forGetter(modifier -> modifier.chance)
            )).apply(instance, HerbLootModifier::new));

    private final List<Entry> entries;
    private final int rolls;
    private final float chance;

    public HerbLootModifier(LootItemCondition[] conditions, List<Entry> entries, int rolls, float chance) {
        super(conditions);
        this.entries = entries;
        this.rolls = rolls;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.spawnWildHerbs() || entries.isEmpty()) {
            return generatedLoot;
        }
        RandomSource random = context.getRandom();
        float weightedChance = chance * (float) Config.herbSpawnRate();
        List<Entry> candidates = new ArrayList<>();
        int totalWeight = 0;
        for (Entry entry : entries) {
            ItemStack stack = entry.stack();
            if (stack.isEmpty()) {
                continue;
            }
            candidates.add(entry);
            totalWeight += entry.weight();
        }
        if (candidates.isEmpty() || totalWeight <= 0) {
            return generatedLoot;
        }
        for (int roll = 0; roll < rolls; roll++) {
            if (random.nextFloat() > weightedChance) {
                continue;
            }
            int pick = random.nextInt(totalWeight);
            for (Entry entry : candidates) {
                pick -= entry.weight();
                if (pick < 0) {
                    generatedLoot.add(entry.create(random));
                    break;
                }
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    /** One weighted entry: an item id plus the count range it rolls. */
    public record Entry(ResourceLocation item, int weight, int minCount, int maxCount) {
        public static final MapCodec<Entry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(Entry::item),
                Codec.intRange(1, 1000).optionalFieldOf("weight", 1).forGetter(Entry::weight),
                Codec.intRange(1, 64).optionalFieldOf("min_count", 1).forGetter(Entry::minCount),
                Codec.intRange(1, 64).optionalFieldOf("max_count", 1).forGetter(Entry::maxCount)
        ).apply(instance, Entry::new));

        public ItemStack stack() {
            return net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(item)
                    .map(ItemStack::new)
                    .orElse(ItemStack.EMPTY);
        }

        public ItemStack create(RandomSource random) {
            ItemStack stack = stack();
            if (stack.isEmpty()) {
                return stack;
            }
            int count = minCount >= maxCount ? minCount : minCount + random.nextInt(maxCount - minCount + 1);
            stack.setCount(count);
            return stack;
        }
    }
}
