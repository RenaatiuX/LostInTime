package com.ren.lostintime.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.ren.lostintime.common.init.ModLootConditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class RandomTagEntryLoot extends LootPoolSingletonContainer {

    TagKey<Item> tag;

    protected RandomTagEntryLoot(TagKey<Item> tag, int pWeight, int pQuality, LootItemCondition[] pConditions, LootItemFunction[] pFunctions) {
        super(pWeight, pQuality, pConditions, pFunctions);
        this.tag = tag;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
       Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(this.tag).getRandomElement(lootContext.getRandom()).ifPresent(item -> consumer.accept(new ItemStack(item)));
    }

    @Override
    public LootPoolEntryType getType() {
        return ModLootConditions.RANDOM_TAG_ENTRY.get();
    }

    public static LootPoolSingletonContainer.Builder<?> randomTagEntry(TagKey<Item> tag){
        return simpleBuilder((p_79583_, p_79584_, p_79585_, p_79586_) -> new RandomTagEntryLoot(tag, p_79583_, p_79584_, p_79585_, p_79586_));
    }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<RandomTagEntryLoot> {
        public Serializer() {
        }

        public void serializeCustom(JsonObject pObject, RandomTagEntryLoot pContext, JsonSerializationContext pConditions) {
            super.serializeCustom(pObject, pContext, pConditions);
            pObject.addProperty("name", pContext.tag.location().toString());
        }

        @Override
        protected @NotNull RandomTagEntryLoot deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, LootItemFunction[] pFunctions) {
            ResourceLocation $$6 = new ResourceLocation(GsonHelper.getAsString(pObject, "name"));
            TagKey<Item> $$7 = TagKey.create(Registries.ITEM, $$6);
            return new RandomTagEntryLoot($$7, pWeight, pQuality, pConditions, pFunctions);
        }
    }
}
