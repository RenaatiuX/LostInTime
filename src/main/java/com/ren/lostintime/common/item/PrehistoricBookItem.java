package com.ren.lostintime.common.item;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrehistoricBookItem extends Item {

    public PrehistoricBookItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide) {
            openBookScreen();
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    private void openBookScreen() {
        Minecraft.getInstance().setScreen(new PrehistoricBookScreen());
    }
}
