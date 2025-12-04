package io.github.viciscat.kustweaks.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.tools.melee.item.BattleSign;

@Mixin(value = BattleSign.class, remap = false)
public class BattleSignMixin {

	@Inject(method = "reducedDamageBlocked", at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/utils/ToolHelper;damageTool(Lnet/minecraft/item/ItemStack;ILnet/minecraft/entity/EntityLivingBase;)V"))
	public void reducedDamageBlocked(LivingHurtEvent event, CallbackInfo ci){
		if (event.getEntityLiving() instanceof EntityPlayer player) kusTweaks$disableBattleSign(player);
	}

	@Inject(method = "reflectProjectiles", at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/utils/ToolHelper;damageTool(Lnet/minecraft/item/ItemStack;ILnet/minecraft/entity/EntityLivingBase;)V"))
	public void reflectProjectiles(LivingAttackEvent event, CallbackInfo ci){
		if (event.getEntityLiving() instanceof EntityPlayer player) kusTweaks$disableBattleSign(player);
	}

	@Unique
	private void kusTweaks$disableBattleSign(EntityPlayer player) {
		player.getCooldownTracker().setCooldown(player.getActiveItemStack().getItem(), 40);
		player.resetActiveHand();
	}
}
