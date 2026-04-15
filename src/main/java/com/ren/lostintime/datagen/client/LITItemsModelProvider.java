package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LITItemsModelProvider extends ItemModelProvider {

    public final ModelFile generated = getExistingFile(mcLoc("item/generated"));
    public final ModelFile handheld = getExistingFile(mcLoc("item/handheld"));
    public final ModelFile spawnEgg = getExistingFile(mcLoc("item/template_spawn_egg"));

    public LITItemsModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LostInTime.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simple(ItemInit.AMBER.get());
        simple(ItemInit.ECTOPLASM.get());
        simple(ItemInit.SOUL_ASH.get());
        simple(ItemInit.SOUL_GRUME.get());
        simple(ItemInit.EMPTY_VITAL_PATTERN.get());
        simple(ItemInit.INFORMATION_DOME.get());
        simple(ItemInit.SOUL_POWDER.get());
        simple(ItemInit.PANEL.get());
        simple(ItemInit.REDSTONE_CHIP.get());
        simple(ItemInit.HYLONOMUS_EGG.get());
        simple(ItemInit.ENDOCERAS_SHELL_FRAGMENT.get());
        simple(ItemInit.SCUTOSAURUS_PLATE.get());
        simple(ItemInit.REGURGITATED_MASS.get());
        simple(ItemInit.GOLDEN_KEY.get());

        simple(ItemInit.DAEODON_SAC.get());
        simple(ItemInit.LEPTICTIDIUM_SAC.get());
        simple(ItemInit.PLESIOSAURUS_SAC.get());
        simple(ItemInit.HELICOPRION_SAC.get());

        simple(ItemInit.RAW_DODO.get());
        simple(ItemInit.COOKED_DODO.get());
        simple(ItemInit.RAW_ANOMALOCARIS.get());
        simple(ItemInit.COOKED_ANOMALOCARIS.get());
        simple(ItemInit.RAW_BOTHRIOLEPIS.get());
        simple(ItemInit.COOKED_BOTHRIOLEPIS.get());
        simple(ItemInit.RAW_DAEODON.get());
        simple(ItemInit.COOKED_DAEODON.get());
        simple(ItemInit.RAW_ENDOCERAS.get());
        simple(ItemInit.COOKED_ENDOCERAS.get());
        simple(ItemInit.RAW_HYLONOMUS.get());
        simple(ItemInit.COOKED_HYLONOMUS.get());
        simple(ItemInit.RAW_LEPTICTIDIUM.get());
        simple(ItemInit.COOKED_LEPTICTIDIUM.get());
        simple(ItemInit.RAW_MASTODONSAURUS_MEAT.get());
        simple(ItemInit.COOKED_MASTODONSAURUS_MEAT.get());
        simple(ItemInit.RAW_PLESIOSAURUS_MEAT.get());
        simple(ItemInit.COOKED_PLESIOSAURUS_MEAT.get());
        simple(ItemInit.RAW_SCUTOSAURUS_MEAT.get());
        simple(ItemInit.COOKED_SCUTOSAURUS_MEAT.get());
        simple(ItemInit.RAW_DEINONYCHUS_MEAT.get());
        simple(ItemInit.COOKED_DEINONYCHUS_MEAT.get());
        simple(ItemInit.RAW_HELICOPRION_MEAT.get());
        simple(ItemInit.COOKED_HELICOPRION_MEAT.get());
        simple(ItemInit.RAW_PTERYGOTUS_MEAT.get());
        simple(ItemInit.COOKED_PTERYGOTUS_MEAT.get());

        simple(ItemInit.SMALL_FRIED_EGG.get());
        simple(ItemInit.FRIED_EGG.get());
        simple(ItemInit.LARGE_FRIED_EGG.get());

        simple(ItemInit.MANGO.get());
        simple(BlockInit.DODO_EGG.get());
        simple(BlockInit.SCUTOSAURUS_EGG.get());
        simple(BlockInit.DEINONYCHUS_EGG.get());

        simple(ItemInit.GOLDEN_EYE.get());
        handheld(ItemInit.STONE_KNIFE.get());
        handheld(ItemInit.IRON_KNIFE.get());
        handheld(ItemInit.GOLDEN_KNIFE.get());
        handheld(ItemInit.DIAMOND_KNIFE.get());
        handheld(ItemInit.NETHERITE_KNIFE.get());
        handheld(ItemInit.ZIRCON_KNIFE.get());

        spawnEgg(ItemInit.DODO_SPAWN_EGG.get());
        spawnEgg(ItemInit.ENDOCERAS_SPAWN_EGG.get());
        spawnEgg(ItemInit.ANOMALOCARIS_SPAWN_EGG.get());
        spawnEgg(ItemInit.BOTHRIOLEPIS_SPAWN_EGG.get());
        spawnEgg(ItemInit.DAEODON_SPAWN_EGG.get());
        spawnEgg(ItemInit.HYLONOMUS_SPAWN_EGG.get());
        spawnEgg(ItemInit.LEPTICTIDIUM_SPAWN_EGG.get());
        spawnEgg(ItemInit.SCUTOSAURUS_SPAWN_EGG.get());
        spawnEgg(ItemInit.PLESIOSAURUS_SPAWN_EGG.get());
        spawnEgg(ItemInit.MASTODONSAURUS_SPAWN_EGG.get());
        spawnEgg(ItemInit.HELICOPRION_SPAWN_EGG.get());
        spawnEgg(ItemInit.DEINONYCHUS_SPAWN_EGG.get());
        spawnEgg(ItemInit.PTERYGOTUS_SPAWN_EGG.get());
        spawnEgg(ItemInit.KALLIGRAMMATIDAE_SPAWN_EGG.get());

        saplingItem(BlockInit.MANGO_SAPLING);
        saplingItem(BlockInit.ARAUCARIOXYLON_SAPLING);
        simple(ItemInit.BOTHRIOLEPIS_ROE.get());
        simple(ItemInit.ENDOCERAS_EGGS.get());
        simple(ItemInit.MASTODONSAURUS_EGG.get());
        simple(ItemInit.ANOMALOCARIS_ROE.get());

        simple(ItemInit.BOTHRIOLEPIS_ROE_BUCKET.get());
        simple(ItemInit.ENDOCERAS_EGGS_BUCKET.get());

        simple(ItemInit.PLESIOSAURUS_BABY_BUCKET.get());
        simple(ItemInit.ENDOCERAS_BABY_BUCKET.get());
        simple(ItemInit.BOTHRIOLEPIS_BABY_BUCKET.get());
        simple(ItemInit.ANOMALOCARIS_BABY_BUCKET.get());

        simple(ItemInit.GUARDIAN_SPIKE.get());

        simple(ItemInit.ARAUCARIOXYLON_BOAT.get());
        simple(ItemInit.ARAUCARIOXYLON_CHEST_BOAT.get());

        simple(ItemInit.CAMBRIAN_FOSSIL.get());
        simple(ItemInit.ORDOVICIAN_FOSSIL.get());
        simple(ItemInit.SILURIAN_FOSSIL.get());
        simple(ItemInit.DEVONIAN_FOSSIL.get());
        simple(ItemInit.CARBONIFEROUS_FOSSIL.get());
        simple(ItemInit.PERMIAN_FOSSIL.get());
        simple(ItemInit.TRIASSIC_FOSSIL.get());
        simple(ItemInit.JURASSIC_FOSSIL.get());
        simple(ItemInit.CRETACEOUS_FOSSIL.get());
        simple(ItemInit.PALEOGENE_FOSSIL.get());
        simple(ItemInit.NEOGENE_FOSSIL.get());
        simple(ItemInit.QUATERNARY_FOSSIL.get());

        simple(ItemInit.BOTHRIOLEPIS_FOSSIL.get());
        simple(ItemInit.DODO_FOSSIL.get());
        simple(ItemInit.DODO_SKULL.get());
        simple(ItemInit.ANOMALOCARIS_FOSSIL.get());
        simple(ItemInit.DAEODON_FOSSIL.get());
        simple(ItemInit.DAEODON_SKULL.get());
        simple(ItemInit.DEINONYCHUS_FOSSIL.get());
        simple(ItemInit.DEINONYCHUS_SKULL.get());
        simple(ItemInit.ENDOCERAS_FOSSIL.get());
        simple(ItemInit.HYLONOMUS_FOSSIL.get());
        simple(ItemInit.LEPTICTIDIUM_FOSSIL.get());
        simple(ItemInit.MASTODONSAURUS_FOSSIL.get());
        simple(ItemInit.MASTODONSAURUS_SKULL.get());
        simple(ItemInit.PLESIOSAURUS_FOSSIL.get());
        simple(ItemInit.PLESIOSAURUS_SKULL.get());
        simple(ItemInit.PTERYGOTUS_FOSSIL.get());
        simple(ItemInit.SCUTOSAURUS_FOSSIL.get());
        simple(ItemInit.SCUTOSAURUS_SKULL.get());
        simple(ItemInit.CLADOPHLEBIS_FOSSIL.get());
        simple(ItemInit.CONIOPTERIS_FOSSIL.get());
        simple(ItemInit.RED_ALGAE_FOSSIL.get());
        simple(ItemInit.ALLONNIA_FOSSL.get());
        simple(ItemInit.COOKSONIA_FOSSIL.get());
        simple(ItemInit.ARAUCARIOXYLON_FOSSIL.get());
        simple(ItemInit.HELICOPRION_FOSSIL.get());
        simple(ItemInit.HELICOPRION_SKULL.get());
        simple(ItemInit.HORSESHOE_CRAB_FOSSIL.get());

        simple(ItemInit.DODO_FOSSIL_MOUNT.get());
        simple(ItemInit.EMPTY_SKELETON_MOUNT.get());

        simple(ItemInit.ASPECT_DIFFERENTIATION.get());
        simple(ItemInit.ASPECT_EMERGENCE.get());
        simple(ItemInit.ASPECT_INTEGRATION.get());
        simple(ItemInit.ASPECT_STRUCTURING.get());
        simple(ItemInit.ASPECT_TRANSIENCE.get());
        simple(ItemInit.ASPECT_ABUNDANCE.get());
        simple(ItemInit.ASPECT_CONTINUITY.get());
        simple(ItemInit.ASPECT_MAGNITUDE.get());
        simple(ItemInit.ASPECT_PROLIFERATION.get());
        simple(ItemInit.ASPECT_RECOVERY.get());
        simple(ItemInit.ASPECT_REFINEMENT.get());
        simple(ItemInit.ASPECT_RESILIENCE.get());
        simple(ItemInit.ASPECT_COMPLEXITY.get());
        simple(ItemInit.ASPECT_DOMINATION.get());
        simple(ItemInit.ASPECT_EXPERIMENTATION.get());
        simple(ItemInit.ASPECT_SUCCESSION.get());

        simple(ItemInit.ZIRCON.get());
        simple(ItemInit.OPAL.get());
        simple(ItemInit.SPINEL.get());

        simple(ItemInit.AMETHYST_CATALYST.get());
        simple(ItemInit.BLUE_ICE_CATALYST.get());
        simple(ItemInit.CALCITE_CATALYST.get());
        simple(ItemInit.COAL_CATALYST.get());
        simple(ItemInit.COPPER_CATALYST.get());
        simple(ItemInit.EMERALD_CATALYST.get());
        simple(ItemInit.GOLD_CATALYST.get());
        simple(ItemInit.IRON_CATALYST.get());
        simple(ItemInit.LAPIS_LAZULI_CATALYST.get());
        simple(ItemInit.QUARTZ_CATALYST.get());
        simple(ItemInit.REDSTONE_CATALYST.get());
        simple(ItemInit.ZIRCON_CATALYST.get());
        simple(ItemInit.AMBER_CATALYST.get());
        simple(ItemInit.OPAL_CATALYST.get());
        simple(ItemInit.SPINEL_CATALYST.get());
        simple(ItemInit.OBSIDIAN_CATALYST.get());

        simple(ItemInit.ANOMALOCARIS_SOUL_CFG.get());
        simple(ItemInit.BOTHRIOLEPIS_SOUL_CFG.get());
        simple(ItemInit.DAEODON_SOUL_CFG.get());
        simple(ItemInit.DEINONYCHUS_SOUL_CFG.get());
        simple(ItemInit.DODO_SOUL_CFG.get());
        simple(ItemInit.EMPTY_SOUL_CFG.get());
        simple(ItemInit.ENDOCERAS_SOUL_CFG.get());
        simple(ItemInit.HYLONOMUS_SOUL_CFG.get());
        simple(ItemInit.LEPTICTIDIUM_SOUL_CFG.get());
        simple(ItemInit.MASTODONSAURUS_SOUL_CFG.get());
        simple(ItemInit.PLESIOSAURUS_SOUL_CFG.get());
        simple(ItemInit.PROTOTAXITES_VITAL_PATTERN.get());
        simple(ItemInit.PTERYGOTUS_SOUL_CFG.get());
        simple(ItemInit.SCUTOSAURUS_SOUL_CFG.get());
        simple(ItemInit.HELICOPRION_SOUL_CFG.get());
        simple(ItemInit.HORSESHOE_CRAB_SOUL_CFG.get());

        simple(ItemInit.BARREL_SPONGE_VITAL_PATTERN.get());
        simple(ItemInit.GLASS_SPONGE_VITAL_PATTERN.get());
        simple(ItemInit.PIPE_SPONGE_VITAL_PATTERN.get());
        simple(ItemInit.TREE_SPONGE_VITAL_PATTERN.get());
        simple(ItemInit.VASE_SPONGE_VITAL_PATTERN.get());
        simple(ItemInit.WOOL_SPONGE_VITAL_PATTERN.get());
        simple(ItemInit.ARAUCARIOXYLON_VITAL_PATTERN.get());
        simple(ItemInit.CLADOPHLEBIS_VITAL_PATTERN.get());
        simple(ItemInit.CONIOPTERIS_VITAL_PATTERN.get());
        simple(ItemInit.COOKSONIA_VITAL_PATTERN.get());
        simple(ItemInit.GONDWANAGARICITES_VITAL_PATTERN.get());
        simple(ItemInit.RED_ALGAE_VITAL_PATTERN.get());
        simple(ItemInit.TITANOSARCOLITES_VITAL_PATTERN.get());
        simple(ItemInit.ALLONNIA_VITAL_PATTERN.get());

        simple(ItemInit.BEIGE_SOLUTION.get());
        simple(ItemInit.CARMINE_SOLUTION.get());
        simple(ItemInit.CHARTREUSE_SOLUTION.get());
        simple(ItemInit.CINEREOUS_SOLUTION.get());
        simple(ItemInit.DELFT_SOLUTION.get());
        simple(ItemInit.EVERGREEN_SOLUTION.get());
        simple(ItemInit.PERIWINKLE_SOLUTION.get());
        simple(ItemInit.ROSE_SOLUTION.get());
        simple(ItemInit.SHAMROCK_SOLUTION.get());
        simple(ItemInit.TEAL_SOLUTION.get());

        simple(ItemInit.FIBROUS_NUTRIENT.get());
        simple(ItemInit.GELATINOUS_NUTRIENT.get());
        simple(ItemInit.RICH_NUTRIENT.get());
        simple(ItemInit.UNIVERSAL_NUTRIENT.get());

        simpleBlockItem(BlockInit.ARAUCARIOXYLON_DOOR);

        fenceItem(BlockInit.ARAUCARIOXYLON_FENCE, BlockInit.ARAUCARIOXYLON_PLANKS);
        buttonItem(BlockInit.ARAUCARIOXYLON_BUTTON, BlockInit.ARAUCARIOXYLON_PLANKS);
        wallItem(BlockInit.ARAUCARIOXYLON_WALL, BlockInit.ARAUCARIOXYLON_PLANKS);

        evenSimplerBlockItem(BlockInit.ARAUCARIOXYLON_STAIRS);
        evenSimplerBlockItem(BlockInit.ARAUCARIOXYLON_SLAB);
        evenSimplerBlockItem(BlockInit.ARAUCARIOXYLON_PRESSURE_PLATE);
        evenSimplerBlockItem(BlockInit.ARAUCARIOXYLON_FENCE_GATE);

        trapdoorItem(BlockInit.ARAUCARIOXYLON_TRAPDOOR);

        simple(ItemInit.ARAUCARIOXYLON_SIGN.get());
        simple(ItemInit.ARAUCARIOXYLON_HANGING_SIGN.get());
    }

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(LostInTime.MODID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(LostInTime.MODID,"item/" + item.getId().getPath()));
    }
    
    private void simple(Item... items) {
        for (Item item : items) {
            getBuilder(itemName(item)).parent(generated).texture("layer0", "item/" + itemName(item));
        }
    }

    private void simple(ItemLike... items) {
        for (ItemLike itemProvider : items) {
            simple(itemProvider.asItem());
        }
    }

    protected void handheld(Item... items) {
        for (Item item : items) {
            getBuilder(itemName(item)).parent(handheld).texture("layer0", "item/" + itemName(item));
        }
    }

    private void spawnEgg(Item... items) {
        for (Item item : items) {
            getBuilder(itemName(item)).parent(spawnEgg);
        }
    }

    private ItemModelBuilder saplingItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(LostInTime.MODID,"block/" + item.getId().getPath()));
    }

    private String itemName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }
}
