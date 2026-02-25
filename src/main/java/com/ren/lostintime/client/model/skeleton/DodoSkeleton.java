package com.ren.lostintime.client.model.skeleton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ren.lostintime.common.entity.skeleton.FossilEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DodoSkeleton <T extends FossilEntity> extends EntityModel<T> {

    private final ModelPart dodo;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart neck2;
    private final ModelPart jaw2;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart left_leg2;
    private final ModelPart left_foot;
    private final ModelPart right_leg;
    private final ModelPart right_leg2;
    private final ModelPart right_foot;

    public DodoSkeleton(ModelPart root) {
        this.dodo = root.getChild("dodo");
        this.body = this.dodo.getChild("body");
        this.tail = this.body.getChild("tail");
        this.neck2 = this.body.getChild("neck2");
        this.jaw2 = this.neck2.getChild("jaw2");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.left_leg = this.dodo.getChild("left_leg");
        this.left_leg2 = this.left_leg.getChild("left_leg2");
        this.left_foot = this.left_leg2.getChild("left_foot");
        this.right_leg = this.dodo.getChild("right_leg");
        this.right_leg2 = this.right_leg.getChild("right_leg2");
        this.right_foot = this.right_leg2.getChild("right_foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition dodo = partdefinition.addOrReplaceChild("dodo", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 5.5F));

        PartDefinition body = dodo.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.0F, -11.5F, 10.0F, 10.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 23).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 1.5F, 0.2618F, 0.0F, 0.0F));

        PartDefinition neck2 = body.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(0, 23).addBox(-4.0F, -5.0F, -6.0F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(26, 35).addBox(-2.0F, -3.0F, -13.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(44, 40).addBox(-2.0F, 0.0F, -13.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -8.5F, 0.2182F, 0.0F, 0.0F));

        PartDefinition jaw2 = neck2.addOrReplaceChild("jaw2", CubeListBuilder.create().texOffs(0, 36).addBox(-1.0F, 0.0F, -6.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, -6.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 36).addBox(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, -9.5F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 36).mirror().addBox(-1.0F, 0.0F, 0.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, 0.0F, -9.5F));

        PartDefinition left_leg = dodo.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 55).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -3.0F, -2.5F));

        PartDefinition left_leg2 = left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(2, 55).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition left_foot = left_leg2.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(0, 43).addBox(-1.5F, -0.05F, -3.0F, 3.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition right_leg = dodo.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 55).mirror().addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.5F, -3.0F, -2.5F));

        PartDefinition right_leg2 = right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(2, 55).mirror().addBox(-0.5F, 0.0F, 0.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition right_foot = right_leg2.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(-1.5F, -0.05F, -3.0F, 3.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 7.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        dodo.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
