package io.github.viciscat.kustweaks.mixin.tg.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.TFGProjectile;
import techguns.packets.PacketSpawnParticle;

@Mixin(TFGProjectile.class)
public class TFGParticleMixin extends GenericProjectile {

    public TFGParticleMixin(World worldIn) {
        super(worldIn);
    }

    @WrapOperation(method = "explode", at = @At(value = "NEW", target = "(Ljava/lang/String;DDDF)Ltechguns/packets/PacketSpawnParticle;"), remap = false)
    private PacketSpawnParticle explodePacketSpawnParticle(String name, double posX, double posY, double posZ, float scale, Operation<PacketSpawnParticle> original) {
        return new PacketSpawnParticle(name, posX, posY, posZ, scale * (float) ExtendedGenericProjectile.of(this).kusTweaks$explosionSizeMult());

    }
}
