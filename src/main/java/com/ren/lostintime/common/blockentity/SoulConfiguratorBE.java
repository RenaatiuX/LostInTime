package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.LITMachineBlock;
import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.menu.SoulConfiguratorMenu;
import com.ren.lostintime.common.recipe.SoulConfiguratorFuelRecipe;
import com.ren.lostintime.common.recipe.SoulConfiguratorRecipe;
import com.sun.jdi.connect.spi.TransportService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulConfiguratorBE extends BlockEntity implements MenuProvider {

    protected LazyOptional<IItemHandlerModifiable> inventory = LazyOptional.of(this::createInventory);
    protected LazyOptional<RangedWrapper> fossilInventory = inventory.lazyMap(d -> new RangedWrapper(d, 0, 1));
    protected LazyOptional<RangedWrapper> lapisInventory = inventory.lazyMap(d -> new RangedWrapper(d, 1, 2));
    protected LazyOptional<RangedWrapper> aspectInventory = inventory.lazyMap(d -> new RangedWrapper(d, 2, 3));
    protected LazyOptional<RangedWrapper> fuelInventory = inventory.lazyMap(d -> new RangedWrapper(d, 3, 4));
    protected LazyOptional<RangedWrapper> bindingInventory = inventory.lazyMap(d -> new RangedWrapper(d, 4, 5));
    protected LazyOptional<RangedWrapper> outputInventory = inventory.lazyMap(d -> new RangedWrapper(d, 5, 6));

    public final ContainerData data = new ContainerData() {

        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> (int) Math.round((float)soulFuel * 100f / (float)maxSoulFuel);
                case 1 -> (int) Math.round((float)progress * 100f / (float)maxProgress);
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {

        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    protected int maxSoulFuel = 1;
    protected int soulFuel = 0;

    protected int maxProgress = 0;
    protected int progress = 0;

    public SoulConfiguratorBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.SOUL_CONFIGURATOR.get(), pPos, pBlockState);
    }

    protected IItemHandlerModifiable createInventory() {
        return new ItemStackHandler(6) {

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (slot == 1) {
                    return stack.is(Items.LAPIS_LAZULI);
                } else if (slot == 3) {
                    return level != null && isSoulFuel(stack, level);
                }
                return super.isItemValid(slot, stack);
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulConfiguratorBE be) {
        var recipe = be.getRecipe();
        if (recipe != null && be.canProcess(recipe) && be.checkFuel(recipe)){
            if (be.progress == 0){
                be.startProcessing(recipe);
            }
            if (be.progress >= be.maxProgress){
                be.finishProcessing(recipe);
                be.reset();
            }else {
                be.progress++;
            }
        }else
            be.reset();

        if (state.getValue(LITMachineBlock.ON) != be.progress > 0){
            level.setBlock(pos, state.setValue(LITMachineBlock.ON, be.progress > 0), 3);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            BlockPos topPos = pos.above();
            BlockPos sidePos = pos.relative(facing.getClockWise());

            level.setBlock(topPos, level.getBlockState(topPos).setValue(LITMachineBlock.ON, be.progress > 0), 3);
            level.setBlock(sidePos, level.getBlockState(sidePos).setValue(LITMachineBlock.ON, be.progress > 0), 3);
        }


    }

    @Nullable
    public SoulConfiguratorRecipe getRecipe() {
        var inv = new SimpleContainer(3);

        fossilInventory.ifPresent(d -> inv.setItem(0, d.getStackInSlot(0)));
        aspectInventory.ifPresent(d -> inv.setItem(1, d.getStackInSlot(0)));
        bindingInventory.ifPresent(d -> inv.setItem(2, d.getStackInSlot(0)));

        return level.getRecipeManager().getRecipeFor(RecipeInit.SOUL_CONFIGURATOR_RECIPE.get(), inv, level).orElse(null);
    }

    public boolean canProcess(SoulConfiguratorRecipe recipe){
        //not the most beautiful code ever written
        if (!lapisInventory.map(d -> {
            for (int i = 0; i < d.getSlots(); i++) {
                var stack = d.getStackInSlot(i);
                if (!stack.isEmpty() && stack.is(Items.LAPIS_LAZULI) && stack.getCount() > 1) return true;
            }
            return false;
        }).orElse(false)) return false;


        return outputInventory.map(d -> {
            for (int i = 0; i < d.getSlots(); i++) {
                if (d.getStackInSlot(i).isEmpty()) return true;
            }

            return false;
        }).orElse(false);
    }

    public void startProcessing(SoulConfiguratorRecipe recipe){
        maxProgress = recipe.getProcessTime();
        progress = 0;
    }

    public void finishProcessing(SoulConfiguratorRecipe recipe){
        assert level != null;
        fossilInventory.ifPresent(d -> d.extractItem(0, 1, false));
        aspectInventory.ifPresent(d -> d.extractItem(0, 1, false));
        bindingInventory.ifPresent(d -> d.extractItem(0, 1, false));
        lapisInventory.ifPresent(d -> d.extractItem(0, 1, false));

        soulFuel -= recipe.getRequiredSoulFuel();

        var outputStack = recipe.getResult(level).copy();
        outputInventory.ifPresent(d -> ItemHandlerHelper.insertItem(d, outputStack, false));
    }

    public void reset(){
        maxProgress = 0;
        progress = 0;
    }

    public boolean checkFuel(SoulConfiguratorRecipe recipe){
        if (soulFuel >= recipe.getRequiredSoulFuel())
            return true;
        return fuelInventory.map(d -> {
            int tmpTotalFuel = 0;
            assert level != null;
            for (int i = 0; i < d.getSlots(); i++) {
                var stack = d.getStackInSlot(i);
                while (isSoulFuel(stack, level) && tmpTotalFuel < recipe.getRequiredSoulFuel()){
                    tmpTotalFuel += getSoulFuel(stack, level);
                    stack.shrink(1);
                }
            }
            maxSoulFuel = tmpTotalFuel;
            soulFuel = tmpTotalFuel;
            return soulFuel >= recipe.getRequiredSoulFuel();
        }).orElse(false);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        this.inventory.ifPresent(d -> {
            if (d instanceof INBTSerializable<?> serializable) {
                pTag.put("inventory", serializable.serializeNBT());
            }
        });
        pTag.putInt("progress", progress);
        pTag.putInt("progress", maxProgress);
        pTag.putInt("soulFuel", soulFuel);
        pTag.putInt("maxSoulFuel", maxSoulFuel);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        //not beautiful but rare
        //german saying
        if (pTag.contains("inventory")) {
            this.inventory.ifPresent(d -> {
                if (d instanceof INBTSerializable<?> serializable) {
                    ((INBTSerializable<Tag>) serializable)
                            .deserializeNBT(pTag.get("inventory"));
                }
            });
        }
        progress = pTag.getInt("progress");
        maxProgress = pTag.getInt("maxProgress");
        soulFuel = pTag.getInt("soulFuel");
        maxSoulFuel = pTag.getInt("maxSoulFuel");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
            if (side != null && side.getAxis() != Direction.Axis.Y){
                return inventory.lazyMap(d -> new RangedWrapper(d, 0, 4)).cast();
            }else if (side == Direction.UP){
                return bindingInventory.cast();
            }else if (side == Direction.DOWN)
                return outputInventory.cast();
            return inventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inventory.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + LostInTime.MODID + ".soul_configurator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SoulConfiguratorMenu(pContainerId, pPlayerInventory, inventory.orElseThrow(IllegalStateException::new), data);
    }

    public static boolean isSoulFuel(ItemStack stack, Level level) {
        if (stack.isEmpty())
            return false;
        var container = new SimpleContainer(stack);
        return level.getRecipeManager().getRecipeFor(RecipeInit.SOUL_CONFIGURATOR_FUEL_RECIPE.get(), container, level).isPresent();
    }

    public static int getSoulFuel(ItemStack stack, Level level) {
        if (stack.isEmpty())
            return 0;
        var container = new SimpleContainer(stack);
        return level.getRecipeManager().getRecipeFor(RecipeInit.SOUL_CONFIGURATOR_FUEL_RECIPE.get(), container, level).map(SoulConfiguratorFuelRecipe::getFuelValue).orElse(0);
    }
}
