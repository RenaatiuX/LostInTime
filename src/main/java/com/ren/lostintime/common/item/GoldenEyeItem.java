package com.ren.lostintime.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class GoldenEyeItem extends Item implements ICurioItem {

    public GoldenEyeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        if (!player.level().isClientSide) {
            MobEffectInstance regenerationEffect = player.getEffect(MobEffects.REGENERATION);

            if (regenerationEffect == null) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0,
                        true, false));
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        if (!player.level().isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0,
                    true, false));
        }
    }
}
