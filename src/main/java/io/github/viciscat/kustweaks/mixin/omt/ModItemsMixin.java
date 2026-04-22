package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.openmodularturrets.init.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModItems.class, remap = false)
public class ModItemsMixin {

	@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lomtteam/omlib/util/InitHelper;registerItem(Lnet/minecraft/item/Item;Lnet/minecraftforge/registries/IForgeRegistry;)Lnet/minecraft/item/Item;", ordinal = 4))
	private static Item init(Item item, IForgeRegistry<Item> registry, Operation<Item> original) {
		return new Item();
	}
}
