package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.block.IdentificationTableBlock;
import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.menu.IdentificationMenu;
import com.ren.lostintime.common.recipe.IdentificationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IdentificationBE extends LITMachineBE implements Container, WorldlyContainer {

    private static final int[] INPUT = new int[]{0};
    private static final int[] OUTPUT = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            if (pIndex == 0) {
                return cookingProgress;
            }
            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {
            if (pIndex == 0) {
                cookingProgress = pValue;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    protected NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);

    public IdentificationBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.IDENTIFICATION_TABLE.get(), pPos, pBlockState);
    }

    public static BlockEntity get(BlockPos pos, BlockState state) {
        return new IdentificationBE(pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean wasProcessing = cookingProgress > 0;
        boolean dirty = false;

        if (canProcess()) {
            cookingProgress++;
            if (cookingProgress >= IdentificationMenu.DURATION) {
                cookingProgress = 0;
                createItem();
                dirty = true;
            }
        } else {
            cookingProgress = Mth.clamp(cookingProgress - 2, 0, IdentificationMenu.DURATION);
        }

        if (wasProcessing != cookingProgress > 0) {
            dirty = true;
            state = state.setValue(IdentificationTableBlock.ON, cookingProgress > 0);
            level.setBlock(pos, state, 3);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    private boolean canBeInvestigated(ItemStack stack) {
        if (level == null) return false;

        SimpleContainer container = new SimpleContainer(stack);
        return level.getRecipeManager()
                .getRecipeFor(RecipeInit.IDENTIFICATION_RECIPE.get(), container, level)
                .isPresent();
    }

    private IdentificationRecipe getRecipeItem(Container container, Level level) {
        return level.getRecipeManager()
                .getRecipeFor(RecipeInit.IDENTIFICATION_RECIPE.get(), container, level)
                .orElse(null);
    }

    protected boolean canProcess() {
        ItemStack itemStack = items.get(0);
        if (!itemStack.isEmpty()) {
            if (canBeInvestigated(itemStack)) {
                for (int outputIndex = 1; outputIndex <= 9; outputIndex++) {
                    if (items.get(outputIndex).isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    protected void createItem() {
        if (canProcess()) {
            ItemStack result = getRecipeItem(new SimpleContainer(items.get(0)), level).assemble(this, level.registryAccess()).copy();
            for (int slot = 1; slot <= 9; slot++) {
                ItemStack stackInSlot = items.get(slot);
                if (ItemStack.isSameItem(stackInSlot, result) && stackInSlot.getCount() + result.getCount() < 64) {
                    stackInSlot.grow(result.getCount());
                    if (items.get(0).getCount() > 1) {
                        items.get(0).shrink(1);
                    } else {
                        items.set(0, ItemStack.EMPTY);
                    }
                    return;
                }
            }
            for (int slot = 1; slot <= 9; slot++) {
                ItemStack stackInSlot = items.get(slot);
                if (stackInSlot.isEmpty()) {
                    items.set(slot, result);
                    if (items.get(0).getCount() > 1) {
                        items.get(0).shrink(1);
                    } else {
                        items.set(0, ItemStack.EMPTY);
                    }
                    return;
                }
            }
        }
    }

    public @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return pIndex == 0 && canBeInvestigated(pStack);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return pSide == Direction.DOWN ? OUTPUT : INPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return canPlaceItem(pIndex, pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return pDirection != Direction.UP && pIndex >= OUTPUT[0];
    }

    @Override
    protected Component getDefaultName() {
        return Component.nullToEmpty("container.lostintime.identification");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new IdentificationMenu(pContainerId, pInventory, this, data);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        items.set(pSlot, pStack);
        if (pStack.getCount() > getMaxStackSize()) {
            pStack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public ContainerData getDataAccess() {
        return data;
    }
}
