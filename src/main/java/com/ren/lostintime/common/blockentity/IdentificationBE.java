package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.block.IdentificationTableBlock;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.menu.IdentificationMenu;
import com.ren.lostintime.common.recipe.IdentificationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IdentificationBE extends BlockEntity implements MenuProvider {

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> processCounter;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            //no need
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    protected ItemStackHandler items = new ItemStackHandler(10){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0){
                return canBeInvestigated(level, stack);
            }
            return super.isItemValid(slot, stack);
        }
    };
    protected IItemHandlerModifiable output = new RangedWrapper(items, 1, 10);

    protected int processCounter = 0;

    public IdentificationBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.IDENTIFICATION_TABLE.get(), pPos, pBlockState);
    }

    public static BlockEntity get(BlockPos pos, BlockState state) {
        return new IdentificationBE(pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean dirty = false;

        var recipe = getRecipe();

        if (recipe != null && canProcess(recipe)) {
            if (processCounter >= Config.identificationTableProcessTime) {
                processCounter = 0;
                finishProcessing(recipe);
                dirty = true;
            }
            processCounter++;
        } else if (processCounter > 0) {
            processCounter = Mth.clamp(processCounter - 2, 0, Config.identificationTableProcessTime);
        }

        if (state.getValue(IdentificationTableBlock.ON) != processCounter > 0) {
            dirty = true;
            //this actually triggers a block update itself, cause of the 3 flag
            level.setBlock(pos, state.setValue(IdentificationTableBlock.ON, processCounter > 0), 3);

        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", items.serializeNBT());
        pTag.putInt("processCounter", processCounter);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("inventory")){
            items.deserializeNBT(pTag.getCompound("inventory"));
        }
        if (pTag.contains("processCounter")){
            processCounter = pTag.getInt("processCounter");
        }
    }

    public static boolean canBeInvestigated(Level level, ItemStack stack) {
        if (level == null) return false;

        SimpleContainer container = new SimpleContainer(stack);
        return level.getRecipeManager()
                .getRecipeFor(RecipeInit.IDENTIFICATION_RECIPE.get(), container, level)
                .isPresent();
    }

    private @Nullable IdentificationRecipe getRecipe() {
        return level.getRecipeManager()
                .getRecipeFor(RecipeInit.IDENTIFICATION_RECIPE.get(), new SimpleContainer(items.getStackInSlot(0)), level).orElse(null);
    }

    protected boolean canProcess(IdentificationRecipe recipe) {
        for (int outputIndex = 0; outputIndex < output.getSlots(); outputIndex++) {
            if (output.getStackInSlot(outputIndex).isEmpty()) {
                return true;
            }
        }
        //if all outputs fit in simultaneously even tho we might be full, ensures that we can process actually
        for (ItemStack stack : recipe.allPossibleOutputs()){
            if (!ItemHandlerHelper.insertItemStacked(output, stack, true).isEmpty())
                return false;
        }
        return true;
    }

    protected void finishProcessing(@NotNull IdentificationRecipe recipe) {
        ItemStack result = recipe.getRandomOutput(level.random).copy();
        ItemHandlerHelper.insertItem(output, result, false);
        items.getStackInSlot(0).shrink(1);
    }


    @Override
    public Component getDisplayName() {
        return Component.nullToEmpty("container.lostintime.identification");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player player) {
        return new IdentificationMenu(pContainerId, pInventory, this.items, data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
            if (side == Direction.UP){
                return LazyOptional.of(() -> new RangedWrapper(items, 0, 1)).cast();
            }if (side == null){
                return LazyOptional.of(() -> items).cast();
            }
            return LazyOptional.of(() -> output).cast();
        }
        return super.getCapability(cap, side);
    }
}
