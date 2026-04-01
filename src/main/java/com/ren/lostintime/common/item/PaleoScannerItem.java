package com.ren.lostintime.common.item;

import com.ren.lostintime.common.entity.LITAnimal;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PaleoScannerItem extends Item {

    public PaleoScannerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND && !pPlayer.level().isClientSide) {
            if (pInteractionTarget instanceof LITAnimal target) {
                float currentHunger = target.getHunger();
                float maxHunger = target.getMaxHunger();

                int hungerPercent = (int) ((currentHunger / maxHunger) * 100.0F);

                String name = target.getName().getString();
                String gender = target.getGenderName();
                String growthName = target.getGrowthStageName();

                String info = "§6[" + name + "] §fStage: §b" + growthName +
                        " §7| §fHunger: §e" + (int)currentHunger + "/" + (int)maxHunger +
                        " §b(" + hungerPercent + "%)";

                int ticksToGrow = target.getTicksUntilNextStage();
                if (ticksToGrow > 0) {
                    info += " §7| §fGrows in: §a" + (ticksToGrow / 20) + "s";
                } else {
                    if (!target.isMale()) {
                        if (target.isPregnant()) {
                            info += " §7| §fGestation: §a" + (target.getGestationTicks() / 20) + "s";
                        } else {
                            info += " §7| §fFertile: §aSí";
                        }
                    } else {
                        if (target.getAge() > 0) {
                            info += " §7| §fRest: §c" + (target.getAge() / 20) + "s";
                        } else {
                            info += " §7| §fMating: §aReady";
                        }
                    }
                }

                pPlayer.displayClientMessage(Component.literal(info), true);

                pPlayer.level().playSound(null, pPlayer.blockPosition(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.5F);

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
