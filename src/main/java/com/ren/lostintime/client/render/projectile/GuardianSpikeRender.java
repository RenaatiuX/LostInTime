package com.ren.lostintime.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ren.lostintime.common.entity.projectile.GuardianSpike;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GuardianSpikeRender extends EntityRenderer<GuardianSpike> {

    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public GuardianSpikeRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(GuardianSpike pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        if (!pEntity.isGrounded()) {
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getYRot()));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(pEntity.getXRot()));
        } else {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        }

        ItemStack stack = pEntity.getPickupItem();
        BakedModel bakedModel = this.itemRenderer.getModel(stack, null, null, 0);
        itemRenderer.render(stack, ItemDisplayContext.GROUND, false, pPoseStack, pBuffer, pPackedLight,
                OverlayTexture.NO_OVERLAY, bakedModel);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GuardianSpike pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
