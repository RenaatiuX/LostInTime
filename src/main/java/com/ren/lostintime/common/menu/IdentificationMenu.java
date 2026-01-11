package com.ren.lostintime.common.menu;

import com.ren.lostintime.common.init.MenuInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.recipe.IdentificationRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IdentificationMenu extends AbstractContainerMenu {

    public static final int DURATION = 300;

    private final Container container;
    private final ContainerData containerData;

    public IdentificationMenu(int pContainerId, Inventory playerInventory) {
        this(pContainerId, playerInventory, new SimpleContainer(10), new SimpleContainerData(3));
    }

    public IdentificationMenu(int id, Inventory playerInventory, Container container, ContainerData containerData) {
        super(MenuInit.IDENTIFICATION_TABLE_MENU.get(), id);
        this.container = container;
        this.containerData = containerData;

        addSlot(new Slot(container, 0, 27, 34));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                addSlot(new FurnaceResultSlot(playerInventory.player, container, 1 + (i * 3) + j,91 + j * 18, 17 + i * 18));
            }
        }

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

    private IdentificationRecipe getRecipeItem(Container container, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(RecipeInit.IDENTIFICATION_RECIPE.get(), container, level)
                .orElse(null);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemStack = current.copy();
            final int inventorySlots = 36;
            final int identificationSlots = 10;
            final int bottomRowEnd = inventorySlots + identificationSlots;
            final int bottomRowStart = bottomRowEnd - 9;
            if (pIndex < identificationSlots) {
                if (!moveItemStackTo(current, identificationSlots, bottomRowEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (getRecipeItem(new SimpleContainer(itemStack), pPlayer.level()) != null) {
                if (!moveItemStackTo(current, 0, identificationSlots, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex < bottomRowStart) {
                if (!moveItemStackTo(current, bottomRowStart, bottomRowEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex < bottomRowEnd && !moveItemStackTo(current, identificationSlots, bottomRowStart, false)) {
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

    @Override
    public boolean stillValid(Player pPlayer) {
        return container.stillValid(pPlayer);
    }
}
