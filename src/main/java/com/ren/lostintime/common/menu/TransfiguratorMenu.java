package com.ren.lostintime.common.menu;

import com.ren.lostintime.common.blockentity.TransfiguratorBE;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.MenuInit;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TransfiguratorMenu extends AbstractContainerMenu {

    public final TransfiguratorBE blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess levelAccess;

    public TransfiguratorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public TransfiguratorMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(MenuInit.TRANSFIGURATOR_MENU.get(), pContainerId);
        checkContainerSize(inv, 5);
        blockEntity = (TransfiguratorBE) entity;
        this.data = data;
        this.levelAccess = ContainerLevelAccess.create(inv.player.level(), entity.getBlockPos());

        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 16, 36){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return isValidInput(stack);
            }
        }); // Input
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 42, 36){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return Config.transfiguratorTranslators.containsKey(stack.getItem());
            }
        }); // Translator
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 64, 45){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return isValidNutrient(stack);
            }
        }); // Nutrient
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 64, 27){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        }); // something which doesnt exist
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 4, 148, 36){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        }); // Output

        addDataSlots(data);

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    private boolean isValidInput(ItemStack stack) {
        return blockEntity.getLevel().getRecipeManager().getAllRecipesFor(RecipeInit.TRANSFIGURATOR_RECIPE.get())
                .stream().anyMatch(r -> r.getInput().test(stack));
    }

    private boolean isValidNutrient(ItemStack stack) {
        return blockEntity.getLevel().getRecipeManager().getAllRecipesFor(RecipeInit.TRANSFIGURATOR_RECIPE.get())
                .stream().anyMatch(r -> r.getNutrient().test(stack));
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);  // Max Progress
        int progressArrowSize = 57; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 88 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 146));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = slot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (index < 5) { // BE Slots (0-4) -> Player Inventory
            if (!this.moveItemStackTo(sourceStack, 5, 40, true)) {
                return ItemStack.EMPTY;
            }
        } else { // Player Inventory -> BE Slots
            if (blockEntity.isItemValid(0, sourceStack)) { // Input
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (blockEntity.isItemValid(1, sourceStack)) { // Translator
                if (!this.moveItemStackTo(sourceStack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (blockEntity.isItemValid(2, sourceStack)) { // Nutrient
                if (!this.moveItemStackTo(sourceStack, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (sourceStack.getCount() == copyStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(playerIn, sourceStack);
        return copyStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(levelAccess, pPlayer, BlockInit.TRANSFIGURATOR.get());
    }
}
