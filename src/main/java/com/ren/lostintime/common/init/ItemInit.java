package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.item.GoldenEyeItem;
import com.ren.lostintime.common.item.GuardianSpikeItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LostInTime.MODID);

    //MISC
    public static final RegistryObject<Item> AMBER = registerSimple("amber");
    public static final RegistryObject<Item> GUARDIAN_SPIKE = ITEMS.register("guardian_spike",
            () -> new GuardianSpikeItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ECTOPLASM = registerSimple("ectoplasm");
    public static final RegistryObject<Item> SOUL_ASH =  registerSimple("soul_ash");
    public static final RegistryObject<Item> SOUL_GRUME =  registerSimple("soul_grume");
    public static final RegistryObject<Item> EMPTY_VITAL_PATTERN =  registerSimple("empty_vital_pattern");
    public static final RegistryObject<Item> INFORMATION_DOME = registerSimple("information_dome");
    public static final RegistryObject<Item> SOUL_POWDER = registerSimple("soul_powder");
    public static final RegistryObject<Item> PANEL = registerSimple("panel");
    public static final RegistryObject<Item> REDSTONE_CHIP = registerSimple("redstone_chip");

    //ASPECT
    public static final RegistryObject<Item> ASPECT_DIFFERENTIATION = registerSimple("aspect_differentiation");
    public static final RegistryObject<Item> ASPECT_EMERGENCE = registerSimple("aspect_emergence");
    public static final RegistryObject<Item> ASPECT_INTEGRATION = registerSimple("aspect_integration");
    public static final RegistryObject<Item> ASPECT_STRUCTURING = registerSimple("aspect_structuring");
    public static final RegistryObject<Item> ASPECT_TRANSIENCE = registerSimple("aspect_transience");
    public static final RegistryObject<Item> ASPECT_ABUNDANCE = registerSimple("aspect_abundance");
    public static final RegistryObject<Item> ASPECT_CONTINUITY = registerSimple("aspect_continuity");
    public static final RegistryObject<Item> ASPECT_MAGNITUDE = registerSimple("aspect_magnitude");
    public static final RegistryObject<Item> ASPECT_PROLIFERATION = registerSimple("aspect_proliferation");
    public static final RegistryObject<Item> ASPECT_RECOVERY = registerSimple("aspect_recovery");
    public static final RegistryObject<Item> ASPECT_REFINEMENT = registerSimple("aspect_refinement");
    public static final RegistryObject<Item> ASPECT_RESILIENCE = registerSimple("aspect_resilience");

    public static final RegistryObject<Item> ZIRCON =  registerSimple("zircon");

    //CATALYST
    public static final RegistryObject<Item> AMETHYST_CATALYST = registerSimple("amethyst_catalyst");
    public static final RegistryObject<Item> BLUE_ICE_CATALYST = registerSimple("blue_ice_catalyst");
    public static final RegistryObject<Item> CALCITE_CATALYST = registerSimple("calcite_catalyst");
    public static final RegistryObject<Item> COAL_CATALYST = registerSimple("coal_catalyst");
    public static final RegistryObject<Item> COPPER_CATALYST = registerSimple("copper_catalyst");
    public static final RegistryObject<Item> EMERALD_CATALYST = registerSimple("emerald_catalyst");
    public static final RegistryObject<Item> GOLD_CATALYST = registerSimple("gold_catalyst");
    public static final RegistryObject<Item> IRON_CATALYST = registerSimple("iron_catalyst");
    public static final RegistryObject<Item> LAPIS_LAZULI_CATALYST = registerSimple("lapis_lazuli_catalyst");
    public static final RegistryObject<Item> QUARTZ_CATALYST = registerSimple("quartz_catalyst");
    public static final RegistryObject<Item> REDSTONE_CATALYST = registerSimple("redstone_catalyst");
    public static final RegistryObject<Item> ZIRCON_CATALYST = registerSimple("zircon_catalyst");

    //FOSSIL
    public static final RegistryObject<Item> CAMBRIAN_FOSSIL = registerSimple("cambrian_fossil");
    public static final RegistryObject<Item> ORDOVICIAN_FOSSIL = registerSimple("ordovician_fossil");
    public static final RegistryObject<Item> SILURIAN_FOSSIL = registerSimple("silurian_fossil");
    public static final RegistryObject<Item> DEVONIAN_FOSSIL = registerSimple("devonian_fossil");
    public static final RegistryObject<Item> CARBONIFEROUS_FOSSIL = registerSimple("carboniferous_fossil");
    public static final RegistryObject<Item> PERMIAN_FOSSIL = registerSimple("permian_fossil");
    public static final RegistryObject<Item> TRIASSIC_FOSSIL = registerSimple("triassic_fossil");
    public static final RegistryObject<Item> JURASSIC_FOSSIL = registerSimple("jurassic_fossil");
    public static final RegistryObject<Item> CRETACEOUS_FOSSIL = registerSimple("cretaceous_fossil");
    public static final RegistryObject<Item> PALEOGENE_FOSSIL = registerSimple("paleogene_fossil");
    public static final RegistryObject<Item> NEOGENE_FOSSIL =  registerSimple("neogene_fossil");
    public static final RegistryObject<Item> QUATERNARY_FOSSIL =  registerSimple("quaternary_fossil");

    //CONFIGURATION
    public static final RegistryObject<Item> ANOMALOCARIS_SOUL_CFC = registerSimple("anomalocaris_soul_configuration");
    public static final RegistryObject<Item> BOTHRIOLEPIS_SOUL_CFC = registerSimple("bothriolepis_soul_configuration");
    public static final RegistryObject<Item> DAEODON_SOUL_CFC = registerSimple("daeodon_soul_configuration");
    public static final RegistryObject<Item> DEINONYCHUS_SOUL_CFC = registerSimple("deinonychus_soul_configuration");
    public static final RegistryObject<Item> DODO_SOUL_CFC = registerSimple("dodo_soul_configuration");
    public static final RegistryObject<Item> EMPTY_SOUL_CFC = registerSimple("empty_soul_configuration");
    public static final RegistryObject<Item> ENDOCERAS_SOUL_CFC = registerSimple("endoceras_soul_configuration");
    public static final RegistryObject<Item> HYLONOMUS_SOUL_CFC = registerSimple("hylonomus_soul_configuration");
    public static final RegistryObject<Item> LEPTICTIDIUM_SOUL_CFC = registerSimple("leptictidium_soul_configuration");
    public static final RegistryObject<Item> MASTODONSAURUS_SOUL_CFC = registerSimple("mastodonsaurus_soul_configuration");
    public static final RegistryObject<Item> PLESIOSAURUS_SOUL_CFC = registerSimple("plesiosaurus_soul_configuration");
    public static final RegistryObject<Item> PTERYGOTUS_SOUL_CFC = registerSimple("pterygotus_soul_configuration");
    public static final RegistryObject<Item> SCUTOSAURUS_SOUL_CFC = registerSimple("scutosaurus_soul_configuration");

    //PATTERN
    public static final RegistryObject<Item> PROTOTAXITES_VITAL_PATTERN = registerSimple("prototaxites_vital_pattern");
    public static final RegistryObject<Item> BARREL_SPONGE_VITAL_PATTERN = registerSimple("barrel_sponge_vital_pattern");
    public static final RegistryObject<Item> GLASS_SPONGE_VITAL_PATTERN = registerSimple("glass_sponge_vital_pattern");
    public static final RegistryObject<Item> PIPE_SPONGE_VITAL_PATTERN = registerSimple("pipe_sponge_vital_pattern");
    public static final RegistryObject<Item> TREE_SPONGE_VITAL_PATTERN = registerSimple("tree_sponge_vital_pattern");
    public static final RegistryObject<Item> VASE_SPONGE_VITAL_PATTERN = registerSimple("vase_sponge_vital_pattern");
    public static final RegistryObject<Item> WOOL_SPONGE_VITAL_PATTERN = registerSimple("wool_sponge_vital_pattern");

    public static final RegistryObject<Item> DODO_FOSSIL = registerSimple("dodo_fossil");
    public static final RegistryObject<Item> DODO_SKULL =  registerSimple("dodo_skull");
    public static final RegistryObject<Item> BOTHRIOLEPIS_FOSSIL = registerSimple("bothriolepis_fossil");
    public static final RegistryObject<Item> ANOMALOCARIS_FOSSIL = registerSimple("anomalocaris_fossil");
    public static final RegistryObject<Item> DAEODON_FOSSIL = registerSimple("daeodon_fossil");
    public static final RegistryObject<Item> DAEODON_SKULL = registerSimple("daeodon_skull");
    public static final RegistryObject<Item> DEINONYCHUS_FOSSIL = registerSimple("deinonychus_fossil");
    public static final RegistryObject<Item> DEINONYCHUS_SKULL = registerSimple("deinonychus_skull");
    public static final RegistryObject<Item> ENDOCERAS_FOSSIL = registerSimple("endoceras_fossil");
    public static final RegistryObject<Item> HYLONOMUS_FOSSIL = registerSimple("hylonomus_fossil");
    public static final RegistryObject<Item> LEPTICTIDIUM_FOSSIL= registerSimple("leptictidium_fossil");
    public static final RegistryObject<Item> MASTODONSAURUS_FOSSIL = registerSimple("mastodonsaurus_fossil");
    public static final RegistryObject<Item> MASTODONSAURUS_SKULL = registerSimple("mastodonsaurus_skull");
    public static final RegistryObject<Item> PLESIOSAURUS_FOSSIL = registerSimple("plesiosaurus_fossil");
    public static final RegistryObject<Item> PLESIOSAURUS_SKULL = registerSimple("plesiosaurus_skull");
    public static final RegistryObject<Item> PTERYGOTUS_FOSSIL = registerSimple("pterygotus_fossil");
    public static final RegistryObject<Item> SCUTOSAURUS_FOSSIL = registerSimple("scutosaurus_fossil");
    public static final RegistryObject<Item> SCUTOSAURUS_SKULL = registerSimple("scutosaurus_skull");

    public static final RegistryObject<Item> DODO_FOSSIL_MOUNT = registerSimple("dodo_fossil_mount");
    public static final RegistryObject<Item> EMPTY_SKELETON_MOUNT = registerSimple("empty_skeleton_mount");

    //EQUIP
    public static final RegistryObject<Item> GOLDEN_EYE = ITEMS.register("golden_eye",
            () -> new GoldenEyeItem(new Item.Properties()));

    //FOOD
    public static final RegistryObject<Item> RAW_DODO = registerFood("raw_dodo", FoodInit.RAW_DODO);
    public static final RegistryObject<Item> COOKED_DODO = registerFood("cooked_dodo", FoodInit.COOKED_DODO);
    public static final RegistryObject<Item> RAW_ANOMALOCARIS = registerFood("raw_anomalocaris", FoodInit.RAW_ANOMALOCARIS);
    public static final RegistryObject<Item> COOKED_ANOMALOCARIS = registerFood("cooked_anomalocaris", FoodInit.COOKED_ANOMALOCARIS);
    public static final RegistryObject<Item> RAW_BOTHRIOLEPIS = registerFood("raw_bothriolepis", FoodInit.RAW_BOTHRIOLEPIS);
    public static final RegistryObject<Item> COOKED_BOTHRIOLEPIS = registerFood("cooked_bothriolepis", FoodInit.COOKED_BOTHRIOLEPIS);
    public static final RegistryObject<Item> RAW_DAEODON = registerFood("raw_daeodon", FoodInit.RAW_DAEODON);
    public static final RegistryObject<Item> COOKED_DAEODON = registerFood("cooked_daeodon", FoodInit.COOKED_DAEODON);
    public static final RegistryObject<Item> RAW_ENDOCERAS = registerFood("raw_endoceras",  FoodInit.RAW_ENDOCERAS);
    public static final RegistryObject<Item> COOKED_ENDOCERAS = registerFood("cooked_endoceras",  FoodInit.COOKED_ENDOCERAS);

    public static final RegistryObject<Item> MANGO = registerFood("mango", FoodInit.MANGO);

    //BLOCKS
    public static final RegistryObject<Item> BOTHRIOLEPIS_ROE = ITEMS.register("bothriolepis_roe",
            () -> new PlaceOnWaterBlockItem(BlockInit.BOTHRIOLEPIS_ROE.get(), new Item.Properties()));

    //BUCKETS


    //SPAWN EGG
    public static final RegistryObject<Item> DODO_SPAWN_EGG = registerSpawnEgg("dodo_spawn_egg",
            EntityInit.DODO, 3679516, 7164742);

    private static RegistryObject<Item> registerSimple(final String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static RegistryObject<Item> registerFood(final String name, FoodProperties foodProperties) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(foodProperties)));
    }

    public static RegistryObject<Item> registerSpawnEgg(final String name, Supplier<? extends EntityType<? extends Mob>>
            type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name, () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }
}
