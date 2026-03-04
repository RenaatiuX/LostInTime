package com.ren.lostintime.common.util;

import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class ModToolTiers {

    public static final Tier ZIRCON = new ForgeTier(
            4,
            2031,
            8.0f,
            2.0f,
            15,
            BlockTags.NEEDS_DIAMOND_TOOL,
            () -> Ingredient.of(ItemInit.ZIRCON.get())
    );

}
