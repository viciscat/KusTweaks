package io.github.viciscat.kustweaks.mixin;

import c4.conarm.common.armor.modifiers.ArmorModifiers;
import c4.conarm.common.armor.modifiers.accessories.ModTravelPotion;
import c4.conarm.lib.modifiers.AccessoryModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.tconstruct.library.utils.ModifierTagHolder;

@Mixin(value = ModTravelPotion.class, remap = false)
public abstract class ModTravelPotionMixin extends AccessoryModifier {
	public ModTravelPotionMixin(String identifier) {
		super(identifier);
	}

	@Override
	public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
		ModifierTagHolder modtag = ModifierTagHolder.getModifier(tool, ArmorModifiers.modTravelPotion.getIdentifier());
		ModTravelPotion.PotionsData data = modtag.getTagData(ModTravelPotion.PotionsData.class);
		if (data == null) return;
		for (int i = 0; i < data.potions.getSlots(); i++) {
			ItemStack stack = data.potions.getStackInSlot(i);
			stack.updateAnimation(world, player, i, false);
		}
	}
}
