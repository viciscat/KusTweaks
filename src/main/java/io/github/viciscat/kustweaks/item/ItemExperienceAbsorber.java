package io.github.viciscat.kustweaks.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemExperienceAbsorber extends Item implements IBauble {

    public ItemExperienceAbsorber() {
        super();
        setRegistryName(KusTweaksMod.MOD_ID, "experience_absorber");
        setTranslationKey(KusTweaksMod.MOD_ID + ".experience_absorber");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);

    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BELT;
    }
}
