package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SoundInit {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, LostInTime.MODID);

    public static final Supplier<SoundEvent> HEARTBEAT = SOUNDS.register("heartbeat",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "heartbeat")));

}
