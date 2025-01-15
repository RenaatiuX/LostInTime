package com.ren.lostintime.client.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.render.entity.DodoRender;
import com.ren.lostintime.client.render.projectile.GuardianSpikeRender;
import com.ren.lostintime.common.entity.projectile.GuardianSpike;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.DODO.get(), DodoRender::new);

        event.registerEntityRenderer(EntityInit.GUARDIAN_SPIKE.get(), GuardianSpikeRender::new);
    }
}
