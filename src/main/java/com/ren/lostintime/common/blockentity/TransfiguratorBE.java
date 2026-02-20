package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.block.TransfiguratorBlock;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.recipe.TransfiguratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TransfiguratorBE extends BlockEntity {

    protected int cookingProgress;
    protected int cookingTotalTime;

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 1) {
                return Config.transfiguratorTranslators.containsKey(stack.getItem());
            }
            // Slot 3 (Nutrient) accepts any item, validity is checked in recipe
            return super.isItemValid(slot, stack);
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> topHandler = LazyOptional.of(() -> new RangedWrapper(itemHandler, 0, 1));
    private final LazyOptional<IItemHandler> bottomHandler = LazyOptional.of(() -> new RangedWrapper(itemHandler, 4, 5) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    });
    private final LazyOptional<IItemHandler> sideHandler = LazyOptional.of(() -> new RangedWrapper(itemHandler, 1, 4));

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> TransfiguratorBE.this.cookingProgress;
                case 1 -> TransfiguratorBE.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> TransfiguratorBE.this.cookingProgress = pValue;
                case 1 -> TransfiguratorBE.this.cookingTotalTime = pValue;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public TransfiguratorBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.TRANSFIGURATOR.get(), pPos, pBlockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        topHandler.invalidate();
        bottomHandler.invalidate();
        sideHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Inventory", itemHandler.serializeNBT());
        pTag.putInt("CookingProgress", cookingProgress);
        pTag.putInt("CookingTotalTime", cookingTotalTime);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("Inventory"));
        cookingProgress = pTag.getInt("CookingProgress");
        cookingTotalTime = pTag.getInt("CookingTotalTime");
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (this.getBlockState().getValue(TransfiguratorBlock.HALF) == DoubleBlockHalf.UPPER) {
                if (level != null) {
                    BlockEntity below = level.getBlockEntity(worldPosition.below());
                    if (below instanceof TransfiguratorBE) {
                        return below.getCapability(cap, side);
                    }
                }
            } else {
                if (side == Direction.UP) {
                    return topHandler.cast();
                }
                if (side == Direction.DOWN) {
                    return bottomHandler.cast();
                }
                if (side != null) {
                    return sideHandler.cast();
                }
                return lazyItemHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    protected boolean canProcessRecipe(){
        ItemStack translatorStack = itemHandler.getStackInSlot(1);
        if( translatorStack.isEmpty() || Config.transfiguratorTranslators.containsKey(translatorStack.getItem())) return false;
        return itemHandler.getStackInSlot(4).isEmpty();
    }

    protected void finishProcessingRecipe(TransfiguratorRecipe recipe){
        ItemStack translatorStack = itemHandler.getStackInSlot(1);
        float chance = Config.transfiguratorTranslators.getOrDefault(translatorStack.getItem(), 0f);
        ItemStack resultStack;

        if (level.random.nextFloat() < chance) {
            resultStack = recipe.getResultItem(level.registryAccess()).copy();
        } else {
            resultStack = recipe.getFailedResult(level.random);
        }
        ItemStack remaining = itemHandler.insertItem(4, resultStack, true);
        if (remaining.isEmpty()) {
            // Can insert fully
            itemHandler.insertItem(1, resultStack, false);
            itemHandler.extractItem(0, 1, false);
            itemHandler.extractItem(1, 1, false);
            itemHandler.extractItem(2, 1, false);
        }
    }

    protected void reset(){
        cookingProgress = 0;
        cookingTotalTime = 0;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransfiguratorBE be) {
        if (level.isClientSide) return;

        ItemStack inputStack = be.itemHandler.getStackInSlot(0);
        ItemStack nutrientStack = be.itemHandler.getStackInSlot(2);
        ItemStack translatorStack = be.itemHandler.getStackInSlot(1);

        if (inputStack.isEmpty() || nutrientStack.isEmpty() || translatorStack.isEmpty()) {
            be.cookingProgress = 0;
            return;
        }

        SimpleContainer container = new SimpleContainer(2);
        container.setItem(0, inputStack);
        container.setItem(1, nutrientStack);

        Optional<TransfiguratorRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeInit.TRANSFIGURATOR_RECIPE.get(), container, level);

        if (recipe.isPresent()) {
            TransfiguratorRecipe r = recipe.get();
            be.cookingTotalTime = r.getProcessingTime();
            
            be.cookingProgress++;

            if (be.cookingProgress >= be.cookingTotalTime) {
                processRecipe(be, r, translatorStack);
                be.cookingProgress = 0;
            }
        } else {
            be.cookingProgress = 0;
        }
    }

    private static void processRecipe(TransfiguratorBE be, TransfiguratorRecipe recipe, ItemStack translatorStack) {
        float chance = Config.transfiguratorTranslators.getOrDefault(translatorStack.getItem(), 0f);
        ItemStack resultStack;
        
        if (be.level.random.nextFloat() < chance) {
            resultStack = recipe.getResultItem(be.level.registryAccess()).copy();
        } else {
            resultStack = recipe.getFailedResult(be.level.random);
        }

        if (resultStack.isEmpty()) {
             // Failed and no failed result (or empty result), just consume inputs
             be.itemHandler.extractItem(0, 1, false);
             be.itemHandler.extractItem(1, 1, false);
             be.itemHandler.extractItem(2, 1, false);
             return;
        }

        // Try to insert result
        ItemStack remaining = be.itemHandler.insertItem(4, resultStack, true);
        if (remaining.isEmpty()) {
            // Can insert fully
            be.itemHandler.insertItem(1, resultStack, false);
            be.itemHandler.extractItem(0, 1, false);
            be.itemHandler.extractItem(1, 1, false);
            be.itemHandler.extractItem(2, 1, false);
        }
    }
}
