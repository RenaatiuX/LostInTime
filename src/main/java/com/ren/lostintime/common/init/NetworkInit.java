package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkInit {

    public static final String channelVersion = "1.0.0";

    public static void registerPackets(){
        int id = 0;
    }

    public static SimpleChannel createChannel(String name){
        return NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, name), () -> channelVersion, channelVersion::equals, channelVersion::equals);
    }
}
