package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.viciscat.kustweaks.KusAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.Explosion;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Nullable public abstract EntityLivingBase getExplosivePlacedBy();

    @Shadow @Final
    private Entity exploder;

    @ModifyExpressionValue(method = "doExplosionA", at = @At(value = "FIELD", target = "Lnet/minecraft/world/Explosion;size:F", opcode = Opcodes.GETFIELD))
    private float thing(float original) {
        EntityLivingBase explosivePlacedBy = getExplosivePlacedBy();
        if (explosivePlacedBy != null) {
            return original * (float) KusAttributes.getAttributeOrDefault(explosivePlacedBy, KusAttributes.EXPLOSION_SIZE_MULTIPLIER);
        } else if (exploder instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) exploder;
            if (arrow.shootingEntity instanceof EntityLivingBase) {
                return original * (float) KusAttributes.getAttributeOrDefault((EntityLivingBase) arrow.shootingEntity, KusAttributes.EXPLOSION_SIZE_MULTIPLIER);
            }
        }
        return original;
    }
}
