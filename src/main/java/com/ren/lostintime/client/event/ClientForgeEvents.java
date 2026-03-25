package com.ren.lostintime.client.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.MobEffectInit;
import com.ren.lostintime.common.init.SoundInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player != null && !minecraft.isPaused() && player.hasEffect(MobEffectInit.BLEEDING.get())) {

                if (player.tickCount % 40 == 0) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundInit.HEARTBEAT.get(), 1.0F, 1.0F));
                }
            }
        }
    }
}
