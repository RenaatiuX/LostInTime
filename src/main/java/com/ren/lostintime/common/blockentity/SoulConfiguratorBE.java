package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.recipe.SoulConfiguratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulConfiguratorBE extends BlockEntity implements MenuProvider {

    protected LazyOptional<IItemHandlerModifiable> inventory = LazyOptional.of(this::createInventory);
    protected LazyOptional<RangedWrapper> fossilInventory =  inventory.lazyMap(d -> new RangedWrapper(d, 0, 1));
    protected LazyOptional<RangedWrapper> lapisInventory = inventory.lazyMap(d -> new RangedWrapper(d, 1, 2));
    protected LazyOptional<RangedWrapper> aspectInventory = inventory.lazyMap(d -> new RangedWrapper(d, 2, 3));
    protected LazyOptional<RangedWrapper> bindingInventory = inventory.lazyMap(d -> new RangedWrapper(d, 3, 4));
    protected LazyOptional<RangedWrapper> outputInventory = inventory.lazyMap(d -> new RangedWrapper(d, 4, 5));

    public SoulConfiguratorBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.SOUL_CONFIGURATOR.get(), pPos, pBlockState);
    }

    protected IItemHandlerModifiable createInventory(){
        return new ItemStackHandler(5){

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (slot == 1){
                    return stack.is(Items.LAPIS_LAZULI);
                }else if (slot == 3){
                    return level != null && isSoulFuel(stack, level);
                }
                return super.isItemValid(slot, stack);
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulConfiguratorBE be){

    }


    public SoulConfiguratorRecipe getRecipe(){

    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    public static boolean isSoulFuel(ItemStack stack, Level level){
        var container = new SimpleContainer(stack);
        return level.getRecipeManager().getRecipeFor(RecipeInit.SOUL_CONFIGURATOR_FUEL_RECIPE.get(), container, level).isPresent();
    }
}
