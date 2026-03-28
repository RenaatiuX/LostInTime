package com.ren.lostintime.client.particles;

import com.mojang.blaze3d.vertex.*;
import com.ren.lostintime.common.init.ParticlesInit;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BleedingStreamParticle extends LITBaseParticle {

    public BleedingStreamParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ, pSpriteSet);

        this.xd = 0.0D;
        this.yd = -0.1D;
        this.zd = 0.0D;

        this.quadSize = 0.15F + this.random.nextFloat() * 0.1F;

        this.lifetime = 15 + this.random.nextInt(10);

        this.gravity = 0.6F;
        this.hasPhysics = true;

        this.setSpriteFromAge(pSpriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0F - ((float) this.age / (float) this.lifetime);

        BlockPos currentPos = BlockPos.containing(this.x, this.y, this.z);
        if (this.level.getFluidState(currentPos).is(FluidTags.WATER)) {
            this.level.addParticle(ParticlesInit.BLEEDING_UNDERWATER.get(), this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            this.remove();
            return;
        }

        if (this.onGround) {
            double microscopicoYOffset = 0.001D + (this.random.nextDouble() * 0.004D);
            this.level.addParticle(ParticlesInit.BLEEDING_DROPLET.get(), this.x, this.y + microscopicoYOffset, this.z, 0.0D, 0.0D, 0.0D);
            this.remove();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 cameraPos = pRenderInfo.getPosition();
        float x = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - cameraPos.x());
        float y = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - cameraPos.y());
        float z = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - cameraPos.z());

        Quaternionf quaternionf = new Quaternionf().rotationYXZ(
                -pRenderInfo.getYRot() * ((float)Math.PI / 180F),
                0.0F,
                0.0F
        );

        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float size = this.getQuadSize(pPartialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);

            vector3f.mul(size);
            vector3f.add(x, y, z);
        }

        float minU = this.getU0();
        float maxU = this.getU1();
        float minV = this.getV0();
        float maxV = this.getV1();
        int light = this.getLightColor(pPartialTicks);

        pBuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet pSpriteSet) {
            this.spriteSet = pSpriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new BleedingStreamParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.spriteSet);
        }
    }
}
