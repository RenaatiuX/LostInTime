package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.menu.SoulExtractorMenu;
import com.ren.lostintime.common.recipe.SoulExtractorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SoulExtractorBE extends LITMachineBE {

    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 2;
    public static final int SLOT_SOUL_SOURCE = 3;
    public static final int SLOT_CATALYST = 4;
    public static final int SLOT_RESIDUE_START = 5;
    public static final int SLOT_RESIDUE_END = 7;
    public static final int SLOT_OUTPUT = 8;

    private static final int BASE_PROCESS_TIME = 200;

    private final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> cookingProgress;
                case 1 -> cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> cookingProgress = pValue;
                case 1 -> cookingTotalTime = pValue;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public SoulExtractorBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.SOUL_EXTRACTOR.get(), pPos, pBlockState);
        this.cookingTotalTime = BASE_PROCESS_TIME;
    }

    @Override
    public ContainerData getDataAccess() {
        return data;
    }

    @Override
    protected boolean canProcess() {
        if (level == null) return false;

        Optional<SoulExtractorRecipe> recipeOpt =
                level.getRecipeManager()
                        .getRecipeFor(RecipeInit.SOUL_EXTRACTOR_RECIPE.get(), this, level);

        if (recipeOpt.isEmpty()) return false;
        SoulExtractorRecipe recipe = recipeOpt.get();
        ItemStack output = recipe.getResultItem(level.registryAccess());
        ItemStack currentOutput = items.get(SLOT_OUTPUT);
        if (currentOutput.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(currentOutput, output)) return false;
        return currentOutput.getCount() + output.getCount()
                <= currentOutput.getMaxStackSize();
    }

    @Override
    protected void createItem() {
        if (level == null) return;

        Optional<SoulExtractorRecipe> recipeOpt =
                level.getRecipeManager()
                        .getRecipeFor(RecipeInit.SOUL_EXTRACTOR_RECIPE.get(), this, level);

        if (recipeOpt.isEmpty()) return;

        SoulExtractorRecipe recipe = recipeOpt.get();

        for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++) {
            if (!items.get(i).isEmpty()) {
                items.get(i).shrink(1);
            }
        }
        items.get(SLOT_SOUL_SOURCE).shrink(1);

        if (!items.get(SLOT_CATALYST).isEmpty()) {
            items.get(SLOT_CATALYST).shrink(1);
        }

        ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
        ItemStack outputSlot = items.get(SLOT_OUTPUT);

        if (outputSlot.isEmpty()) {
            items.set(SLOT_OUTPUT, result);
        } else {
            outputSlot.grow(result.getCount());
        }

        insertResidue(new ItemStack(ItemInit.DAEODON_FOSSIL.get()));

        cookingProgress = 0;
        setChanged();
    }

    private void insertResidue(ItemStack residue) {
        for (int i = SLOT_RESIDUE_START; i <= SLOT_RESIDUE_END; i++) {
            ItemStack slot = items.get(i);

            if (slot.isEmpty()) {
                items.set(i, residue.copy());
                return;
            }

            if (ItemStack.isSameItemSameTags(slot, residue)
                    && slot.getCount() < slot.getMaxStackSize()) {
                slot.grow(1);
                return;
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulExtractorBE be) {
        if (!be.canProcess()) {
            be.cookingProgress = 0;
            return;
        }
        be.cookingProgress++;
        if (be.cookingProgress >= be.cookingTotalTime) {
            be.createItem();
        }
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return switch (pSide) {
            case DOWN -> new int[]{
                    SLOT_OUTPUT,
                    SLOT_RESIDUE_START,
                    SLOT_RESIDUE_START + 1,
                    SLOT_RESIDUE_END
            };
            case UP -> new int[]{
                    SLOT_INPUT_START,
                    SLOT_INPUT_START + 1,
                    SLOT_INPUT_END
            };
            default -> new int[]{
                    SLOT_SOUL_SOURCE,
                    SLOT_CATALYST
            };
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return pIndex <= SLOT_CATALYST;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return pIndex >= SLOT_RESIDUE_START;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.lostintime.soul_extractor");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new SoulExtractorMenu(pContainerId, pInventory, this, getDataAccess());
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        items.set(pSlot, pStack);
        if (pSlot <= SLOT_CATALYST) {
            setChanged();
        }
    }
}
