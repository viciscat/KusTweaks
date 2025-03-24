package io.github.viciscat.kustweaks.potion;

import io.github.viciscat.kustweaks.KusAttributes;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.potion.Potion;

public class RedirectionPotion extends Potion {

	public static final RedirectionPotion INSTANCE = new RedirectionPotion();

	protected RedirectionPotion() {
		super(false, 0x55FFFF);
		setPotionName("kus_tweaks.effect.redirection");
		setRegistryName(KusTweaksMod.MOD_ID, "redirection");
		registerPotionAttributeModifier(KusAttributes.HEAL_PERCENT_DAMAGE, "3165e18c-e429-4878-98aa-65140fbd9fa2", 0.1, 0);
	}
}
