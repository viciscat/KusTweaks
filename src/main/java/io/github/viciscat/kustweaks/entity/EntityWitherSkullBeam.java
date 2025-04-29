package io.github.viciscat.kustweaks.entity;

import io.github.viciscat.kustweaks.mixin.WorldInvoker;
import io.github.viciscat.kustweaks.sound.BeamSound;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityWitherSkullBeam extends Entity {

    @GameRegistry.ObjectHolder("kus_tweaks:beam_charge")
    private static SoundEvent beamChargeSound;


    private static final DataParameter<Integer> STATE = EntityDataManager.createKey(EntityWitherSkullBeam.class, DataSerializers.VARINT);

    private EntityWither shootingEntity;
    private int chargeStartTime;
    private int shootTime;
    private int end;

    private final List<ChunkPos> chunks = new ArrayList<>();

    @SuppressWarnings("unused")
    public EntityWitherSkullBeam(World worldIn) {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityWitherSkullBeam(World worldIn, EntityWither shooter, double accelX, double accelY, double accelZ) {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
        this.shootingEntity = shooter;
        chargeStartTime = rand.nextInt(20) + 40;
        shootTime = chargeStartTime + 20;
        end = shootTime + 40;

        motionX = accelX;
        motionY = accelY;
        motionZ = accelZ;

        double dx = motionX;
        double dy = motionY - this.getEyeHeight();
        double dz = motionZ;
        double d3 = MathHelper.sqrt(dx * dx + dz * dz);
        rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90);
        rotationPitch = MathHelper.wrapDegrees((float) (-MathHelper.atan2(dy, d3) * (180D / Math.PI)));
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(STATE, 0);
        noClip = true;
    }

    @Override
    public void setDead() {
        if (world.isRemote) for (int i = 0; i < 15; i++) {
            float x = (float) (posX + rand.nextFloat() * 0.2f - 0.1f);
            float y = (float) (posY + rand.nextFloat() * 0.2f - 0.1f);
            float z = (float) (posZ + rand.nextFloat() * 0.2f - 0.1f);
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0.0D, 0.2D, 0.0D);
        }
        super.setDead();
    }

    // https://playtechs.blogspot.com/2007/03/raytracing-on-grid.html
    private void calculateChunksToHit() {
        double x0 = posX;
        double y0 = posZ;
        Vec3d add = getPositionVector().add(getLookVec().normalize().scale(35));
        double x1 = add.x;
        double y1 = add.z;
        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);

        int x0Floor = MathHelper.floor(x0 / 16);
        int x = x0Floor;
        int y0Floor = MathHelper.floor(y0 / 16);
        int y = y0Floor;

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0)
        {
            x_inc = 0;
            error = Double.POSITIVE_INFINITY;
        }
        else if (x1 > x0)
        {
            x_inc = 1;
            n +=  MathHelper.floor(x1 / 16) - x;
            error = ( x0Floor + 1 - x0) * dy;
        }
        else
        {
            x_inc = -1;
            n += x -  MathHelper.floor(x1 / 16);
            error = (x0 - x0Floor) * dy;
        }

        if (dy == 0)
        {
            y_inc = 0;
            error -= Double.POSITIVE_INFINITY;
        }
        else if (y1 > y0)
        {
            y_inc = 1;
            n +=  MathHelper.floor(y1 / 16) - y;
            error -= ( y0Floor + 1 - y0) * dx;
        }
        else
        {
            y_inc = -1;
            n += y -  MathHelper.floor(y1 / 16);
            error -= (y0 - y0Floor) * dx;
        }

        for (; n > 0; --n) {
            chunks.add(new ChunkPos(x, y));

            if (error > 0) {
                y += y_inc;
                error -= dx;
            } else {
                x += x_inc;
                error += dy;
            }
        }
    }

    private List<Entity> getTargets() {
        List<Entity> targets = new ArrayList<>();
        AxisAlignedBB alignedBB = new AxisAlignedBB(getPositionVector(), getPositionVector().add(getLookVec().normalize().scale(35)));
        for (ChunkPos chunkPos : chunks) {
            if (!((WorldInvoker)world).invokeIsChunkLoaded(chunkPos.x, chunkPos.z, true)) continue;
            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            chunk.getEntitiesWithinAABBForEntity(this, alignedBB, targets, entity -> entity instanceof EntityLivingBase && !(entity instanceof EntityWitherSkeleton));
        }
        return targets;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        if (world.isRemote) return;


        if (ticksExisted >= chargeStartTime) {

            if (ticksExisted >= shootTime) {
                if (ticksExisted == shootTime) {
                    dataManager.set(STATE, 1);
                    calculateChunksToHit();
                }
                Vec3d positionVector = getPositionVector();
                Vec3d add = positionVector.add(getLookVec().normalize().scale(35));
                for (Entity target : getTargets()) {
                    if (target.getEntityBoundingBox().grow(0.15).calculateIntercept(positionVector, add) == null) continue;

                    DamageSource source = new EntityDamageSourceIndirect("wither", this, shootingEntity).setMagicDamage();

                    int i = target.hurtResistantTime;
                    target.hurtResistantTime = 0;
                    target.attackEntityFrom(source, 1);
                    target.hurtResistantTime = i;
                }

                if (ticksExisted >= end) setDead();
            } else {
                EntityLivingBase target = shootingEntity.getAttackTarget();
                if ((target == null || target.isDead)) {
                    setDead();
                    return;
                }
                if (ticksExisted == chargeStartTime) {
                    motionX = motionY = motionZ = 0;
                    playSound(beamChargeSound, 32.0F, 1.0F);
                }

                double dx = target.posX - this.posX;
                double dy = target.posY + target.height / 2d - this.posY - this.getEyeHeight();
                double dz = target.posZ - this.posZ;
                double d3 = MathHelper.sqrt(dx * dx + dz * dz);
                rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90);
                rotationPitch = MathHelper.wrapDegrees((float) (-MathHelper.atan2(dy, d3) * (180D / Math.PI)));
            }
        }
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (!world.isRemote) return;
        if (STATE.equals(key) && dataManager.get(STATE) == 1) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new BeamSound(this));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    @SideOnly(Side.CLIENT)
    public boolean notShootingLaser() {
        return dataManager.get(STATE) != 1;
    }
}
