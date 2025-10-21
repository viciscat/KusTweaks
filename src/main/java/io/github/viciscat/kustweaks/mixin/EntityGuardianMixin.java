package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityGuardian.class)
public class EntityGuardianMixin {

    @ModifyArg(
            method = "initEntityAI",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAINearestAttackableTarget;<init>(Lnet/minecraft/entity/EntityCreature;Ljava/lang/Class;IZZLcom/google/common/base/Predicate;)V"),
            index = 3)
    private boolean seeThroughWalls(boolean checkSight) {

        boolean b = !(((EntityGuardian) (Object) this) instanceof EntityElderGuardian) && checkSight;
        return b;
    }

    @Mixin(targets = "net.minecraft.entity.monster.EntityGuardian$AIGuardianAttack")
    public static class AttackAIMixin {

        @WrapOperation(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntityGuardian;canEntityBeSeen(Lnet/minecraft/entity/Entity;)Z"))
        private boolean canEntityBeSeen(EntityGuardian instance, Entity entity, Operation<Boolean> original) {
            return instance instanceof EntityElderGuardian || original.call(instance, entity);
        }
    }

}
