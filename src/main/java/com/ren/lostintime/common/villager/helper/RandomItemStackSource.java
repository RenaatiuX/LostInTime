package com.ren.lostintime.common.villager.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class RandomItemStackSource {

    private final @Nullable Item item;
    private final @Nullable TagKey<Item> tag;
    private final int count;

    private RandomItemStackSource(@Nullable Item item, @Nullable TagKey<Item> tag, int count) {
        this.item = item;
        this.tag = tag;
        this.count = count;
    }

    public static RandomItemStackSource of(Item item, int count) {
        return new RandomItemStackSource(item, null, count);
    }

    public static RandomItemStackSource of(TagKey<Item> tag, int count) {
        return new RandomItemStackSource(null, tag, count);
    }

    public Optional<ItemStack> resolve(RandomSource random) {
        if (item != null) {
            return Optional.of(new ItemStack(item, count));
        }

        if (tag != null) {
            return BuiltInRegistries.ITEM
                    .getTag(tag)
                    .flatMap(set -> set.getRandomElement(random))
                    .map(holder -> new ItemStack(holder.value(), count));
        }

        return Optional.empty();
    }

}
