package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class LITTags {

    public static class Items{

        public static final TagKey<Item> SEEDS = tag("seeds");
        public static final TagKey<Item> FRUITS = tag("fruits");
        public static final TagKey<Item> UNIDENTIFIED_FOSSIL = tag("unidentified_fossil");
        public static final TagKey<Item> QUATERNARY_FOSSIL = tag("quaternary_fossil");
        public static final TagKey<Item> NEOGENE_FOSSIL = tag("neogene_fossil");
        public static final TagKey<Item> PALEOGENE_FOSSIL = tag("paleogene_fossil");
        public static final TagKey<Item> CRETACEOUS_FOSSIL = tag("cretaceous_fossil");
        public static final TagKey<Item> JURASSIC_FOSSIL = tag("jurassic_fossil");
        public static final TagKey<Item> TRIASSIC_FOSSIL = tag("triassic_fossil");
        public static final TagKey<Item> PERMIAN_FOSSIL = tag("permian_fossil");
        public static final TagKey<Item> CARBONIFEROUS_FOSSIL = tag("carboniferous_fossil");
        public static final TagKey<Item> DEVONIAN_FOSSIL = tag("devonian_fossil");
        public static final TagKey<Item> SILURIAN_FOSSIL = tag("silurian_fossil");
        public static final TagKey<Item> ORDOVICIAN_FOSSIL = tag("ordovician_fossil");
        public static final TagKey<Item> CAMBRIAN_FOSSIL = tag("cambrian_fossil");

        public static final TagKey<Item> GOLDEN_FOODS = tag("golden_foods");

        public static final TagKey<Item> DODO_FOOD = tag("dodo_food");

        public static TagKey<Item> tag(String name){
            return TagKey.create(Registries.ITEM, new ResourceLocation(LostInTime.MODID, name));
        }
    }

    public static class Blocks{

        public static final TagKey<Block> DODO_SOILS = tag("dodo_soils");

        public static TagKey<Block> tag(String name){
            return TagKey.create(Registries.BLOCK, new ResourceLocation(LostInTime.MODID, name));
        }
    }

}
