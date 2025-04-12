package io.github.viciscat.kustweaks.mixin;

import arekkuusu.enderskills.api.event.SkillDamageSource;
import arekkuusu.enderskills.api.registry.Skill;
import arekkuusu.enderskills.common.skill.effect.BlackFlame;
import com.llamalad7.mixinextras.sugar.Local;
import com.srpcotesia.init.SRPCAttributes;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlackFlame.class, remap = false)
public class BlackFlameMixin {
    @Inject(
            method = "dealTrueDamageHAHAHA", remap = false,
            at = @At(value = "INVOKE", target = "Larekkuusu/enderskills/api/event/SkillDamageEvent;<init>(Lnet/minecraft/entity/EntityLivingBase;Larekkuusu/enderskills/api/registry/Skill;Lnet/minecraft/util/DamageSource;D)V"),
            cancellable = true
    )
    private static void dealTrueDamageHAHAHA(Skill skill, EntityLivingBase entity, EntityLivingBase owner, double damage, CallbackInfo ci, @Local SkillDamageSource skillDamageSource) {
        if (damage > 0.0D) {
            SRPCAttributes.trueDamage(owner, entity, skillDamageSource, (float)(damage * entity.getMaxHealth()));
            ci.cancel();
        }
    }
    @Inject(
            method = "dealTrueDamage(Larekkuusu/enderskills/api/registry/Skill;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/EntityLivingBase;DI)V", remap = false,
            at = @At(value = "INVOKE", target = "Larekkuusu/enderskills/api/event/SkillDamageEvent;<init>(Lnet/minecraft/entity/EntityLivingBase;Larekkuusu/enderskills/api/registry/Skill;Lnet/minecraft/util/DamageSource;D)V"),
            cancellable = true
    )
    private static void dealTrueDamage2(Skill skill, EntityLivingBase entity, EntityLivingBase owner, double damage, int time, CallbackInfo ci, @Local SkillDamageSource skillDamageSource) {
        if (damage > 0.0D) {
            SRPCAttributes.trueDamage(owner, entity, skillDamageSource, (float)(damage * entity.getMaxHealth()) / time);
            ci.cancel();
        }
    }
}
