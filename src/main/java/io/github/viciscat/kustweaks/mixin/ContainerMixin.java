package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Container.class)
public class ContainerMixin {

	@WrapOperation(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemStacksEqualUsingNBTShareTag(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", remap = false))
	private boolean eq(ItemStack stackA, ItemStack stackB, Operation<Boolean> original) {
		return UpgradableGun.getOrEmpty(stackA).equals(UpgradableGun.getOrEmpty(stackB)) && original.call(stackA, stackB);
	}
}
