package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.viciscat.kustweaks.GunUpgrade;
import io.github.viciscat.kustweaks.KusAttributes;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.items.guns.GenericGun;
import techguns.items.guns.GenericGunCharge;
import techguns.items.guns.GenericGunMeleeCharge;

@Mixin(GenericGun.class)
public abstract class GenericGunMixin {

    @Shadow(remap = false)
    public abstract String getCurrentAmmoVariantKey(ItemStack stack);

    // multishot chance, idk if this is the best way to do it

    @Shadow(remap = false) int minFiretime;

    @Inject(
            remap = false,
            method = "shootGun",
            at = @At("HEAD"))
    private void multishotAmountCalc(World world, EntityLivingBase player, ItemStack itemstack, float accuracybonus, float damagebonus, int attackType, EnumHand hand, EnumBulletFirePos firePos, Entity target, CallbackInfo ci,
                                     @Share("bulletMultiplier") LocalIntRef bulletMultiplier, @Share("spreadMultiplier")LocalFloatRef spreadMultiplier) {
        float multiplier = 1;
        // the attribute itself.
		double chance = UpgradableGun.getOrEmpty(itemstack).getUpgradeStack(GunUpgrade.MULTISHOT) * 0.1 + KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_MULTISHOT_CHANCE);
        spreadMultiplier.set(1 + (float) chance / 10.f);
        while (chance >= 1d) {
            multiplier++;
            chance -= 1d;
        }
        if (Math.random() < chance) multiplier++;

        // fire rate extra bullets cuz can't go faster than once per tick
        double fireRate = KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_FIRE_RATE);
        double modifiedFireTime = (minFiretime + 1) / fireRate;
        if (modifiedFireTime < 1.d) {
            multiplier *= (float) (1.d/modifiedFireTime);
        }
        bulletMultiplier.set(Math.round(multiplier));
    }

    @WrapOperation(
            remap = false,
            method = "shootGun",
            at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGun;spawnProjectile(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;FFFLtechguns/entities/projectiles/EnumBulletFirePos;Lnet/minecraft/entity/Entity;)V"))
    private void multiplyShots(GenericGun instance, World world, EntityLivingBase player, ItemStack itemstack, float spread, float offset, float damagebonus, EnumBulletFirePos firePos, Entity target, Operation<Void> original,
                               @Share("bulletMultiplier") LocalIntRef bulletMultiplier, @Share("spreadMultiplier")LocalFloatRef spreadMultiplier) {
        for (int i = 0; i < bulletMultiplier.get(); i++) {
            original.call(instance, world, player, itemstack, spread * spreadMultiplier.get(), offset, damagebonus, firePos, target);
        }
    }

    // Refund chance
    @WrapOperation(
            remap = false,
            method = "shootGunPrimary",
            at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGun;useAmmo(Lnet/minecraft/item/ItemStack;I)I"))
    private int refundAmmoChanceShoot(GenericGun instance, ItemStack stack, int amount, Operation<Integer> original, @Local(argsOnly = true) EntityPlayer player) {
        double attributeValue = kusTweaks$customScaling(UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.REFUND_CHANCE)) + KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_AMMO_REFUND_CHANCE);
        if (!getCurrentAmmoVariantKey(stack).equals("nuke") && (attributeValue >= 1d || Math.random() < attributeValue))
            return 0;
        return original.call(instance, stack, amount);
    }

    @WrapOperation(
            remap = false,
            method = "consumeAmmoOnMeleeHit",
            at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGun;useAmmo(Lnet/minecraft/item/ItemStack;I)I"))
    private int refundAmmoChanceMelee(GenericGun instance, ItemStack stack, int amount, Operation<Integer> original, @Local(argsOnly = true) EntityLivingBase player) {
        double attributeValue = KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_AMMO_REFUND_CHANCE);
        if (attributeValue >= 1d || Math.random() < attributeValue) return 0;
        return original.call(instance, stack, amount);
    }


    // Reload Speed
    @ModifyExpressionValue(
            remap = false,
            method = "shootGunPrimary",
            at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGun;reloadtime:I", opcode = Opcodes.GETFIELD)
    )
    private int editReloadTimeShoot(int original, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) ItemStack stack) {
        double attribute = kusTweaks$customScaling(UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.RELOAD_SPEED)) + KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_RELOAD_SPEED);
        return Math.round(original / (float) attribute);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tryForcedReload",
            at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGun;reloadtime:I", opcode = Opcodes.GETFIELD)
    )
    private int editReloadTimeForceReload(int original, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) ItemStack stack) {
        double attribute = kusTweaks$customScaling(UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.RELOAD_SPEED)) + KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_RELOAD_SPEED);
        return Math.round(original / (float) attribute);
    }

    // Fire Rate
    @ModifyExpressionValue(
            remap = false,
            method = "shootGunPrimary",
            at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGun;minFiretime:I", opcode = Opcodes.GETFIELD)
    )
    private int editFireTimeShoot(int original, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) ItemStack stack) {
        double attribute = UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.FIRE_RATE) * 0.1 + KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_FIRE_RATE);
        return Math.round(original / (float) attribute);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tryForcedReload",
            at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGun;minFiretime:I", opcode = Opcodes.GETFIELD)
    )
    private int editFireTimeForceReload(int original, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) ItemStack stack) {
        double attribute = UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.FIRE_RATE) * 0.1 + KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_FIRE_RATE);
        return Math.round(original / (float) attribute);
    }

	@ModifyExpressionValue(
			remap = false,
			method = {"getAmmoOnUnload", "tryForcedReload", "isFullyLoaded", "getPercentAmmoLeft", "reloadAmmo(Lnet/minecraft/item/ItemStack;)V", "onCreated"},
			at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGun;clipsize:I", opcode = Opcodes.GETFIELD)
	)
	private int editClipSize(int original, @Local(argsOnly = true) ItemStack stack) {
		int upgradeStack = UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.AMMO);
		return original + Math.max((int) (original * 0.1f), 1) * upgradeStack;
	}


    @Mixin(GenericGunCharge.class)
    public static class ChargeMixin {
        @Shadow(remap = false)
        public int ammoConsumedOnFullCharge;

        // refund chance baby
        @WrapOperation(
                method = "onPlayerStoppedUsing",
                at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGunCharge;consumeAmmoCharge(Lnet/minecraft/item/ItemStack;FZ)I", remap = false))
        private int refundAmmoChanceMelee(GenericGunCharge instance, ItemStack item, float f, boolean creative, Operation<Integer> original, @Local(argsOnly = true) EntityLivingBase player) {
            double attributeValue = KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_AMMO_REFUND_CHANCE);
            if (attributeValue >= 1d || Math.random() < attributeValue)
                return (int) Math.ceil(f * ammoConsumedOnFullCharge);
            return original.call(instance, item, f, creative);
        }

        // Reload Speed
        @ModifyExpressionValue(
                method = "onItemRightClick",
                at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGunCharge;reloadtime:I", opcode = Opcodes.GETFIELD, remap = false)
        )
        private int editReloadTimeRightClick(int original, @Local(argsOnly = true) EntityPlayer player) {
            double attribute = KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_RELOAD_SPEED);
            return Math.round(original / (float) attribute);
        }

        @ModifyExpressionValue(
                method = "onItemRightClick",
                at = @At(value = "FIELD", target = "Ltechguns/items/guns/GenericGunCharge;minFiretime:I", opcode = Opcodes.GETFIELD, remap = false)
        )
        private int editFireTimeRightClick(int original, @Local(argsOnly = true) EntityPlayer player) {
            double attribute = KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_FIRE_RATE);
            return Math.round(original / (float) attribute);
        }
    }

	@Unique
	private static double kusTweaks$customScaling(int stacks) {
		return Math.min(stacks, 6) * 0.1 + (stacks > 6 ? 0.39f * (1 - Math.exp(-0.108 * (stacks - 6))) : 0);
	}

    @Mixin(GenericGunMeleeCharge.class)
    public static class MeleeMixin {
        @WrapOperation(
                method = "onBlockDestroyed",
                at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGunMeleeCharge;useAmmo(Lnet/minecraft/item/ItemStack;I)I", remap = false))
        private int refundAmmoChanceMelee(GenericGunMeleeCharge instance, ItemStack stack, int amount, Operation<Integer> original, @Local(argsOnly = true) EntityLivingBase player) {
            double attributeValue = KusAttributes.getAttributeOrDefault(player, KusAttributes.TECHGUNS_AMMO_REFUND_CHANCE);
            ResourceLocation registryName = stack.getItem().getRegistryName();
            if ((registryName == null || !registryName.getPath().equals("miningdrill")) && (attributeValue >= 1d || Math.random() < attributeValue))
                return 0;
            return original.call(instance, stack, amount);
        }
    }


}
