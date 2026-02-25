package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ActivitInit {

    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, LostInTime.MODID);

    public static final RegistryObject<Activity> HURT_GRABBED_PREY = register("hurt_grabbed_prey");
    public static final RegistryObject<Activity> GRAB_PREY = register("grab_prey");
    public static final RegistryObject<Activity> MATING = register("mating");


    public static RegistryObject<Activity> register(String name){
        return ACTIVITIES.register(name, () -> new Activity(name));
    }
}
