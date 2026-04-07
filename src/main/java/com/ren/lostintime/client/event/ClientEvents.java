package com.ren.lostintime.client.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.model.skeleton.DodoSkeleton;
import com.ren.lostintime.client.particles.BleedingDropletParticle;
import com.ren.lostintime.client.particles.BleedingStreamParticle;
import com.ren.lostintime.client.particles.BleedingUnderwaterParticle;
import com.ren.lostintime.client.particles.SleepingParticle;
import com.ren.lostintime.client.model.ModModelLayers;
import com.ren.lostintime.client.model.blockentities.*;
import com.ren.lostintime.client.renderer.entity.*;
import com.ren.lostintime.client.renderer.entity.misc.LITBoatRenderer;
import com.ren.lostintime.client.renderer.entity.skeleton.SkeletonRenderer;
import com.ren.lostintime.client.renderer.projectile.GuardianSpikeRenderer;
import com.ren.lostintime.client.renderer.projectile.ThrownKnifeRenderer;
import com.ren.lostintime.client.renderer.blockentities.TransfiguratorBERenderer;
import com.ren.lostintime.client.screen.IdentificationScreen;
import com.ren.lostintime.client.screen.SoulConfiguratorScreen;
import com.ren.lostintime.client.screen.SoulExtractorScreen;
import com.ren.lostintime.client.screen.TransfiguratorScreen;
import com.ren.lostintime.client.screen.overlay.BleedingOverlay;
import com.ren.lostintime.common.init.*;
import com.ren.lostintime.common.util.LITWoodTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.IDENTIFICATION_TABLE_MENU.get(), IdentificationScreen::new);
        MenuScreens.register(MenuInit.SOUL_EXTRACTOR_MENU.get(), SoulExtractorScreen::new);
        MenuScreens.register(MenuInit.SOUL_CONFIGURATOR_MENU.get(), SoulConfiguratorScreen::new);
        MenuScreens.register(MenuInit.TRANSFIGURATOR_MENU.get(), TransfiguratorScreen::new);

        Sheets.addWoodType(LITWoodTypes.ARAUCARIOXYLON);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.DODO.get(), DodoRenderer::new);
        event.registerEntityRenderer(EntityInit.ANOMALOCARIS.get(), AnomalocarisRenderer::new);
        event.registerEntityRenderer(EntityInit.BOTHRIOLEPIS.get(), BothriolepisRenderer::new);
        event.registerEntityRenderer(EntityInit.HYLONOMUS.get(), HylonomusRenderer::new);
        event.registerEntityRenderer(EntityInit.ENDOCERAS.get(), EndocerasRenderer::new);
        event.registerEntityRenderer(EntityInit.DAEODON.get(), DaeodonRenderer::new);
        event.registerEntityRenderer(EntityInit.LEPTICTIDIUM.get(), LeptictidiumRenderer::new);
        event.registerEntityRenderer(EntityInit.SCUTOSAURUS.get(), ScutosaurusRenderer::new);
        event.registerEntityRenderer(EntityInit.PLESIOSAURUS.get(), PlesiosaurusRenderer::new);
        event.registerEntityRenderer(EntityInit.MASTODONSAURUS.get(), MastodonsaurusRenderer::new);
        event.registerEntityRenderer(EntityInit.HELICOPRION.get(), HelicoprionRenderer::new);
        event.registerEntityRenderer(EntityInit.DEINONYCHUS.get(), DeinonychusRenderer::new);
        event.registerEntityRenderer(EntityInit.PTERYGOTUS.get(), PterygotusRenderer::new);
        event.registerEntityRenderer(EntityInit.KALLIGRAMMATIDAE.get(), KalligrammatidaeRenderer::new);

        event.registerEntityRenderer(EntityInit.GUARDIAN_SPIKE.get(), GuardianSpikeRenderer::new);
        event.registerEntityRenderer(EntityInit.LIT_THROWN_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityInit.THROWN_KNIFE.get(), ThrownKnifeRenderer::new);
        event.registerEntityRenderer(EntityInit.LIT_BOAT.get(), p_174094_ -> new LITBoatRenderer(p_174094_, false));
        event.registerEntityRenderer(EntityInit.LIT_CHEST_BOAT.get(), p_174094_ -> new LITBoatRenderer(p_174094_, true));

        event.registerEntityRenderer(EntityInit.LIT_SKELETON.get(), SkeletonRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityInit.TRANSFIGURATOR.get(), TransfiguratorBERenderer::new);

        event.registerBlockEntityRenderer(BlockEntityInit.LIT_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.LIT_HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.TRANSFIGURATOR_EGG, TransfiguratorEgg::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TRANSFIGURATOR_EGG_2, TransfiguratorEgg2::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TRANSFIGURATOR_EMBRYO, TransfiguratorEmbryo::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TRANSFIGURATOR_PLANT, TransfiguratorPlant::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.DODO_LAYER, DodoSkeleton::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.ARAUCARIOXYLON_BOAT_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(ModModelLayers.ARAUCARIOXYLON_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);


    }

    @SubscribeEvent
    public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticlesInit.SLEEPING_PARTICLES.get(), SleepingParticle.Factory::new);
        event.registerSpriteSet(ParticlesInit.BLEEDING_DROPLET.get(), BleedingDropletParticle.Provider::new);
        event.registerSpriteSet(ParticlesInit.BLEEDING_STREAM.get(), BleedingStreamParticle.Provider::new);
        event.registerSpriteSet(ParticlesInit.BLEEDING_UNDERWATER.get(), BleedingUnderwaterParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("bleeding_hud", BleedingOverlay.HUD_BLEEDING);
    }
}
