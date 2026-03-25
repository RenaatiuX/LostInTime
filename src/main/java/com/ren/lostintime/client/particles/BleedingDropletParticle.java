package com.ren.lostintime.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BleedingDropletParticle extends LITBaseParticle {

    private final float fixedRotation;

    public BleedingDropletParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ, pSpriteSet);
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;

        this.quadSize = 0.2F + this.random.nextFloat() * 0.15F;

        this.lifetime = 60 + this.random.nextInt(40);

        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.fixedRotation = this.random.nextFloat() * ((float)Math.PI * 2.0F);
        this.setSpriteFromAge(pSpriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0F - ((float)this.age / (float)this.lifetime);
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

        Quaternionf quaternionf = new Quaternionf().rotationX((float)Math.PI / 2.0F);

        quaternionf.rotateZ(this.fixedRotation);

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

        public Provider(SpriteSet pSprites) {
            this.spriteSet = pSprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new BleedingDropletParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.spriteSet);
        }
    }
}
