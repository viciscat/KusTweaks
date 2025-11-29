package io.github.viciscat.kustweaks.mixin.tg.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GenericProjectileExplosive;
import techguns.entities.projectiles.GrenadeProjectile;
import techguns.packets.PacketSpawnParticle;

@Mixin({GrenadeProjectile.class, GenericProjectileExplosive.class})
public class CommonParticleMixin extends GenericProjectile {

    public CommonParticleMixin(World worldIn) {
        super(worldIn);
    }

    @WrapOperation(method = "explode", at = @At(value = "NEW", target = "(Ljava/lang/String;DDD)Ltechguns/packets/PacketSpawnParticle;"), remap = false)
    private PacketSpawnParticle increaseParticleSize(String name, double posX, double posY, double posZ, Operation<PacketSpawnParticle> original) {
        return new PacketSpawnParticle(name, posX, posY, posZ, (float) ExtendedGenericProjectile.of(this).kusTweaks$explosionSizeMult());
    }


}
