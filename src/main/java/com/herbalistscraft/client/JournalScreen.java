package com.herbalistscraft.client;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbDefinition;
import com.herbalistscraft.herb.HerbProperty;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.herb.HerbSeason;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.knowledge.Knowledge;
import com.herbalistscraft.registry.ModHerbs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * The Herbalist's Journal: the mod's own book, six tabs down the spine, a list on the left and
 * a detail page on the right. Everything in it comes from the player's own {@link Knowledge}, so
 * a herb they have only heard of is listed as unknown until they have actually held it.
 */
public class JournalScreen extends Screen {
    public enum Tab {
        HERBS("gui.herbalistscraft.journal.tab.herbs"),
        PROPERTIES("gui.herbalistscraft.journal.tab.properties"),
        RECIPES("gui.herbalistscraft.journal.tab.recipes"),
        RESEARCH("gui.herbalistscraft.journal.tab.research"),
        SEASONAL("gui.herbalistscraft.journal.tab.seasonal"),
        BIOMES("gui.herbalistscraft.journal.tab.biomes");

        private final String key;

        Tab(String key) {
            this.key = key;
        }

        public Component title() {
            return Component.translatable(key);
        }

        public String shortKey() {
            return key + ".short";
        }
    }

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "textures/gui/journal.png");
    private static final int TEXT = 0x2F2A22;

    private final Tab initialTab;
    private Tab tab;
    private int selected;
    private int scroll;

    public JournalScreen(Tab initialTab) {
        super(Component.translatable("gui.herbalistscraft.journal.title"));
        this.initialTab = initialTab;
        this.tab = initialTab;
    }

    @Override
    protected void init() {
        int left = (width - GuiLayout.Journal.WIDTH) / 2;
        int top = (height - GuiLayout.Journal.HEIGHT) / 2;
        for (int index = 0; index < Tab.values().length; index++) {
            Tab candidate = Tab.values()[index];
            int y = top + GuiLayout.Journal.TAB_Y + index * GuiLayout.Journal.TAB_GAP;
            addRenderableWidget(Button.builder(Component.translatable(candidate.shortKey()), button -> {
                tab = candidate;
                selected = 0;
                scroll = 0;
            }).bounds(left + GuiLayout.Journal.TAB_X, y, GuiLayout.Journal.TAB_W, GuiLayout.Journal.TAB_H).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = (width - GuiLayout.Journal.WIDTH) / 2;
        int top = (height - GuiLayout.Journal.HEIGHT) / 2;
        graphics.blit(TEXTURE, left, top, 0, 0, GuiLayout.Journal.WIDTH, GuiLayout.Journal.HEIGHT);
        graphics.drawString(font, title, left + GuiLayout.Journal.TITLE_X0, top + GuiLayout.Journal.TITLE_Y0, TEXT,
                false);
        graphics.drawString(font, tab.title(), left + GuiLayout.Journal.LIST_X0,
                top + GuiLayout.Journal.LIST_Y0 - 10, 0x6B5A3E, false);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderList(graphics, left, top);
        renderDetail(graphics, left, top);
    }

    private void renderList(GuiGraphics graphics, int left, int top) {
        List<Component> rows = rows();
        int x = left + GuiLayout.Journal.LIST_X0;
        int y = top + GuiLayout.Journal.LIST_Y0;
        for (int index = scroll; index < Math.min(rows.size(), scroll + GuiLayout.Journal.ROWS_VISIBLE); index++) {
            int colour = index == selected ? 0x6B4A1E : TEXT;
            graphics.drawString(font, rows.get(index), x, y, colour, false);
            y += GuiLayout.Journal.ROW_HEIGHT;
        }
        if (rows.isEmpty()) {
            graphics.drawString(font, Component.translatable("gui.herbalistscraft.journal.empty"),
                    x, y, 0x7A6A55, false);
        }
    }

    private void renderDetail(GuiGraphics graphics, int left, int top) {
        int x = left + GuiLayout.Journal.DETAIL_X0;
        int y = top + GuiLayout.Journal.DETAIL_Y0;
        for (Component line : detail()) {
            graphics.drawString(font, line, x, y, TEXT, false);
            y += 11;
        }
    }

    /** The left-hand list for the current tab, already filtered by what the player knows. */
    private List<Component> rows() {
        Knowledge knowledge = Discovery.knowledge(minecraft.player);
        List<Component> rows = new ArrayList<>();
        switch (tab) {
            case HERBS -> {
                if (minecraft.level == null) {
                    return rows;
                }
                var registry = HerbRegistry.registry(minecraft.level.registryAccess());
                for (var entry : registry.entrySet()) {
                    ResourceLocation id = entry.getKey().location();
                    boolean known = knowledge.knowsHerb(id);
                    rows.add(known
                            ? Component.translatable("item." + id.toLanguageKey())
                            : Component.translatable("gui.herbalistscraft.journal.unknown_herb"));
                }
            }
            case PROPERTIES -> {
                for (HerbProperty property : HerbProperty.VALUES) {
                    rows.add(knowledge.knowsProperty(property)
                            ? property.displayName()
                            : Component.translatable("tooltip.herbalistscraft.property.unknown"));
                }
            }
            case RECIPES -> {
                for (ResourceLocation id : knowledge.recipes()) {
                    rows.add(Component.translatable("item." + id.toLanguageKey()));
                }
            }
            case RESEARCH -> {
                for (ResourceLocation id : knowledge.medicines()) {
                    rows.add(Component.translatable("item." + id.toLanguageKey()));
                }
            }
            case SEASONAL -> {
                for (HerbSeason season : HerbSeason.values()) {
                    rows.add(knowledge.seasons().contains(season)
                            ? season.displayName()
                            : Component.translatable("gui.herbalistscraft.journal.unknown_season"));
                }
            }
            case BIOMES -> {
                for (String group : knowledge.biomeGroups()) {
                    rows.add(Component.translatable("biome_group." + HerbalistsCraft.MODID + "." + group));
                }
            }
        }
        return rows;
    }

    /** The right-hand page: herb lore, properties, preferred seasons and biomes. */
    private List<Component> detail() {
        Knowledge knowledge = Discovery.knowledge(minecraft.player);
        List<Component> lines = new ArrayList<>();
        switch (tab) {
            case HERBS -> {
                if (minecraft.level == null) {
                    return lines;
                }
                List<ResourceLocation> known = new ArrayList<>(knowledge.herbs());
                if (known.isEmpty()) {
                    return lines;
                }
                ResourceLocation id = known.get(Math.min(selected, known.size() - 1));
                HerbDefinition definition = HerbRegistry.get(minecraft.level.registryAccess(),
                        key(id)).orElse(null);
                lines.add(Component.translatable("item." + id.toLanguageKey()).withStyle(ChatFormatting.BOLD));
                if (definition == null) {
                    return lines;
                }
                lines.add(Component.translatable(definition.loreKey(id.getPath())).withStyle(ChatFormatting.ITALIC));
                lines.add(Component.translatable("gui.herbalistscraft.journal.fact.category",
                        definition.category().displayName()));
                lines.add(Component.translatable("gui.herbalistscraft.journal.fact.rarity",
                        definition.rarity().displayName()));
                lines.add(Component.translatable("gui.herbalistscraft.journal.fact.toxicity",
                        definition.toxicity().displayName()));
                StringBuilder seasons = new StringBuilder();
                for (HerbSeason season : definition.seasons()) {
                    if (seasons.length() > 0) {
                        seasons.append(", ");
                    }
                    seasons.append(season.displayName().getString());
                }
                lines.add(Component.translatable("gui.herbalistscraft.journal.fact.seasons", seasons.toString()));
                lines.add(Component.translatable("gui.herbalistscraft.journal.fact.biomes",
                        String.join(", ", definition.biomeGroups())));
            }
            case PROPERTIES -> {
                HerbProperty property = HerbProperty.VALUES[
                        Math.min(selected, HerbProperty.VALUES.length - 1)];
                lines.add(property.displayName().copy().withStyle(ChatFormatting.BOLD));
                if (knowledge.knowsProperty(property)) {
                    lines.add(property.hint());
                } else {
                    lines.add(Component.translatable("tooltip.herbalistscraft.property.unknown"));
                }
            }
            case RECIPES, RESEARCH -> {
                List<ResourceLocation> ids = new ArrayList<>(tab == Tab.RECIPES
                        ? knowledge.recipes() : knowledge.medicines());
                if (ids.isEmpty()) {
                    return lines;
                }
                ResourceLocation id = ids.get(Math.min(selected, ids.size() - 1));
                lines.add(Component.translatable("item." + id.toLanguageKey()).withStyle(ChatFormatting.BOLD));
                lines.add(Component.translatable("medicine." + id.toLanguageKey() + ".description"));
            }
            case SEASONAL -> {
                for (HerbSeason season : HerbSeason.values()) {
                    boolean known = knowledge.seasons().contains(season);
                    lines.add(Component.translatable("gui.herbalistscraft.journal.season.line",
                            season.displayName(),
                            known ? (int) (season.growthMultiplier() * 100) : -1));
                }
            }
            case BIOMES -> {
                lines.add(Component.translatable("gui.herbalistscraft.journal.biomes.found",
                        knowledge.biomeCount()));
                lines.add(Component.translatable("gui.herbalistscraft.journal.biomes.hint"));
            }
        }
        return lines;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int left = (width - GuiLayout.Journal.WIDTH) / 2;
            int top = (height - GuiLayout.Journal.HEIGHT) / 2;
            if (inside(mouseX, mouseY, left + GuiLayout.Journal.LIST_X0, top + GuiLayout.Journal.LIST_Y0,
                    GuiLayout.Journal.LIST_X1, GuiLayout.Journal.LIST_Y1)) {
                int row = (int) ((mouseY - (top + GuiLayout.Journal.LIST_Y0)) / GuiLayout.Journal.ROW_HEIGHT);
                int index = scroll + Math.max(0, row);
                if (index < rows().size()) {
                    selected = index;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int size = rows().size();
        int max = Math.max(0, size - GuiLayout.Journal.ROWS_VISIBLE);
        scroll = Math.max(0, Math.min(max, scroll + (scrollY > 0 ? -1 : 1)));
        return true;
    }

    private static boolean inside(double x, double y, int x0, int y0, int x1, int y1) {
        return x >= x0 && x <= x1 && y >= y0 && y <= y1;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** Item stack of a herb, for the icon row; empty when the item is missing. */
    static ItemStack iconOf(ResourceLocation herb) {
        return new ItemStack(com.herbalistscraft.herb.HerbForms.item(key(herb),
                com.herbalistscraft.herb.HerbForm.FRESH));
    }

    /** Helper so the screen never has to build registry keys by hand. */
    static net.minecraft.resources.ResourceKey<HerbDefinition> key(ResourceLocation herb) {
        return net.minecraft.resources.ResourceKey.create(ModHerbs.REGISTRY, herb);
    }
}
