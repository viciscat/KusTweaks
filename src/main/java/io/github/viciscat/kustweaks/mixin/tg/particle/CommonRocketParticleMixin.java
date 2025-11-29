package io.github.viciscat.kustweaks.mixin.tg.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GuidedMissileProjectile;
import techguns.entities.projectiles.RocketProjectile;
import techguns.entities.projectiles.RocketProjectileNuke;
import techguns.packets.PacketSpawnParticle;

@Mixin({RocketProjectileNuke.class, GuidedMissileProjectile.class, RocketProjectile.class})
public class CommonRocketParticleMixin extends GenericProjectile {

    public CommonRocketParticleMixin(World worldIn) {
        super(worldIn);
    }

    @WrapOperation(method = "explodeRocket", at = @At(value = "NEW", target = "(Ljava/lang/String;DDD)Ltechguns/packets/PacketSpawnParticle;"), remap = false)
    private PacketSpawnParticle explodeRocket(String name, double posX, double posY, double posZ, Operation<PacketSpawnParticle> original) {
        return new PacketSpawnParticle(name, posX, posY, posZ, (float) ExtendedGenericProjectile.of(this).kusTweaks$explosionSizeMult());
    }
}
