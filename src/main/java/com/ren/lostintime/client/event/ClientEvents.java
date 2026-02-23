package com.ren.lostintime.client.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.particles.SleepingParticle;
import com.ren.lostintime.client.render.entity.*;
import com.ren.lostintime.client.render.projectile.GuardianSpikeRender;
import com.ren.lostintime.client.screen.IdentificationScreen;
import com.ren.lostintime.client.screen.SoulConfiguratorScreen;
import com.ren.lostintime.client.screen.SoulExtractorScreen;
import com.ren.lostintime.client.screen.TransfiguratorScreen;
import com.ren.lostintime.common.entity.projectile.GuardianSpike;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.MenuInit;
import com.ren.lostintime.common.init.ParticlesInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.DODO.get(), DodoRender::new);
        event.registerEntityRenderer(EntityInit.ANOMALOCARIS.get(), AnomalocarisRender::new);
        event.registerEntityRenderer(EntityInit.BOTHRIOLEPIS.get(), BothriolepisRender::new);
        event.registerEntityRenderer(EntityInit.HYLONOMUS.get(), HylonomusRender::new);
        event.registerEntityRenderer(EntityInit.ENDOCERAS.get(), EndocerasRender::new);

        event.registerEntityRenderer(EntityInit.GUARDIAN_SPIKE.get(), GuardianSpikeRender::new);
        event.registerEntityRenderer(EntityInit.LIT_THROWN_EGG.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticlesInit.SLEEPING_PARTICLES.get(), SleepingParticle.Factory::new);
    }
}
