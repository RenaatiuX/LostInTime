package com.ren.lostintime.datagen.server;

import com.ren.lostintime.common.init.CuriousSlotInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosTags {

    public static class Items{

        public static final TagKey<Item> BELT_SLOT_TAG = curiousTag(CuriousSlotInit.BELT_SLOT);

        public static TagKey<Item> curiousTag(String name){
            return TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, name));
        }
    }

}
