package io.github.viciscat.kustweaks.entity.render;

import io.github.viciscat.kustweaks.KusTweaksMod;
import io.github.viciscat.kustweaks.entity.EntityWitherSkullBeam;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RenderWitherSkullBeam extends Render<EntityWitherSkullBeam> {
    private static final ResourceLocation WITHER_TEXTURES = new ResourceLocation(KusTweaksMod.MOD_ID, "textures/entity/skull_beam.png");
    /** The Skeleton's head model. */
    private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead(0, 0, 64, 32);

    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation(KusTweaksMod.MOD_ID,"textures/entity/wither_beam.png");

    public RenderWitherSkullBeam(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    private float interpolateRotation(float prevRot, float rot, float partialTicks)
    {
        float f;

        for (f = rot - prevRot; f < -180.0F; f += 360.0F);


        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return prevRot + partialTicks * f;
    }

    public boolean shouldRender(EntityWitherSkullBeam skull, ICamera camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(skull, camera, camX, camY, camZ)) return true;
        if (skull.notShootingLaser()) return false;

        Vec3d skullPos = this.getPosition(skull, skull.getEyeHeight(), 1.0F);
        Vec3d targetPos = skullPos.add(skull.getLookVec().scale(35));

        return camera.isBoundingBoxInFrustum(new AxisAlignedBB(targetPos.x, targetPos.y, targetPos.z, skullPos.x, skullPos.y, skullPos.z));

    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityWitherSkullBeam skull, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        float yaw = this.interpolateRotation(skull.prevRotationYaw, skull.rotationYaw, partialTicks);
        float pitch =  this.interpolateRotation(skull.prevRotationPitch, skull.rotationPitch, partialTicks);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        this.bindEntityTexture(skull);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(skull));
        }

        this.skeletonHeadModel.render(skull, 0.0F, 0.0F, 0.0F, MathHelper.wrapDegrees(yaw + 180), pitch, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();


        if (skull.notShootingLaser()) return;
        //super.doRender(entity, x, y, z, entityYaw, partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        this.bindTexture(BEAM_TEXTURE);
        //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        double distance = 35;
        //float pitch = (float) Math.toDegrees(Math.acos(diff.y));
        //float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x));
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();
        float brightness = 1.0F;
        float halfWidth = 0.3f;

        int UVscale = 2;
        int numFrames = 17;
        float frametime = 0.5F;
        double u = distance / (halfWidth * UVscale * 8.0);
        int frame = (int)(((float)skull.ticksExisted + partialTicks) * frametime % numFrames);
        double v1 = 1.0d / numFrames * frame;
        double v2 = 1.0d / numFrames * (frame + 1);



        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y + skull.getEyeHeight(), (float)z);
        GlStateManager.rotate(90-yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, 1.f);
            builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            builder.pos(-distance, -halfWidth, 0).tex(i, v1).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            builder.pos(0.0F, -halfWidth, 0).tex(u + i, v1).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            builder.pos(0.0F, halfWidth, 0).tex(u + i, v2).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            builder.pos(-distance, halfWidth, 0).tex(i, v2).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            tessellator.draw();
        }

        GlStateManager.popMatrix();
    }

    private Vec3d getPosition(Entity entityLivingBaseIn, double yOffset, float partialTicks)
    {
        double d0 = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * partialTicks;
        double d1 = yOffset + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * partialTicks;
        double d2 = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * partialTicks;
        return new Vec3d(d0, d1, d2);
    }


    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityWitherSkullBeam entity)
    {
        return WITHER_TEXTURES;
    }
}
