package com.ren.lostintime.common.item;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.common.entity.util.TimePeriod;
import com.ren.lostintime.common.init.CapabilityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PrehistoricBookItem extends Item {

    public PrehistoricBookItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide) {
            openBookScreen(itemstack);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    public static Set<TimePeriod> discoveredTimePeriods(ItemStack book) {
        var nbt = book.getOrCreateTag();
        Set<TimePeriod> periods = new HashSet<>();
        if (nbt.contains("periods", 9)) {
            var listTag = nbt.getList("periods", 3);
            for (int i = 0; i < listTag.size(); i++) {
                int ordinal = listTag.getInt(i);
                periods.add(TimePeriod.values()[ordinal]);
            }
        }
        return periods;
    }

    public static void discoverTimePeriod(ItemStack book, TimePeriod period){
        var nbt = book.getOrCreateTag();
        var discoveredPeriods = discoveredTimePeriods(book);
        discoveredPeriods.add(period);


        ListTag periodListTag = new ListTag();
        for (TimePeriod p : discoveredPeriods) {
            periodListTag.add(IntTag.valueOf(p.ordinal()));
        }
        nbt.put("periods", periodListTag);

    }

    public static Set<EntityType<?>> discoveredEntities(ItemStack book){
        var nbt = book.getOrCreateTag();
        Set<EntityType<?>> entities = new HashSet<>();
        if (nbt.contains("discoveredEntities", 9)) {
            var listTag = nbt.getList("discoveredEntities", 8);
            for (int i = 0; i < listTag.size(); i++) {
                var entityType = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.parse(listTag.getString(i)));
                if (entityType != null) {
                    entities.add(entityType);
                }
            }

        }
        return entities;
    }

    public static void discoverEntity(ItemStack book, EntityType<?> entity){
        var nbt = book.getOrCreateTag();
        var discoveredEntities = discoveredEntities(book);
        discoveredEntities.add(entity);

        ListTag entityListTag = new ListTag();
        for (EntityType<?> description : discoveredEntities) {
            entityListTag.add(StringTag.valueOf(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(description)).toString()));
        }
        nbt.put("discoveredEntities", entityListTag);
    }

    private void openBookScreen(ItemStack book) {
        Minecraft.getInstance().setScreen(new PrehistoricBookScreen(book));
    }
}
