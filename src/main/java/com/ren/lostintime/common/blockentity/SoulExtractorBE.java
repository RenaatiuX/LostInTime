package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.init.RecipeInit;
import com.ren.lostintime.common.menu.SoulExtractorMenu;
import com.ren.lostintime.common.recipe.SoulExtractorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulExtractorBE extends BlockEntity implements MenuProvider {

    public static final int MAX_RESIDUE = 27;

    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 2;
    public static final int SLOT_SOUL_SOURCE = 3;
    public static final int SLOT_CATALYST = 4;
    public static final int SLOT_RESIDUE_START = 5;
    public static final int SLOT_RESIDUE_END = 7;
    public static final int SLOT_OUTPUT = 8;

    //60 seconds in ticks
    public static final int RESIDUE_REDUCTION_TIME = 60 * 20;

    //20 seconds in ticks
    private static final int BASE_PROCESS_TIME = 20 * 20;


    private final LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> new ItemStackHandler(9));
    private final LazyOptional<IItemHandlerModifiable> input = LazyOptional.of(() -> new RangedWrapper(itemHandler.orElseThrow(IllegalStateException::new), SLOT_INPUT_START, SLOT_INPUT_END + 1));
    private final LazyOptional<IItemHandlerModifiable> residue = LazyOptional.of(() -> new RangedWrapper(itemHandler.orElseThrow(IllegalStateException::new), SLOT_RESIDUE_START, SLOT_RESIDUE_END + 1));
    private final LazyOptional<IItemHandlerModifiable> output = LazyOptional.of(() -> new RangedWrapper(itemHandler.orElseThrow(IllegalStateException::new), SLOT_OUTPUT, SLOT_OUTPUT + 1));

    protected int processTime;
    protected int processCounter;

    protected int currentResidue = 0;
    protected int residueReductionCounter = 0;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> Math.round((processCounter * 100f) / (float) processTime);
                case 1 -> currentResidue;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            //we dont need to do anything here actually
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public SoulExtractorBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.SOUL_EXTRACTOR.get(), pPos, pBlockState);
        this.processTime = BASE_PROCESS_TIME;
    }

    protected List<ItemStack> getCurrentPossibleWasteItems() {
        List<ItemStack> list = new ArrayList<>(3);
        if (currentResidue <= 9) {
            list.add(new ItemStack(ItemInit.SOUL_ASH.get()));
            list.add(new ItemStack(ItemInit.SOUL_GRUME.get()));
            return list;
        } else if (currentResidue <= 18) {
            list.add(new ItemStack(ItemInit.SOUL_ASH.get()));
            list.add(new ItemStack(ItemInit.SOUL_GRUME.get()));
            list.add(new ItemStack(ItemInit.ECTOPLASM.get()));
        }
        list.add(new ItemStack(ItemInit.SOUL_GRUME.get()));
        list.add(new ItemStack(ItemInit.ECTOPLASM.get()));
        return list;
    }

    protected List<ItemStack> calculateWaste() {
        List<ItemStack> list = new ArrayList<>(3);
        assert level != null;
        if (currentResidue <= 9) {
            float soulAshChance = Mth.map(currentResidue, 0f, 9f, 0.1f, 0.25f);
            if (level.random.nextFloat() < soulAshChance){
                list.add(new ItemStack(ItemInit.SOUL_ASH.get()));
            }
            float soulGrumeChance = Mth.map(currentResidue, 0f, 9f, 0.025f, 0.05f);
            if (level.random.nextFloat() < soulGrumeChance){
                list.add(new ItemStack(ItemInit.SOUL_GRUME.get()));
            }
        }else if (currentResidue <= 18){
            float soulAshChance = Mth.map(currentResidue, 10f, 18f, 0.25f, 0.05f);

            if (level.random.nextFloat() < soulAshChance){
                list.add(new ItemStack(ItemInit.SOUL_ASH.get()));
            }
            float soulGrumeChance = Mth.map(currentResidue, 10f, 18f, 0.05f, 0.25f);
            if (level.random.nextFloat() < soulGrumeChance){
                list.add(new ItemStack(ItemInit.SOUL_GRUME.get()));
            }

            float ectoplasmChance = Mth.map(currentResidue, 10f, 18f, 0.01f, 0.02f);
            if (level.random.nextFloat() < ectoplasmChance){
                list.add(new ItemStack(ItemInit.ECTOPLASM.get()));
            }
        }else {
            float soulGrumeChance = Mth.map(currentResidue, 19f, 27f, 0.25f, 0.1f);
            if (level.random.nextFloat() < soulGrumeChance){
                list.add(new ItemStack(ItemInit.SOUL_GRUME.get()));
            }
            float ectoplasmChance = Mth.map(currentResidue, 10f, 27f, 0.02f, 0.05f);
            if (level.random.nextFloat() < ectoplasmChance){
                list.add(new ItemStack(ItemInit.ECTOPLASM.get()));
            }
        }
        return list;

    }


    //basically check for outputs etc
    protected boolean canProcess(SoulExtractorRecipe recipe) {
        if (level == null) return false;

        ItemStack output = recipe.getResultItem(level.registryAccess());
        if (!ItemHandlerHelper.insertItemStacked(this.output.orElseThrow(IllegalStateException::new), output, true).isEmpty())
            return false;

        var wasteList = getCurrentPossibleWasteItems();
        for (var item : wasteList) {
            //when those items cant be added to the residue, even if it may be ok because of chances, this will block it never the less
            if (!ItemHandlerHelper.insertItemStacked(residue.orElseThrow(IllegalAccessError::new), item, true).isEmpty())
                return false;
        }
        return true;
    }

    protected void finishProcessing(SoulExtractorRecipe recipe) {
        if (level == null) return;

        if (level.random.nextFloat() < recipe.getChance()) {
            increaseResidue(recipe.getResidueOnSuccess());
            ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
            ItemHandlerHelper.insertItemStacked(this.output.orElseThrow(IllegalStateException::new), result, false);
        }
        getInventory().getStackInSlot(SLOT_SOUL_SOURCE).shrink(1);
        getInventory().getStackInSlot(SLOT_CATALYST).shrink(1);

        var outputInventory = output.orElseThrow(IllegalStateException::new);

        for (var ingredient : recipe.getInputs()) {
            for (int i = 0; i < outputInventory.getSlots(); i++) {
                var stack = outputInventory.getStackInSlot(i);
                if (ingredient.test(stack)){
                    stack.shrink(1);
                    break;
                }
            }
        }


        residue.ifPresent(d -> {
            var waste = calculateWaste();
            for (var stack : waste){
                ItemHandlerHelper.insertItem(d, stack, false);
            }
        });


        setChanged();
    }

    protected void reset() {
        processCounter = 0;
        processTime = Math.round(20f + (currentResidue / 27f) * 40f);
    }

    protected void startProcessing(SoulExtractorRecipe recipe) {
        increaseResidue();
    }

    protected void increaseResidue() {
        increaseResidue(1);
    }

    protected void increaseResidue(int amount) {
        currentResidue = Mth.clamp(currentResidue + amount, 0, MAX_RESIDUE);
    }

    protected void decreaseResidue(int amount) {
        increaseResidue(-amount);
    }

    protected void decreaseResidue() {
        decreaseResidue(1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulExtractorBE be) {
        var recipe = be.getRecipe();
        if (recipe != null && be.canProcess(recipe)) {
            be.residueReductionCounter = 0;
            if (be.processCounter == 0) {
                be.startProcessing(recipe);
            }
            if (be.processCounter >= be.processTime) {
                be.finishProcessing(recipe);
                be.reset();
            } else
                be.processCounter++;

        } else {
            be.reset();
            if (be.residueReductionCounter < RESIDUE_REDUCTION_TIME){
                be.residueReductionCounter++;
            }else {
                be.decreaseResidue();
                be.residueReductionCounter = 0;
            }
        }
    }

    public @Nullable SoulExtractorRecipe getRecipe() {
        return level.getRecipeManager().getRecipeFor(RecipeInit.SOUL_EXTRACTOR_RECIPE.get(), createContainer(), level).orElse(null);
    }

    public SoulExtractorRecipeContainer createContainer(){
        return new SoulExtractorRecipeContainer(input.orElseThrow(IllegalStateException::new),
                getInventory().getStackInSlot(SLOT_SOUL_SOURCE), getInventory().getStackInSlot(SLOT_CATALYST));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.lostintime.soul_extractor");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player player) {
        return new SoulExtractorMenu(pContainerId, pInventory, itemHandler.orElse(new ItemStackHandler(9)), data);
    }

    public IItemHandlerModifiable getInventory() {
        return itemHandler.orElseThrow(IllegalStateException::new);
    }
}
