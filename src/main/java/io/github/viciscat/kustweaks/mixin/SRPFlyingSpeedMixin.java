package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.KusAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        targets = {
                "com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha$AIMoveControl",
                "com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo$AIMoveControl",
                "com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana$AIMoveControl",
                "com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted$AIMoveControl",
                "com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam$AIMoveControl",
                "com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol$AIMoveControl",
        },
        remap = false
)
public abstract class SRPFlyingSpeedMixin extends EntityMoveHelper {

    @Unique
    double kusTweaks$originalSpeed = speed;

    public SRPFlyingSpeedMixin(EntityLiving entitylivingIn) {
        super(entitylivingIn);
    }

    @Inject(method = "onUpdateMoveHelper", at = @At("HEAD"), remap = true)
    public void onUpdateMoveHelper(CallbackInfo ci) {
        speed = kusTweaks$originalSpeed * KusAttributes.getSpeedRatio(entity);
    }

    @Override
    public void setMoveTo(double x, double y, double z, double speedIn) {
        super.setMoveTo(x, y, z, speedIn);
        kusTweaks$originalSpeed = speed;
    }
}
