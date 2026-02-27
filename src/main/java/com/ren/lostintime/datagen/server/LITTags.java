package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class LITTags {

    public static class Items{

        public static final TagKey<Item> SEEDS = tag("seeds");
        public static final TagKey<Item> FRUITS = tag("fruits");
        public static final TagKey<Item> UNIDENTIFIED_FOSSIL = tag("unidentified_fossil");

        public static final TagKey<Item> DODO_FOOD = tag("dodo_food");

        public static final TagKey<Item> ANOMALOCARIS_BREEDABLE_FOOD = tag("anomalocaris_breedable_food");
        public static final TagKey<Item> ENDOCERAS_BREEDABLE_FOOD = tag("endoceras_breedable_food");
        public static final TagKey<Item> HYLONOMUS_BREEDABLE_FOOD = tag("hylonomus_breedable_food");
        public static final TagKey<Item> BOTHRIOLEPIS_FOOD = tag("bothrilepis_food");
        public static final TagKey<Item> DAEODON_CONTROL_ITEMS = tag("daeodon_control_items");

        public static TagKey<Item> tag(String name){
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, name));
        }
    }

    public static class Blocks{

        public static final TagKey<Block> DODO_SOILS = tag("dodo_soils");

        public static TagKey<Block> tag(String name){
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, name));
        }
    }

    public static class Biomes{

        public static final TagKey<Biome> DODO_CAN_SPAWN = tag("dodo_can_spawn");
        public static final TagKey<Biome> ANOMALOCARIS_CAN_SPAWN = tag("anomalocaris_can_spawn");
        public static final TagKey<Biome> ENDOCERAS_CAN_SPAWN = tag("endoceras_can_spawn");
        public static final TagKey<Biome> HYLONOMUS_CAN_SPAWN = tag("hylonomus_can_spawn");

        public static TagKey<Biome> tag(String name){
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, name));
        }
    }

}
