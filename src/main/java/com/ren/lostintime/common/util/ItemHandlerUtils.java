package com.ren.lostintime.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ItemHandlerUtils {


    /**
     * @param from      the item handler the items will be taken from
     * @param to        the item handler this will be copied to this must be bigger or equal size of the from handler
     * @param stackCopy this defines whether the stacks should be copied individually
     */
    public static void copy(IItemHandler from, IItemHandlerModifiable to, boolean stackCopy) {
        if (from.getSlots() > to.getSlots())
            throw new IllegalArgumentException("copiing from a larger itemhandler to a smaller one isnt supported with this method");
        for (int i = 0; i < from.getSlots(); i++) {
            if (stackCopy) {
                to.setStackInSlot(i, from.getStackInSlot(i).copy());
            } else
                to.setStackInSlot(i, from.getStackInSlot(i));
        }
    }

    public static Stream<ItemStack> stream(IItemHandler handler){
        return StreamSupport.stream(iterator(handler).spliterator(), false);
    }

    public static Iterable<ItemStack> iterator(IItemHandler handler){
        return () -> new Iterator<ItemStack>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < handler.getSlots();
            }

            @Override
            public ItemStack next() {
                if (!hasNext()) {
                    throw new NoSuchElementException(); // It's good practice to throw this exception
                }
                return handler.getStackInSlot(index++);
            }
        };
    }

    public static List<ItemStack> getAllMatching(IItemHandler inventory, TagKey<Item> stack, boolean deepCopy) {
        return getAllMatching(inventory, s -> s.is(stack), deepCopy);
    }

    public static List<ItemStack> getAllMatching(IItemHandler inventory, ItemLike stack, boolean deepCopy) {
        return getAllMatching(inventory, s -> s.is(stack.asItem()), deepCopy);
    }

    public static List<ItemStack> getAllMatching(IItemHandler inventory, Predicate<ItemStack> stackPredicate, boolean deepCopy) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack curren = inventory.getStackInSlot(i);
            if (stackPredicate.test(curren)) {
                stacks.add(deepCopy ? curren.copy() : curren);
            }
        }
        return stacks;
    }

    public static IItemHandlerModifiable deepCopy(IItemHandler handler) {
        ItemStackHandler another = new ItemStackHandler(handler.getSlots());
        for (int i = 0; i < handler.getSlots(); i++) {
            another.setStackInSlot(i, handler.getStackInSlot(i).copy());
        }
        return another;
    }

    public static void dropContents(Level level, BlockPos pos, IItemHandler inventory) {
        dropContents(level, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropContents(Level level, double x, double y, double z, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            Containers.dropItemStack(level, x, y, z, inventory.getStackInSlot(i));
        }
    }

    /**
     * if this item is present in the given inventory
     */
    public static boolean contains(IItemHandler handler, ItemLike item) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).is(item.asItem()))
                return true;
        }
        return false;
    }

    public static boolean containsOne(IItemHandler handler, Predicate<ItemStack> stackPrediacte) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (stackPrediacte.test(handler.getStackInSlot(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * @return whether all items in the given tag are present in this inventory
     */
    public static boolean containsAll(IItemHandler handler, TagKey<Item> item) {
        for (Item i : ForgeRegistries.ITEMS.tags().getTag(item)) {
            if (!contains(handler, i))
                return false;
        }
        return true;
    }

    /**
     * @return whether one item of the given tag is inside this inventory
     */
    public static boolean containsOne(IItemHandler handler, TagKey<Item> item) {
       return containsOne(handler, s -> s.is(item));
    }


    /**
     * this will clear the given handler so no item remain inside, depends a little on {@link IItemHandler#extractItem(int, int, boolean)} if this cant extract all items then this wont work
     *
     * @param handler
     */
    public static void clear(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.extractItem(i, Integer.MAX_VALUE, false);
        }
    }

    public static boolean empty(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    /**
     * @param dest     the inventory we want to extract from
     * @param template the template stack, all items have to be able to stack with
     * @param simulate whether we just want to simulate this
     * @return the extracted stack, this stacks with the template and has a max count of template, if its empty then we didnt find any matching stacks
     */
    @NotNull
    public static ItemStack extractItem(IItemHandler dest, @NotNull ItemStack template, boolean simulate) {
        if (dest == null || template.isEmpty())
            return ItemStack.EMPTY;
        ItemStack actualExtraction = template.copyWithCount(0);
        for (int i = 0; i < dest.getSlots(); i++) {
            ItemStack current = dest.extractItem(i, template.getCount() - actualExtraction.getCount(), true);//just look if we actually can extract something
            if (current.isEmpty()) {//nothing needs to be done as we didnt extract anything
                continue;
            }
            if (ItemHandlerHelper.canItemStacksStackRelaxed(current, template)) {
                int prevCount = actualExtraction.getCount();
                actualExtraction.grow(current.getCount());
                if (!simulate)
                    dest.extractItem(i, template.getCount() - prevCount, false);
            }
            if (actualExtraction.getCount() == template.getCount()) {
                break;
            }

        }

        return actualExtraction;
    }

    public static Container wrap(IItemHandler handler, boolean deepCopy) {
        SimpleContainer inv = new SimpleContainer(handler.getSlots());
        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, deepCopy ? handler.getStackInSlot(i).copy() : handler.getStackInSlot(i));
        }
        return inv;
    }

    @NotNull
    public static ItemStack insertItem(IItemHandler dest, @NotNull ItemStack stack, boolean simulate, Consumer<Integer> onAdd) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++) {
            ItemStack prev = stack.copy();
            stack = dest.insertItem(i, stack, simulate);
            if (stack.getCount() != prev.getCount()) {
                onAdd.accept(i);//we added something
            }
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    @NotNull
    public static ItemStack insertItemStacked(IItemHandler inventory, @NotNull ItemStack stack, boolean simulate, Consumer<Integer> onAdd) {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate, onAdd);
        }

        int sizeInventory = inventory.getSlots();

        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (ItemHandlerHelper.canItemStacksStackRelaxed(slot, stack)) {
                ItemStack prev = stack.copy();
                stack = inventory.insertItem(i, stack, simulate);
                if (prev.getCount() != stack.getCount()) {
                    onAdd.accept(i);
                }
                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        // insert remainder into empty slots
        if (!stack.isEmpty()) {
            // find empty slot
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    ItemStack prev = stack.copy();
                    stack = inventory.insertItem(i, stack, simulate);
                    if (prev.getCount() != stack.getCount()) {
                        onAdd.accept(i);
                    }
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static String getArrayRepresentation(IItemHandler handler) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < handler.getSlots(); i++) {
            builder.append(handler.getStackInSlot(i));
            if (i + 1 < handler.getSlots()) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
