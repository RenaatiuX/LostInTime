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

        simple(ItemInit.MANGO.get());
        simple(BlockInit.DODO_EGG.get());

        simple(ItemInit.GOLDEN_EYE.get());

        spawnEgg(ItemInit.DODO_SPAWN_EGG.get());

        saplingItem(BlockInit.MANGO_SAPLING);
        simple(ItemInit.BOTHRIOLEPIS_ROE.get());

        simple(ItemInit.GUARDIAN_SPIKE.get());

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

        simple(ItemInit.ZIRCON.get());

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
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(LostInTime.MODID,"block/" + item.getId().getPath()));
    }

    private String itemName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }
}
