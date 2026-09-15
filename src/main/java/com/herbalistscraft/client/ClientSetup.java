package com.herbalistscraft.client;

import com.herbalistscraft.ClientBridge;
import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.registry.ModBlockEntities;
import com.herbalistscraft.registry.ModMenus;
import com.herbalistscraft.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** Every client-only registration lives here, on the mod event bus. */
@EventBusSubscriber(modid = HerbalistsCraft.MODID, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientBridge.installLocalPlayer(() -> Minecraft.getInstance().player);
            ClientBridge.installJournalOpener(ClientHooks::openJournal);
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.HERBAL_MILL.get(), HerbalMillScreen::new);
        event.register(ModMenus.HERBALISTS_TABLE.get(), HerbalistTableScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK.get(), DryingRackRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.HERBAL_SPARK.get(),
                sprites -> new HerbalParticle.Provider(sprites, HerbalParticle.Kind.HERBAL));
        event.registerSpriteSet(ModParticles.FROST_SPARK.get(),
                sprites -> new HerbalParticle.Provider(sprites, HerbalParticle.Kind.FROST));
        event.registerSpriteSet(ModParticles.EMBER_SPARK.get(),
                sprites -> new HerbalParticle.Provider(sprites, HerbalParticle.Kind.EMBER));
        event.registerSpriteSet(ModParticles.TOXIC_SMOKE.get(),
                sprites -> new HerbalParticle.Provider(sprites, HerbalParticle.Kind.TOXIC));
        event.registerSpriteSet(ModParticles.DRIED_LEAF.get(),
                sprites -> new HerbalParticle.Provider(sprites, HerbalParticle.Kind.LEAF));
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                HerbalistsCraft.MODID, "toxin_indicator"), HudIndicators::renderToxin);
    }
}
