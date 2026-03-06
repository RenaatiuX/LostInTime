package com.ren.lostintime.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ren.lostintime.common.entity.projectile.ThrownKnife;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ThrownKnifeRenderer extends EntityRenderer<ThrownKnife> {

    private final ItemRenderer itemRenderer;

    public ThrownKnifeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(ThrownKnife pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.scale(1.0F, 1.0F, 1.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot())));

        if (!pEntity.isKnifeInGround()) {
            float spin = pEntity.tickCount + pPartialTick;
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(spin * 50.0F));
        } else {
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        }
        ItemStack knifeStack = pEntity.getKnifeItem();
        this.itemRenderer.renderStatic(knifeStack, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownKnife pEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
