package io.github.viciscat.kustweaks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class SplashTrait extends ModifierTrait {
    private static final float DISTANCE = 3;
    private static final float DISTANCE_SQ = DISTANCE * DISTANCE;
    private static final float DAMAGE_RATIO = .35f;

    public SplashTrait() {
        super("kus_splash", 0xFFAA00AA);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        super.afterHit(tool, player, target, damageDealt, wasCritical, wasHit);
        target.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                new AxisAlignedBB(target.getPositionVector(), target.getPositionVector()).grow(DISTANCE),
                e -> {
                    if (e == player || e == target) return false;
                    return e.getDistanceSq(target) < DISTANCE_SQ;
                }
        ).forEach(entity ->
                entity.attackEntityFrom(
                        player instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer) player) : DamageSource.causeMobDamage(player),
                        damageDealt * DAMAGE_RATIO
                )
        );
    }
}
