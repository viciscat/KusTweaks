package io.github.viciscat.kustweaks.mixin.tg;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import techguns.entities.projectiles.GenericProjectile;

@Mixin(GenericProjectile.class)
public interface GenericProjectileAccessor {

    @Accessor(value = "shooter", remap = false)
    EntityLivingBase getShooter();
}
