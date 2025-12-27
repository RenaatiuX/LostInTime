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

        simple(ItemInit.RAW_DODO.get());
        simple(ItemInit.COOKED_DODO.get());
        simple(ItemInit.MANGO.get());
        simple(BlockInit.DODO_EGG.get());

        simple(ItemInit.GOLDEN_EYE.get());

        spawnEgg(ItemInit.DODO_SPAWN_EGG.get());

        saplingItem(BlockInit.MANGO_SAPLING);

        simple(ItemInit.GUARDIAN_SPIKE.get());
        simple(ItemInit.DEVONIAN_FOSSIL.get());
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
