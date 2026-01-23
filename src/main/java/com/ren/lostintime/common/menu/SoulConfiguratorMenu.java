package com.ren.lostintime.common.menu;

import com.ren.lostintime.common.init.MenuInit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SoulConfiguratorMenu extends AbstractContainerMenu {

    private final IItemHandlerModifiable container;
    private final ContainerData containerData;

    public SoulConfiguratorMenu(int pContainerId, Inventory playerInventory) {
        this(pContainerId, playerInventory, new ItemStackHandler(6), new SimpleContainerData(2));
    }

    public SoulConfiguratorMenu(int id, Inventory playerInventory, IItemHandlerModifiable container, ContainerData containerData) {
        super(MenuInit.SOUL_CONFIGURATOR_MENU.get(), id);
        this.container = container;
        this.containerData = containerData;

        //Precedent matter
        addSlot(new SlotItemHandler(container, 0, 12, 28));

        //Lapis lazuli
        addSlot(new SlotItemHandler(container, 1, 12, 57));

        //Aspect
        addSlot(new SlotItemHandler(container, 2, 48, 27));

        //Bindin matterial
        addSlot(new SlotItemHandler(container, 3, 100, 27));

        //soul powder
        addSlot(new SlotItemHandler(container,4, 76, 60));

        //output
        addSlot(new SlotItemHandler(container, 5, 139, 56));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 91 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 149));
        }
        addDataSlots(containerData);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemStack = current.copy();
            final int inventorySlots = 36;
            final int soulConfiguratorSlots = 6;
            final int bottomRowEnd = inventorySlots + soulConfiguratorSlots;
            final int bottomRowStart = bottomRowEnd - 9;
            if (pIndex < soulConfiguratorSlots) {
                if (!moveItemStackTo(current, soulConfiguratorSlots, bottomRowEnd, true)) {
                    return ItemStack.EMPTY;
                }
            }else if (pIndex < bottomRowStart) {
                if (!moveItemStackTo(current, bottomRowStart, bottomRowEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex < bottomRowEnd && !moveItemStackTo(current, soulConfiguratorSlots, bottomRowStart, false)) {
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
        return false;
    }
}
