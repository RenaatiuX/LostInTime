package com.ren.lostintime.common.item;

import com.ren.lostintime.common.entity.skeleton.FossilEntity;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SkeletonPlacerItem extends Item {

    private final String skeletonType;

    public SkeletonPlacerItem(Properties properties, String skeletonType) {
        super(properties);
        this.skeletonType = skeletonType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!level.isClientSide) {
            FossilEntity entity = EntityInit.LIT_SKELETON.get().create(level);

            if (entity != null) {
                BlockPos clickPos = context.getClickedPos();
                BlockPos spawnPos = clickPos.relative(context.getClickedFace());

                float yaw = 0.0F;
                if (context.getPlayer() != null) {
                    yaw = context.getPlayer().getYRot();
                    yaw += 180.0F;
                    yaw = (float)(Math.round(yaw / 45.0F) * 45);
                }

                entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, yaw, 0.0F);

                entity.setYBodyRot(yaw);
                entity.setYHeadRot(yaw);

                entity.setSkeletonType(this.skeletonType);

                level.addFreshEntity(entity);

                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
