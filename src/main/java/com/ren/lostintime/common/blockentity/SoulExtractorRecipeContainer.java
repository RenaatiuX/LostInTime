package com.ren.lostintime.common.blockentity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SoulExtractorRecipeContainer extends SimpleContainer {

    protected final IItemHandlerModifiable input;
    private final ItemStack soulSource, catalyst;

    public SoulExtractorRecipeContainer(IItemHandlerModifiable input, ItemStack soulSource, ItemStack catalyst) {
        super(0);
        this.input = input;
        this.soulSource = soulSource;
        this.catalyst = catalyst;
    }

    public IItemHandlerModifiable getInput() {
        return input;
    }

    public ItemStack getCatalyst() {
        return catalyst;
    }

    public ItemStack getSoulSource() {
        return soulSource;
    }

    public int getAmountInputSlots() {
        return input.getSlots();
    }
}
