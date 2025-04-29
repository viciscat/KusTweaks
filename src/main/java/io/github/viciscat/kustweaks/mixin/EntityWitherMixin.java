package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.MixinMethods;
import io.github.viciscat.kustweaks.entity.EntityWitherSkullBeam;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {

    @Shadow
    protected abstract double getHeadX(int head);
    @Shadow
    protected abstract double getHeadY(int head);
    @Shadow
    protected abstract double getHeadZ(int head);

    public EntityWitherMixin(World worldIn) {
        super(worldIn);
    }

    @Unique
    private int nextShot = 0;
    @Unique
    private int extraShots = -1;


    @Inject(method = "updateAITasks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntityMob;updateAITasks()V", shift = At.Shift.AFTER))
    public void updateAITasks(CallbackInfo ci) {
        if (ticksExisted >= nextShot) {
            EntityWither thiz = (EntityWither) (Object) this;
            if (extraShots < 0) {
                extraShots = MixinMethods.getExtraShots(thiz);
            }
            nextShot = ticksExisted + (--extraShots >= 0 ? 10 : rand.nextInt(40) + 160);
            float pitch = rotationPitch + rand.nextInt(60) - 30;
            float yaw = rotationYaw + rand.nextInt(90) - 45;

            Vec3d d = getVectorForRotation(pitch, yaw).normalize().scale(0.2);
            EntityWitherSkullBeam skullBeam = new EntityWitherSkullBeam(world, thiz, d.x, d.y, d.z);

            skullBeam.posX = getHeadX(0);
            skullBeam.posY = getHeadY(0);
            skullBeam.posZ = getHeadZ(0);

            world.spawnEntity(skullBeam);
        }
    }


}
