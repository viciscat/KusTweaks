package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityShulkerBullet.class)
public class EntityShulkerBulletMixin {

    @WrapOperation(method = "bulletHit", at = @At(value = "NEW", target = "(Lnet/minecraft/potion/Potion;I)Lnet/minecraft/potion/PotionEffect;"))
    public PotionEffect bulletHit(Potion potionIn, int durationIn, Operation<PotionEffect> original) {
        return new PotionEffect(potionIn, durationIn, 21);
    }
}
