package io.github.viciscat.kustweaks;

import io.github.viciscat.kustweaks.item.ItemExperienceAbsorber;
import io.github.viciscat.kustweaks.item.ItemGunUpgrade;
import io.github.viciscat.kustweaks.item.ItemInfiniteAntiGravPack;
import io.github.viciscat.kustweaks.item.ItemMagnet;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.Objects;

public class KusItems {
    private static final String PREFIX = KusTweaksMod.MOD_ID + ".";
    public static final ItemArmor.ArmorMaterial GLASS_ARMOR = Objects.requireNonNull(EnumHelper.addArmorMaterial(
            KusTweaksMod.MOD_ID + ":glass_armor",
            KusTweaksMod.MOD_ID +":glass",
            3,
            new int[]{1, 1, 1, 1},
            5,
            SoundEvents.BLOCK_GLASS_PLACE,
            0
    ));

    @GameRegistry.ObjectHolder("kus_tweaks:item_magnet")
    public static ItemMagnet itemMagnet;
    @GameRegistry.ObjectHolder("kus_tweaks:infinite_antigravpack")
    public static ItemInfiniteAntiGravPack itemInfiniteAntiGravPack;
    @GameRegistry.ObjectHolder("kus_tweaks:evil_essence")
    public static Item itemEvilEssence;
    @GameRegistry.ObjectHolder("kus_tweaks:experience_absorber")
    public static ItemExperienceAbsorber itemExperienceAbsorber;
    @GameRegistry.ObjectHolder("kus_tweaks:chaotic_amethyst")
    public static Item itemChaoticAmethyst;

    @GameRegistry.ObjectHolder("kus_tweaks:glass_helmet")
    public static ItemArmor itemGlassHelmet;
    @GameRegistry.ObjectHolder("kus_tweaks:glass_chestplate")
    public static ItemArmor itemGlassChestplate;
    @GameRegistry.ObjectHolder("kus_tweaks:glass_leggings")
    public static ItemArmor itemGlassLeggings;
    @GameRegistry.ObjectHolder("kus_tweaks:glass_boots")
    public static ItemArmor itemGlassBoots;

	public static ItemGunUpgrade[] gunUpgrades;

    
    public static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(new ItemMagnet());
        registry.register(new ItemExperienceAbsorber());
        registry.register(new ItemInfiniteAntiGravPack("infinite_antigravpack", 123456789));
        registry.register(new Item().setRegistryName("evil_essence").setCreativeTab(CreativeTabs.MATERIALS).setTranslationKey(PREFIX + "evil_essence"));
        registry.register(new Item().setRegistryName("chaotic_amethyst").setCreativeTab(CreativeTabs.MATERIALS).setTranslationKey(PREFIX + "chaotic_amethyst"));

        registry.register(new ItemArmor(GLASS_ARMOR, 0, EntityEquipmentSlot.HEAD).setRegistryName("glass_helmet").setTranslationKey(PREFIX + "glass_helmet").setMaxDamage(38));
        registry.register(new ItemArmor(GLASS_ARMOR, 0, EntityEquipmentSlot.CHEST).setRegistryName("glass_chestplate").setTranslationKey(PREFIX + "glass_chestplate").setMaxDamage(56));
        registry.register(new ItemArmor(GLASS_ARMOR, 0, EntityEquipmentSlot.LEGS).setRegistryName("glass_leggings").setTranslationKey(PREFIX + "glass_leggings").setMaxDamage(52));
        registry.register(new ItemArmor(GLASS_ARMOR, 0, EntityEquipmentSlot.FEET).setRegistryName("glass_boots").setTranslationKey(PREFIX + "glass_boots").setMaxDamage(45));

		gunUpgrades = Arrays.stream(GunUpgrade.values()).map(ItemGunUpgrade::new).toArray(ItemGunUpgrade[]::new);
		for (ItemGunUpgrade gunUpgrade : gunUpgrades) {
			gunUpgrade.setCreativeTab(CreativeTabs.MATERIALS);
			registry.register(gunUpgrade.setTranslationKey(PREFIX + gunUpgrade.getRegistryName().getPath()));
		}
    }
}
