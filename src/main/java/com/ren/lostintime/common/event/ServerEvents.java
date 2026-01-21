package com.ren.lostintime.common.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.init.VillagerInit;
import com.ren.lostintime.common.villager.LITItemTrade;
import com.ren.lostintime.common.villager.helper.RandomItemStackSource;
import com.ren.lostintime.datagen.server.LITTags;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = LostInTime.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractIllager illager)) return;
        if (event.getLevel().isClientSide()) return;

        illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, Dodo.class, true));
    }

    @SubscribeEvent
    public static void AddFossilMasterTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerInit.FOSSIL_MASTER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add(((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.BONE, 8),
                    new ItemStack(Items.EMERALD, 1),
                    20, 2, 0.02F)));
            trades.get(1).add(new LITItemTrade(
                    RandomItemStackSource.of(LITTags.Items.UNIDENTIFIED_FOSSIL, 1),
                    null,
                    RandomItemStackSource.of(Items.EMERALD, 1), 12, 2, 0.02F));

            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(Items.BRUSH, 1),
                    8, 2, 0.02F));

            trades.get(3).add(new LITItemTrade(
                    RandomItemStackSource.of(Items.EMERALD, 8),
                    null,
                    RandomItemStackSource.of(ItemInit.DODO_FOSSIL_MOUNT.get(), 1), 12, 2, 0.02F));
            trades.get(3).add(new LITItemTrade(
                    RandomItemStackSource.of(Items.EMERALD, 2),
                    null,
                    RandomItemStackSource.of(Blocks.BONE_BLOCK.asItem(), 1), 10, 2, 0.02F));

            trades.get(4).add(new LITItemTrade(
                    RandomItemStackSource.of(Items.BOOK, 1),
                    RandomItemStackSource.of(Items.EMERALD, 20),
                    RandomItemStackSource.of(Blocks.BONE_BLOCK.asItem(), 1), 12, 2, 0.02F));
        }
    }

}
