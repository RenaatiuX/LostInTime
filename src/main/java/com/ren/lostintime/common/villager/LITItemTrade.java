package com.ren.lostintime.common.villager;

import com.ren.lostintime.common.villager.helper.RandomItemStackSource;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LITItemTrade implements VillagerTrades.ItemListing {

    private final RandomItemStackSource inputA;
    private final @Nullable RandomItemStackSource inputB;
    private final RandomItemStackSource result;
    private final int maxUses;
    private final int xp;
    private final float priceMultiplier;

    public LITItemTrade(RandomItemStackSource inputA, @Nullable RandomItemStackSource inputB, RandomItemStackSource result,
            int maxUses, int xp, float priceMultiplier) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.result = result;
        this.maxUses = maxUses;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public @Nullable MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        Optional<ItemStack> a = inputA.resolve(pRandom);
        Optional<ItemStack> b = inputB != null ? inputB.resolve(pRandom) : Optional.empty();
        Optional<ItemStack> out = result.resolve(pRandom);

        if (a.isEmpty() || out.isEmpty()) return null;

        return b.map(itemStack -> new MerchantOffer(a.get(), itemStack, out.get(), maxUses, xp, priceMultiplier))
                .orElseGet(() -> new MerchantOffer(a.get(), out.get(), maxUses, xp, priceMultiplier));
    }
}
