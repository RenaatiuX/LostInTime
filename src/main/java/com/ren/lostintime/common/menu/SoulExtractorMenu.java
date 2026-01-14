package com.ren.lostintime.common.menu;

import com.ren.lostintime.common.init.MenuInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.recipe.SoulExtractorRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SoulExtractorMenu extends AbstractContainerMenu {

    private final IItemHandlerModifiable container;
    private final ContainerData containerData;

    public SoulExtractorMenu(int pContainerId, Inventory playerInventory) {
        this(pContainerId, playerInventory, new ItemStackHandler(9), new SimpleContainerData(2));
    }

    public SoulExtractorMenu(int id, Inventory playerInventory, IItemHandlerModifiable container, ContainerData containerData) {
        super(MenuInit.SOUL_EXTRACTOR_MENU.get(), id);
        this.container = container;
        this.containerData = containerData;

        // Inputs
        for (int i = 0; i < 3; i++) {
            addSlot(new SlotItemHandler(container, i, 18 + 18 * i, 26));
        }

        // Soul source
        addSlot(new SlotItemHandler(container, 3, 18, 54));

        // Catalyst
        addSlot(new SlotItemHandler(container, 4, 54, 54));

        // Residues
        for (int i = 0; i < 3; i++) {
            addSlot(new SlotItemHandler(container, 5 + i, 112 + 18 * i, 55));
        }

        // Output
        addSlot(new SlotItemHandler(container, 8, 130, 26){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
        addDataSlots(containerData);
    }

    public int getProgress() {
        return containerData.get(0);
    }

    public int getResidue() {
        return containerData.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemStack = current.copy();
            final int inventorySlots = 36;
            final int soulExtractorSlots = 9;
            final int bottomRowEnd = inventorySlots + soulExtractorSlots;
            final int bottomRowStart = bottomRowEnd - 9;
            if (pIndex < soulExtractorSlots) {
                if (!moveItemStackTo(current, soulExtractorSlots, bottomRowEnd, true)) {
                    return ItemStack.EMPTY;
                }
            }else if (pIndex < bottomRowStart) {
                if (!moveItemStackTo(current, bottomRowStart, bottomRowEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex < bottomRowEnd && !moveItemStackTo(current, soulExtractorSlots, bottomRowStart, false)) {
                return ItemStack.EMPTY;
            }
            if (current.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (current.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, current);
        }
        return itemStack;
    }

    //im to lazy to make anything here
    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
