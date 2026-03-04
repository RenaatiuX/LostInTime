package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.block.SoulExtractorBlock;
import com.ren.lostintime.common.block.TransfiguratorBlock;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.menu.TransfiguratorMenu;
import com.ren.lostintime.common.recipe.TransfiguratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

public class TransfiguratorBE extends BlockEntity implements MenuProvider {

    protected int cookingProgress;
    protected int cookingTotalTime;
    private TransfiguratorRecipe.Type currentType;

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
            // Slot 2 (Nutrient) accepts any item, validity is checked in recipe
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
        if (currentType != null) {
            pTag.putInt("Type", currentType.ordinal());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("Inventory"));
        cookingProgress = pTag.getInt("CookingProgress");
        cookingTotalTime = pTag.getInt("CookingTotalTime");
        if (pTag.contains("Type")) {
            currentType = TransfiguratorRecipe.Type.values()[pTag.getInt("Type")];
        } else {
            currentType = null;
        }
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

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return itemHandler.isItemValid(slot, stack);
    }

    public TransfiguratorRecipe.Type getCurrentType() {
        return currentType;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.lostintime.transfigurator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TransfiguratorMenu(pContainerId, pPlayerInventory, this, this.dataAccess);
    }

    protected boolean canProcessRecipe(TransfiguratorRecipe recipe) {
        /*ItemStack translatorStack = itemHandler.getStackInSlot(1);
        if (translatorStack.isEmpty() || !Config.transfiguratorTranslators.containsKey(translatorStack.getItem()))
            return false;
        return itemHandler.getStackInSlot(4).isEmpty();*/
        ItemStack translatorStack = itemHandler.getStackInSlot(1);
        if (translatorStack.isEmpty() || !Config.transfiguratorTranslators.containsKey(translatorStack.getItem())) {
            return false;
        }

        if (itemHandler.getStackInSlot(0).isEmpty()) {
            return false;
        }

        ItemStack outputSlot = itemHandler.getStackInSlot(4);
        if (outputSlot.isEmpty()) {
            return true;
        }

        ItemStack resultStack = recipe.getResultItem(level.registryAccess());
        if (!outputSlot.is(resultStack.getItem())) return false;
        return outputSlot.getCount() + resultStack.getCount() <= outputSlot.getMaxStackSize();
    }

    protected void finishProcessingRecipe(TransfiguratorRecipe recipe) {
        /*ItemStack translatorStack = itemHandler.getStackInSlot(1);
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
            itemHandler.insertItem(4, resultStack, false);
            itemHandler.extractItem(0, 1, false);
            itemHandler.extractItem(1, 1, false);
            itemHandler.extractItem(2, 1, false);
        }
        reset();*/
        ItemStack translatorStack = itemHandler.getStackInSlot(1);
        float successChance = Config.transfiguratorTranslators.getOrDefault(translatorStack.getItem(), 0f);
        ItemStack finalResult;

        if (level.random.nextFloat() < successChance) {
            finalResult = recipe.getResultItem(level.registryAccess()).copy();
        } else {
            finalResult = recipe.getFailedResult(level.random);
        }

        itemHandler.insertItem(4, finalResult, false);
        itemHandler.extractItem(0, 1, false); // Soul Config
        if (recipe.getSolution() != null && recipe.getSolution() != Ingredient.EMPTY) {
            itemHandler.extractItem(3, 1, false);
        } // Solution
        itemHandler.extractItem(2, 1, false); // Nutrient
        itemHandler.extractItem(1, 1, false);

        reset();
    }

    protected void reset() {
        cookingProgress = 0;
        cookingTotalTime = 0;
        if (currentType != null) {
            currentType = null;
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransfiguratorBE be) {
        if (level.isClientSide) return;

        ItemStack translator = be.itemHandler.getStackInSlot(1);
        if (translator.isEmpty() || !Config.transfiguratorTranslators.containsKey(translator.getItem())) {
            if (be.cookingProgress > 0) be.reset();
            updateState(level, pos, state, false);
            return;
        };

        SimpleContainer tempContainer = new SimpleContainer(5);
        for (int i = 0; i < 5; i++) {
            tempContainer.setItem(i, be.itemHandler.getStackInSlot(i));
        }

        Optional<TransfiguratorRecipe> match = level.getRecipeManager()
                .getRecipeFor(RecipeInit.TRANSFIGURATOR_RECIPE.get(), tempContainer, level);

        if (match.isPresent()) {
            TransfiguratorRecipe recipe = match.get();

            ItemStack outputSlot = be.itemHandler.getStackInSlot(4);
            ItemStack resultStack = recipe.getResultItem(level.registryAccess());
            boolean canInsert = outputSlot.isEmpty() ||
                    (outputSlot.is(resultStack.getItem()) && outputSlot.getCount() + resultStack.getCount() <= outputSlot.getMaxStackSize());

            if (canInsert) {
                be.cookingTotalTime = recipe.getProcessingTime();
                if (be.currentType != recipe.getProcessingType()) {
                    be.currentType = recipe.getProcessingType();
                    be.setChanged();
                }

                be.cookingProgress++;

                if (be.cookingProgress >= be.cookingTotalTime) {
                    be.finishProcessingRecipe(recipe);
                }
                updateState(level, pos, state, true);
            } else {
                updateState(level, pos, state, false);
            }
        } else {
            if (be.cookingProgress > 0) be.reset();
            updateState(level, pos, state, false);
        }

        /*if (!be.canProcessRecipe()) {
            be.cookingProgress = 0;
            return;
        }

        SimpleContainer container = new SimpleContainer(2);
        container.setItem(0, inputStack);
        container.setItem(1, nutrientStack); // Recipe expects nutrient in slot 3? Wait.

        // TransfiguratorRecipe matches: input.test(pContainer.getItem(0)) && nutrient.test(pContainer.getItem(3));
        // But here nutrient is in slot 2.
        // I should update TransfiguratorRecipe or the container passed here.
        // If I pass a container of size 5, and put nutrient in slot 3, it matches the recipe expectation.
        // But in BE, nutrient is in slot 2.
        // I should probably align them.
        // Let's put nutrient in slot 3 of the temporary container.
        
        Optional<TransfiguratorRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeInit.TRANSFIGURATOR_RECIPE.get(), container, level);

        if (recipe.isPresent()) {
            TransfiguratorRecipe r = recipe.get();
            be.cookingTotalTime = r.getProcessingTime();

            if (be.currentType != r.getProcessingType()) {
                be.currentType = r.getProcessingType();
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }

            be.cookingProgress++;

            if (be.cookingProgress >= be.cookingTotalTime) {
                be.finishProcessingRecipe(r);
                be.reset();
            }
        } else {
            be.reset();
        }
        if (state.getValue(SoulExtractorBlock.ON) != be.cookingProgress > 0){
            level.setBlock(pos, state.setValue(SoulExtractorBlock.ON, be.cookingProgress > 0), Block.UPDATE_ALL);
            var doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            var otherHalfPos = doubleBlockHalf == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            var otherHalfState = level.getBlockState(otherHalfPos);
            level.setBlock(otherHalfPos, otherHalfState.setValue(SoulExtractorBlock.ON, be.cookingProgress > 0), Block.UPDATE_ALL);
        }*/
    }

    private static void updateState(Level level, BlockPos pos, BlockState state, boolean isWorking) {
        if (state.getValue(SoulExtractorBlock.ON) != isWorking) {
            level.setBlock(pos, state.setValue(SoulExtractorBlock.ON, isWorking), Block.UPDATE_ALL);
            var doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            var otherHalfPos = doubleBlockHalf == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            var otherHalfState = level.getBlockState(otherHalfPos);
            level.setBlock(otherHalfPos, otherHalfState.setValue(SoulExtractorBlock.ON, isWorking), Block.UPDATE_ALL);
        }
    }
}
