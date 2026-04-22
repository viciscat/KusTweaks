package io.github.viciscat.kustweaks.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import omtteam.omlib.util.GeneralUtil;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class ItemBulwarkUpgrade extends Item {

	public ItemBulwarkUpgrade() {
		setRegistryName("bulwark_upgrade");
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
			tooltip.add(GeneralUtil.getShiftDetail());
		} else {
			tooltip.add("");
			tooltip.add(TextFormatting.BLUE + safeLocalize("turret.upgrade.label"));
			tooltip.add("");
			tooltip.add(safeLocalize("turret.upgrade.turretinfo"));
			tooltip.add(safeLocalize("turret.upgrade.stacks"));
			tooltip.add("");
			tooltip.add(TextFormatting.DARK_GRAY + "Beefy turret!");
		}
	}
}
