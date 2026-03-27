package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.network.SyncDiscoveredEntites;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkInit {

    public static final String channelVersion = "1.0.0";

    public static final SimpleChannel DISCOVERED_ENTITIES_CHANNEL = createChannel("discovered_entities");

    public static void registerPackets(){
        int id = 0;
        DISCOVERED_ENTITIES_CHANNEL.registerMessage(id++, SyncDiscoveredEntites.class, SyncDiscoveredEntites::toBytes, SyncDiscoveredEntites::new, SyncDiscoveredEntites::handle);
    }

    public static SimpleChannel createChannel(String name){
        return NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, name), () -> channelVersion, channelVersion::equals, channelVersion::equals);
    }
}
