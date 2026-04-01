package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.enums.BoatType;
import com.ren.lostintime.common.item.*;
import com.ren.lostintime.common.util.ModToolTiers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
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
    public static final RegistryObject<Item> SOUL_ASH = registerSimple("soul_ash");
    public static final RegistryObject<Item> SOUL_GRUME = registerSimple("soul_grume");
    public static final RegistryObject<Item> EMPTY_VITAL_PATTERN = registerSimple("empty_vital_pattern");
    public static final RegistryObject<Item> INFORMATION_DOME = registerSimple("information_dome");
    public static final RegistryObject<Item> SOUL_POWDER = registerSimple("soul_powder");
    public static final RegistryObject<Item> PANEL = registerSimple("panel");
    public static final RegistryObject<Item> REDSTONE_CHIP = registerSimple("redstone_chip");
    public static final RegistryObject<Item> HYLONOMUS_EGG = registerEgg("hylonomus_egg", EntityInit.HYLONOMUS);
    public static final RegistryObject<Item> ENDOCERAS_SHELL_FRAGMENT = registerSimple("endoceras_shell_fragment");
    public static final RegistryObject<Item> SCUTOSAURUS_PLATE = registerSimple("scutosaurus_plate");
    public static final RegistryObject<Item> REGURGITATED_MASS = registerSimple("regurgitated_mass");
    public static final RegistryObject<Item> ZIRCON = registerSimple("zircon");
    public static final RegistryObject<Item> OPAL = registerSimple("opal");
    public static final RegistryObject<Item> SPINEL = registerSimple("spinel");
    public static final RegistryObject<Item> PREHISTORIC_BOOK = ITEMS.register("prehistoric_book",
            () -> new PrehistoricBookItem(new Item.Properties()));
    public static final RegistryObject<Item> SCANNER = ITEMS.register("scanner",
            () -> new PaleoScannerItem(new Item.Properties().stacksTo(1)));

    //SAC
    public static final RegistryObject<Item> DAEODON_SAC = registerSimple("daeodon_sac");
    public static final RegistryObject<Item> LEPTICTIDIUM_SAC = registerSimple("leptictidium_sac");
    public static final RegistryObject<Item> PLESIOSAURUS_SAC = registerSimple("plesiosaurus_sac");
    public static final RegistryObject<Item> HELICOPRION_SAC = registerSimple("helicoprion_sac");

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
    public static final RegistryObject<Item> ASPECT_COMPLEXITY = registerSimple("aspect_complexity");
    public static final RegistryObject<Item> ASPECT_DOMINATION = registerSimple("aspect_domination");
    public static final RegistryObject<Item> ASPECT_EXPERIMENTATION = registerSimple("aspect_experimentation");
    public static final RegistryObject<Item> ASPECT_SUCCESSION = registerSimple("aspect_succession");

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
    public static final RegistryObject<Item> AMBER_CATALYST = registerSimple("amber_catalyst");
    public static final RegistryObject<Item> OPAL_CATALYST = registerSimple("opal_catalyst");
    public static final RegistryObject<Item> SPINEL_CATALYST = registerSimple("spinel_catalyst");
    public static final RegistryObject<Item> OBSIDIAN_CATALYST = registerSimple("obsidian_catalyst");

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
    public static final RegistryObject<Item> NEOGENE_FOSSIL = registerSimple("neogene_fossil");
    public static final RegistryObject<Item> QUATERNARY_FOSSIL = registerSimple("quaternary_fossil");

    //CONFIGURATION
    public static final RegistryObject<Item> ANOMALOCARIS_SOUL_CFG = registerSimple("anomalocaris_soul_configuration");
    public static final RegistryObject<Item> BOTHRIOLEPIS_SOUL_CFG = registerSimple("bothriolepis_soul_configuration");
    public static final RegistryObject<Item> DAEODON_SOUL_CFG = registerSimple("daeodon_soul_configuration");
    public static final RegistryObject<Item> DEINONYCHUS_SOUL_CFG = registerSimple("deinonychus_soul_configuration");
    public static final RegistryObject<Item> DODO_SOUL_CFG = registerSimple("dodo_soul_configuration");
    public static final RegistryObject<Item> EMPTY_SOUL_CFG = registerSimple("empty_soul_configuration");
    public static final RegistryObject<Item> ENDOCERAS_SOUL_CFG = registerSimple("endoceras_soul_configuration");
    public static final RegistryObject<Item> HYLONOMUS_SOUL_CFG = registerSimple("hylonomus_soul_configuration");
    public static final RegistryObject<Item> LEPTICTIDIUM_SOUL_CFG = registerSimple("leptictidium_soul_configuration");
    public static final RegistryObject<Item> MASTODONSAURUS_SOUL_CFG = registerSimple("mastodonsaurus_soul_configuration");
    public static final RegistryObject<Item> PLESIOSAURUS_SOUL_CFG = registerSimple("plesiosaurus_soul_configuration");
    public static final RegistryObject<Item> PTERYGOTUS_SOUL_CFG = registerSimple("pterygotus_soul_configuration");
    public static final RegistryObject<Item> SCUTOSAURUS_SOUL_CFG = registerSimple("scutosaurus_soul_configuration");
    public static final RegistryObject<Item> HELICOPRION_SOUL_CFG = registerSimple("helicoprion_soul_configuration");

    //PATTERN
    public static final RegistryObject<Item> PROTOTAXITES_VITAL_PATTERN = registerSimple("prototaxites_vital_pattern");
    public static final RegistryObject<Item> BARREL_SPONGE_VITAL_PATTERN = registerSimple("barrel_sponge_vital_pattern");
    public static final RegistryObject<Item> GLASS_SPONGE_VITAL_PATTERN = registerSimple("glass_sponge_vital_pattern");
    public static final RegistryObject<Item> PIPE_SPONGE_VITAL_PATTERN = registerSimple("pipe_sponge_vital_pattern");
    public static final RegistryObject<Item> TREE_SPONGE_VITAL_PATTERN = registerSimple("tree_sponge_vital_pattern");
    public static final RegistryObject<Item> VASE_SPONGE_VITAL_PATTERN = registerSimple("vase_sponge_vital_pattern");
    public static final RegistryObject<Item> WOOL_SPONGE_VITAL_PATTERN = registerSimple("wool_sponge_vital_pattern");
    public static final RegistryObject<Item> ARAUCARIOXYLON_VITAL_PATTERN = registerSimple("araucarioxylon_vital_pattern");
    public static final RegistryObject<Item> CLADOPHLEBIS_VITAL_PATTERN = registerSimple("cladophlebis_vital_pattern");
    public static final RegistryObject<Item> CONIOPTERIS_VITAL_PATTERN = registerSimple("coniopteris_vital_pattern");
    public static final RegistryObject<Item> COOKSONIA_VITAL_PATTERN = registerSimple("cooksonia_vital_pattern");
    public static final RegistryObject<Item> GONDWANAGARICITES_VITAL_PATTERN = registerSimple("gondwanagaricites_vital_pattern");
    public static final RegistryObject<Item> RED_ALGAE_VITAL_PATTERN = registerSimple("red_algae_vital_pattern");
    public static final RegistryObject<Item> TITANOSARCOLITES_VITAL_PATTERN = registerSimple("titanosarcolites_vital_pattern");

    //SOLUTION
    public static final RegistryObject<Item> BEIGE_SOLUTION = registerSimple("beige_solution");
    public static final RegistryObject<Item> CARMINE_SOLUTION = registerSimple("carmine_solution");
    public static final RegistryObject<Item> CHARTREUSE_SOLUTION = registerSimple("chartreuse_solution");
    public static final RegistryObject<Item> CINEREOUS_SOLUTION = registerSimple("cinereous_solution");
    public static final RegistryObject<Item> DELFT_SOLUTION = registerSimple("delft_solution");
    public static final RegistryObject<Item> EVERGREEN_SOLUTION = registerSimple("evergreen_solution");
    public static final RegistryObject<Item> PERIWINKLE_SOLUTION = registerSimple("periwinkle_solution");
    public static final RegistryObject<Item> ROSE_SOLUTION = registerSimple("rose_solution");
    public static final RegistryObject<Item> SHAMROCK_SOLUTION = registerSimple("shamrock_solution");
    public static final RegistryObject<Item> TEAL_SOLUTION = registerSimple("teal_solution");

    //NUTRIENTS
    public static final RegistryObject<Item> FIBROUS_NUTRIENT = registerSimple("fibrous_nutrient");
    public static final RegistryObject<Item> GELATINOUS_NUTRIENT = registerSimple("gelatinous_nutrient");
    public static final RegistryObject<Item> RICH_NUTRIENT = registerSimple("rich_nutrient");
    public static final RegistryObject<Item> UNIVERSAL_NUTRIENT = registerSimple("universal_nutrient");

    //ENTITY FOSSIL
    public static final RegistryObject<Item> DODO_FOSSIL = registerSimple("dodo_fossil");
    public static final RegistryObject<Item> DODO_SKULL = registerSimple("dodo_skull");
    public static final RegistryObject<Item> BOTHRIOLEPIS_FOSSIL = registerSimple("bothriolepis_fossil");
    public static final RegistryObject<Item> ANOMALOCARIS_FOSSIL = registerSimple("anomalocaris_fossil");
    public static final RegistryObject<Item> DAEODON_FOSSIL = registerSimple("daeodon_fossil");
    public static final RegistryObject<Item> DAEODON_SKULL = registerSimple("daeodon_skull");
    public static final RegistryObject<Item> DEINONYCHUS_FOSSIL = registerSimple("deinonychus_fossil");
    public static final RegistryObject<Item> DEINONYCHUS_SKULL = registerSimple("deinonychus_skull");
    public static final RegistryObject<Item> ENDOCERAS_FOSSIL = registerSimple("endoceras_fossil");
    public static final RegistryObject<Item> HYLONOMUS_FOSSIL = registerSimple("hylonomus_fossil");
    public static final RegistryObject<Item> LEPTICTIDIUM_FOSSIL = registerSimple("leptictidium_fossil");
    public static final RegistryObject<Item> MASTODONSAURUS_FOSSIL = registerSimple("mastodonsaurus_fossil");
    public static final RegistryObject<Item> MASTODONSAURUS_SKULL = registerSimple("mastodonsaurus_skull");
    public static final RegistryObject<Item> PLESIOSAURUS_FOSSIL = registerSimple("plesiosaurus_fossil");
    public static final RegistryObject<Item> PLESIOSAURUS_SKULL = registerSimple("plesiosaurus_skull");
    public static final RegistryObject<Item> PTERYGOTUS_FOSSIL = registerSimple("pterygotus_fossil");
    public static final RegistryObject<Item> SCUTOSAURUS_FOSSIL = registerSimple("scutosaurus_fossil");
    public static final RegistryObject<Item> SCUTOSAURUS_SKULL = registerSimple("scutosaurus_skull");
    public static final RegistryObject<Item> HELICOPRION_FOSSIL = registerSimple("helicoprion_fossil");
    public static final RegistryObject<Item> HELICOPRION_SKULL = registerSimple("helicoprion_skull");

    //PLANT FOSSIL
    public static final RegistryObject<Item> CLADOPHLEBIS_FOSSIL = registerSimple("cladophlebis_fossil");
    public static final RegistryObject<Item> CONIOPTERIS_FOSSIL = registerSimple("coniopteris_fossil");
    public static final RegistryObject<Item> RED_ALGAE_FOSSIL = registerSimple("red_algae_fossil");

    //FOSSIL MOUNT
    public static final RegistryObject<Item> DODO_FOSSIL_MOUNT = registerFossilItem("dodo_fossil_mount", "dodo");
    public static final RegistryObject<Item> EMPTY_SKELETON_MOUNT = registerSimple("empty_skeleton_mount");

    //EQUIP
    public static final RegistryObject<Item> GOLDEN_EYE = ITEMS.register("golden_eye",
            () -> new GoldenEyeItem(new Item.Properties()));

    //WEAPONS
    public static final RegistryObject<Item> STONE_KNIFE = ITEMS.register("stone_knife",
            () -> new KnifeItem(Tiers.STONE, 1, -1.2F,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> IRON_KNIFE = ITEMS.register("iron_knife",
            () -> new KnifeItem(Tiers.IRON, 1, -1.2F,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_KNIFE = ITEMS.register("golden_knife",
            () -> new KnifeItem(Tiers.GOLD, 1, -1.2F,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIAMOND_KNIFE = ITEMS.register("diamond_knife",
            () -> new KnifeItem(Tiers.DIAMOND, 1, -1.2F,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NETHERITE_KNIFE = ITEMS.register("netherite_knife",
            () -> new KnifeItem(Tiers.NETHERITE, 1, -1.2F,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ZIRCON_KNIFE = ITEMS.register("zircon_knife",
            () -> new KnifeItem(ModToolTiers.ZIRCON, 1, -1.2F,
                    new Item.Properties().stacksTo(1).fireResistant()));


    //FOOD
    public static final RegistryObject<Item> RAW_DODO = registerFood("raw_dodo", FoodInit.RAW_DODO);
    public static final RegistryObject<Item> COOKED_DODO = registerFood("cooked_dodo", FoodInit.COOKED_DODO);
    public static final RegistryObject<Item> RAW_ANOMALOCARIS = registerFood("raw_anomalocaris", FoodInit.RAW_ANOMALOCARIS);
    public static final RegistryObject<Item> COOKED_ANOMALOCARIS = registerFood("cooked_anomalocaris", FoodInit.COOKED_ANOMALOCARIS);
    public static final RegistryObject<Item> RAW_BOTHRIOLEPIS = registerFood("raw_bothriolepis", FoodInit.RAW_BOTHRIOLEPIS);
    public static final RegistryObject<Item> COOKED_BOTHRIOLEPIS = registerFood("cooked_bothriolepis", FoodInit.COOKED_BOTHRIOLEPIS);
    public static final RegistryObject<Item> RAW_DAEODON = registerFood("raw_daeodon", FoodInit.RAW_DAEODON);
    public static final RegistryObject<Item> COOKED_DAEODON = registerFood("cooked_daeodon", FoodInit.COOKED_DAEODON);
    public static final RegistryObject<Item> RAW_ENDOCERAS = registerFood("raw_endoceras", FoodInit.RAW_ENDOCERAS);
    public static final RegistryObject<Item> COOKED_ENDOCERAS = registerFood("cooked_endoceras", FoodInit.COOKED_ENDOCERAS);
    public static final RegistryObject<Item> RAW_HYLONOMUS = registerFood("raw_hylonomus", FoodInit.RAW_HYLONOMUS);
    public static final RegistryObject<Item> COOKED_HYLONOMUS = registerFood("cooked_hylonomus", FoodInit.COOKED_HYLONOMUS);
    public static final RegistryObject<Item> RAW_LEPTICTIDIUM = registerFood("raw_leptictidium", FoodInit.RAW_LEPTICTIDIUM);
    public static final RegistryObject<Item> COOKED_LEPTICTIDIUM = registerFood("cooked_leptictidium", FoodInit.COOKED_LEPTICTIDIUM);
    public static final RegistryObject<Item> RAW_MASTODONSAURUS_MEAT = registerFood("raw_mastodonsaurus_meat", FoodInit.RAW_MASTODONSAURUS_MEAT);
    public static final RegistryObject<Item> COOKED_MASTODONSAURUS_MEAT = registerFood("cooked_mastodonsaurus_meat", FoodInit.COOKED_MASTODONSAURUS_MEAT);
    public static final RegistryObject<Item> RAW_PLESIOSAURUS_MEAT = registerFood("raw_plesiosaurus_meat", FoodInit.RAW_PLESIOSAURUS_MEAT);
    public static final RegistryObject<Item> COOKED_PLESIOSAURUS_MEAT = registerFood("cooked_plesiosaurus_meat", FoodInit.COOKED_PLESIOSAURUS_MEAT);
    public static final RegistryObject<Item> RAW_SCUTOSAURUS_MEAT = registerFood("raw_scutosaurus_meat", FoodInit.RAW_SCUTOSAURUS_MEAT);
    public static final RegistryObject<Item> COOKED_SCUTOSAURUS_MEAT = registerFood("cooked_scutosaurus_meat", FoodInit.COOKED_SCUTOSAURUS_MEAT);
    public static final RegistryObject<Item> RAW_DEINONYCHUS_MEAT = registerFood("raw_deinonychus_meat", FoodInit.RAW_DEINONYCHUS_MEAT);
    public static final RegistryObject<Item> COOKED_DEINONYCHUS_MEAT = registerFood("cooked_deinonychus_meat", FoodInit.COOKED_DEINONYCHUS_MEAT);
    public static final RegistryObject<Item> RAW_HELICOPRION_MEAT = registerFood("raw_helicoprion_meat", FoodInit.RAW_HELICOPRION_MEAT);
    public static final RegistryObject<Item> COOKED_HELICOPRION_MEAT = registerFood("cooked_helicoprion_meat", FoodInit.COOKED_HELICOPRION_MEAT);

    public static final RegistryObject<Item> MANGO = registerFood("mango", FoodInit.MANGO);

    public static final RegistryObject<Item> SMALL_FRIED_EGG = registerFood("small_fried_egg", FoodInit.SMALL_FRIED_EGG);
    public static final RegistryObject<Item> FRIED_EGG = registerFood("fried_egg", FoodInit.FRIED_EGG);
    public static final RegistryObject<Item> LARGE_FRIED_EGG = registerFood("large_fried_egg", FoodInit.LARGE_FRIED_EGG);

    //BLOCKS
    public static final RegistryObject<Item> BOTHRIOLEPIS_ROE = ITEMS.register("bothriolepis_roe",
            () -> new PlaceOnWaterBlockItem(BlockInit.BOTHRIOLEPIS_ROE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANOMALOCARIS_ROE = ITEMS.register("anomalocaris_roe",
            () -> new PlaceOnWaterBlockItem(BlockInit.ANOMALOCARIS_ROE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ENDOCERAS_EGGS = ITEMS.register("endoceras_eggs",
            () -> new PlaceOnWaterBlockItem(BlockInit.ENDOCERAS_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> MASTODONSAURUS_EGG = ITEMS.register("mastodonsaurus_egg",
            () -> new PlaceOnWaterBlockItem(BlockInit.MASTODONSAURUS_EGG.get(), new Item.Properties()));

    //BUCKETS
    public static final RegistryObject<Item> BOTHRIOLEPIS_ROE_BUCKET = ITEMS.register("bothriolepis_roe_bucket",
            () -> new RoeBucketItem(BlockInit.BOTHRIOLEPIS_ROE, () -> SoundEvents.BUCKET_EMPTY, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENDOCERAS_EGGS_BUCKET = ITEMS.register("endoceras_eggs_bucket",
            () -> new RoeBucketItem(BlockInit.ENDOCERAS_EGG, () -> SoundEvents.BUCKET_EMPTY, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> PLESIOSAURUS_BABY_BUCKET = ITEMS.register("plesiosaurus_baby_bucket",
            () -> new MobBucketItem(EntityInit.PLESIOSAURUS, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENDOCERAS_BABY_BUCKET = ITEMS.register("endoceras_baby_bucket",
            () -> new MobBucketItem(EntityInit.ENDOCERAS, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BOTHRIOLEPIS_BABY_BUCKET = ITEMS.register("bothriolepis_baby_bucket",
            () -> new MobBucketItem(EntityInit.BOTHRIOLEPIS, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ANOMALOCARIS_BABY_BUCKET = ITEMS.register("anomalocaris_baby_bucket",
            () -> new MobBucketItem(EntityInit.ANOMALOCARIS, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY,
                    new Item.Properties().stacksTo(1)));

    //SPAWN EGG
    public static final RegistryObject<Item> DODO_SPAWN_EGG = registerSpawnEgg("dodo_spawn_egg",
            EntityInit.DODO, 3679516, 7164742);
    public static final RegistryObject<Item> ENDOCERAS_SPAWN_EGG = registerSpawnEgg("endoceras_spawn_egg",
            EntityInit.ENDOCERAS, 6709041, 8353403);
    public static final RegistryObject<Item> ANOMALOCARIS_SPAWN_EGG = registerSpawnEgg("anomalocaris_spawn_egg",
            EntityInit.ANOMALOCARIS, 8461857, 12872607);
    public static final RegistryObject<Item> BOTHRIOLEPIS_SPAWN_EGG = registerSpawnEgg("bothriolepis_spawn_egg",
            EntityInit.BOTHRIOLEPIS, 6109751, 10715772);
    public static final RegistryObject<Item> DAEODON_SPAWN_EGG = registerSpawnEgg("daeodon_spawn_egg",
            EntityInit.DAEODON, 5981765, 9339517);
    public static final RegistryObject<Item> HYLONOMUS_SPAWN_EGG = registerSpawnEgg("hylonomus_spawn_egg",
            EntityInit.HYLONOMUS, 5325867, 10457717);
    public static final RegistryObject<Item> LEPTICTIDIUM_SPAWN_EGG = registerSpawnEgg("leptictidium_spawn_egg",
            EntityInit.LEPTICTIDIUM, 7955270, 11707795);
    public static final RegistryObject<Item> SCUTOSAURUS_SPAWN_EGG = registerSpawnEgg("scutosaurus_spawn_egg",
            EntityInit.SCUTOSAURUS, 5852492, 7691613);
    public static final RegistryObject<Item> PLESIOSAURUS_SPAWN_EGG = registerSpawnEgg("plesiosaurus_spawn_egg",
            EntityInit.PLESIOSAURUS, 2372675, 14280690);
    public static final RegistryObject<Item> MASTODONSAURUS_SPAWN_EGG = registerSpawnEgg("mastodonsaurus_spawn_egg",
            EntityInit.MASTODONSAURUS, 5262637, 9999450);
    public static final RegistryObject<Item> HELICOPRION_SPAWN_EGG = registerSpawnEgg("helicoprion_spawn_egg",
            EntityInit.HELICOPRION, 4871251, 15592941);
    public static final RegistryObject<Item> DEINONYCHUS_SPAWN_EGG = registerSpawnEgg("deinonychus_spawn_egg",
            EntityInit.DEINONYCHUS, 6967369, 9993579);
    public static final RegistryObject<Item> PTERYGOTUS_SPAWN_EGG = registerSpawnEgg("pterygotus_spawn_egg",
            EntityInit.PTERYGOTUS, 4080219, 2631200);

    //BOATS
    public static final RegistryObject<Item> ARAUCARIOXYLON_BOAT = ITEMS.register("araucarioxylon_boat",
            () -> new LITBoatItem(BoatType.ARAUCARIOXYLON, false, new Item.Properties()));
    public static final RegistryObject<Item> ARAUCARIOXYLON_CHEST_BOAT = ITEMS.register("araucarioxylon_chest_boat",
            () -> new LITBoatItem(BoatType.ARAUCARIOXYLON, true, new Item.Properties()));

    //SIGNS
    public static final RegistryObject<Item> ARAUCARIOXYLON_SIGN = ITEMS.register("araucarioxylon_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), BlockInit.ARAUCARIOXYLON_SIGN.get(), BlockInit.ARAUCARIOXYLON_WALL_SIGN.get()));
    public static final RegistryObject<Item> ARAUCARIOXYLON_HANGING_SIGN = ITEMS.register("araucarioxylon_hanging_sign",
            () -> new HangingSignItem(BlockInit.ARAUCARIOXYLON_HANGING_SIGN.get(), BlockInit.ARAUCARIOXYLON_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

    private static RegistryObject<Item> registerSimple(final String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> registerFossilItem(final String name, String skeletonType) {
        return ITEMS.register(name, () -> new SkeletonPlacerItem(new Item.Properties(), skeletonType));
    }

    private static RegistryObject<Item> registerEgg(final String name, Supplier<? extends EntityType<? extends Mob>> entityType) {
        return ITEMS.register(name, () -> new LITEggItem(new Item.Properties(), entityType));
    }

    public static RegistryObject<Item> registerFood(final String name, FoodProperties foodProperties) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(foodProperties)));
    }

    public static RegistryObject<Item> registerSpawnEgg(final String name, Supplier<? extends EntityType<? extends Mob>>
            type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name, () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }
}
