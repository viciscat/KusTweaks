package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.entity.EntityTurretHeadHitbox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.RayTraceResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {

    @Shadow
    public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @ModifyExpressionValue(method = "travel", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;jumpMovementFactor:F", opcode = Opcodes.GETFIELD))
    private float travel(float original) {
        IAttributeInstance instance = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (instance == null)
            return original;
        return original * (float) (instance.getAttributeValue() / instance.getBaseValue());
    }

	@ModifyExpressionValue(method = "canEntityBeSeen", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;"))
	private RayTraceResult seeingTurretHitboxFix(RayTraceResult original, @Local(argsOnly = true) Entity entity) {
		if (original != null && entity instanceof EntityTurretHeadHitbox hitbox && original.getBlockPos().equals(hitbox.getPosition())) return null;
		return original;
	}
}
