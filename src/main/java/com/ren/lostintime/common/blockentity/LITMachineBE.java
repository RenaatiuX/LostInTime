package com.ren.lostintime.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class LITMachineBE extends BaseContainerBlockEntity implements Container, WorldlyContainer {

    protected int litTime;
    protected int litDuration;
    protected int cookingProgress;
    protected int cookingTotalTime;

    protected LITMachineBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ContainerHelper.loadAllItems(pTag, getItems());
        this.litTime = pTag.getShort("LitTime");
        this.litDuration = pTag.getShort("LitDuration");
        this.cookingProgress = pTag.getShort("CookingProgress");
        this.cookingTotalTime = pTag.getShort("CookingTotalTime");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putShort("LitTime", (short) litTime);
        pTag.putShort("LitDuration", (short) litDuration);
        pTag.putShort("CookingProgress", (short) cookingProgress);
        pTag.putShort("CookingTotalTime", (short) cookingTotalTime);
        ContainerHelper.saveAllItems(pTag, getItems());
    }

    @Override
    public int getContainerSize() {
        return getItems().size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : getItems()) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(getItems(), slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(getItems(), slot);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        getItems().clear();
    }

    public abstract ContainerData getDataAccess();

    protected abstract boolean canProcess();

    protected abstract void createItem();

    protected abstract @NotNull NonNullList<ItemStack> getItems();
}
