package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.srpcotesia.init.SRPCAttributes;
import io.github.viciscat.kustweaks.KusAttributes;
import io.github.viciscat.kustweaks.mixin.tg.GenericProjectileAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.entities.projectiles.GenericProjectile;

@Mixin(SRPCAttributes.class)
public abstract class SRPCAttributesMixin {

    @Shadow(remap = false)
    public static void attachMinDamage(EntityLivingBase living, Entity proj) {
    }

    @Inject(method = "onProjectileCreation", at = @At("TAIL"), remap = false)
    private static void addTechgunsProjectile(EntityJoinWorldEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof GenericProjectile projectile) {
			EntityLivingBase shooter = ((GenericProjectileAccessor) projectile).getShooter();
            if (shooter != null) attachMinDamage(shooter, projectile);
        }
    }

    @WrapOperation(
            method = "dealMiniDamage(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)V", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getFloat(Ljava/lang/String;)F", ordinal = 0, remap = true)
    )
    private static float healVictim(NBTTagCompound instance, String key, Operation<Float> original, @Local(argsOnly = true) EntityLivingBase victim) {
        return (float) (original.call(instance, key) * (1 - KusAttributes.getAttributeOrDefault(victim, KusAttributes.TRUE_HEAL_PERCENT_DAMAGE)));
    }

    @WrapOperation(
            method = "dealMiniDamage(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;getAttributeValue()D", ordinal = 0)
    )
    private static double healVictim2(IAttributeInstance instance, Operation<Double> original, @Local(argsOnly = true, ordinal = 1) EntityLivingBase victim) {
        return original.call(instance) * (1 - KusAttributes.getAttributeOrDefault(victim, KusAttributes.TRUE_HEAL_PERCENT_DAMAGE));
    }


    @WrapOperation(method = "onMinDamageAttack", remap = false, at = @At(value = "CONSTANT", args = "classValue=net/minecraft/entity/projectile/EntityArrow", remap = true))
    private static boolean allowGenericProjectile(Object object, Operation<Boolean> original, @Local DamageSource source) {

        return original.call(object) || (object instanceof GenericProjectile && !source.getDamageType().equals("tg_knockback"));
    }
}
