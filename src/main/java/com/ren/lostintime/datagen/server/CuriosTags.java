package com.ren.lostintime.datagen.server;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosTags {

    public static class Items{

        public static final TagKey<Item> BELT = tag("belt");

        public static TagKey<Item> tag(String name){
            return TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, name));
        }
    }

}
