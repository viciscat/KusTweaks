package io.github.viciscat.kustweaks.mixin;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.srpcotesia.handler.EnhancedMobHandler;
import com.srpcotesia.init.SRPCAttributes;
import com.srpcotesia.util.ParasiteInteractions;
import com.srpcotesia.util.XPManager;
import io.github.viciscat.kustweaks.KusAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import techguns.entities.projectiles.GenericProjectile;

@Mixin(EntityPMalleable.class)
public abstract class EntityPMalleableMixin extends EntityParasiteBase {
    @Shadow(remap = false) @Final private static DataParameter<Byte> HIT;

    public EntityPMalleableMixin(World worldIn) {
        super(worldIn);
    }

    // again thanks to cotesia for allowing me to use this
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void magicPercentDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        EntityPMalleable victim = (EntityPMalleable) (Object)this;
        if (victim.world.isRemote) return;
        Entity e = source.getTrueSource();

        if (!(e instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) e;

        if (source.getImmediateSource() instanceof GenericProjectile) {
            if (living instanceof EntityPlayer) {
                XPManager.setAttackingPlayer(this, (EntityPlayer)living);
            }

            SRPCAttributes.dealMiniDamage(source.getImmediateSource(), this, source, amount);
        } else if (source.isMagicDamage()) {



            if (ParasiteInteractions.isParasite(living)) return;
            dataManager.set(HIT, (byte) 0);


            float pd = (float) KusAttributes.getAttributeOrDefault(living, KusAttributes.MAGIC_PERCENT_DAMAGE);
            float minDamage = amount * pd;
            if (minDamage < 1.0E-7) return;
            minDamage = EnhancedMobHandler.blockMiniDamage(living, victim, minDamage);
            if (minDamage < 1.0E-7) return;
            SRPCAttributes.trueDamage(living, victim, source, minDamage);
        }
    }
}
