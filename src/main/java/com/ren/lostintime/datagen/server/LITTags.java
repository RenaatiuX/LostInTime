package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class LITTags {

    public static class Items{

        public static final TagKey<Item> SEEDS = tag("seeds");

        public static TagKey<Item> tag(String name){
            return TagKey.create(Registries.ITEM, new ResourceLocation(LostInTime.MODID, name));
        }
    }

}
