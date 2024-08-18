package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.KusConfig;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.handlers.EntitySizeHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Pseudo
@Mixin(value = EntitySizeHandler.class, remap = false)
public class LilliputianEntitySizeHandlerMixin {

	@Unique
	private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(UUID.randomUUID(), "size_health_mod", 0.0, 1);

	@WrapOperation(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D"), remap = false)
	private static double modifyScaling(double a, double b, Operation<Double> original) {
		return a;
	}

	@Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;getEntityAttribute(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;", ordinal = 2, remap = true), remap = false)
	private static void healthAttribute(LivingEvent.LivingUpdateEvent event, CallbackInfo ci, @Local() EntityLiving entity, @Local()ISizeCapability capability) {
		IAttributeInstance maxHealth = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		boolean shouldMaxOutHealth = entity.getMaxHealth() - entity.getHealth() < 0.01f;
		maxHealth.removeModifier(HEALTH_MODIFIER.getID());
		maxHealth.applyModifier((new AttributeModifier(HEALTH_MODIFIER.getID(), HEALTH_MODIFIER.getName(), capability.getScale() - 1.f, HEALTH_MODIFIER.getOperation())).setSaved(false));
		if (shouldMaxOutHealth) {
			entity.setHealth(entity.getMaxHealth());
		}
	}

	@ModifyArg(method = "onAddCapabilites", at = @At(value = "INVOKE", target = "Llilliputian/capabilities/DefaultSizeCapability;<init>(F)V"), remap = false, index = 0)
	private static float randomScale(float original) {
		if (original == 1.f) {
			return Math.random() < KusConfig.veryLargeChance ? 4.f : Math.random() < KusConfig.verySmallChance ? 0.4f : original;
		} else {
			return Math.random() < KusConfig.veryLargeChanceOther ? 4.f : Math.random() < KusConfig.verySmallChanceOther ? 0.4f : original;
		}
	}
}
