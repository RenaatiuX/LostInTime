package com.ren.lostintime.common.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerEvents {

    //TODO change this name to CommonEvents cause without specifying a side this will be executed on both, the client and server side which is also required for the entity attributes
    @SubscribeEvent
    public static void registerAttr(EntityAttributeCreationEvent event) {
        event.put(EntityInit.DODO.get(), Dodo.createAttributes().build());
    }
}
