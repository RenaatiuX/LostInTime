package com.ren.lostintime.common.item;

import com.ren.lostintime.common.entity.creatures.Hylonomus;
import com.ren.lostintime.common.entity.enums.HylonomusVariant;
import com.ren.lostintime.common.entity.projectile.LITThrownEgg;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class LITEggItem extends Item {

    private final Supplier<? extends EntityType<?>> entityType;

    public LITEggItem(Properties pProperties, Supplier<? extends EntityType<?>> entityType) {
        super(pProperties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!pLevel.isClientSide) {
            LITThrownEgg thrownEgg = new LITThrownEgg(pLevel, pPlayer, entityType.get(), itemstack);
            thrownEgg.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
            pLevel.addFreshEntity(thrownEgg);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    public EntityType<?> getEntityType() {
        return entityType.get();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (pStack.hasTag() && pStack.getTag().contains("Variant")) {
            if (this.entityType.get() == EntityInit.HYLONOMUS.get()) {
                int variantId = pStack.getTag().getInt("Variant");
                HylonomusVariant variant = HylonomusVariant.byId(variantId);

                pTooltipComponents.add(Component.literal("Variant: " + variant.name()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
