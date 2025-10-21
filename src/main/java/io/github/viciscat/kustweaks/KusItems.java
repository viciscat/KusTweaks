package io.github.viciscat.kustweaks;

import io.github.viciscat.kustweaks.item.ItemExperienceAbsorber;
import io.github.viciscat.kustweaks.item.ItemInfiniteAntiGravPack;
import io.github.viciscat.kustweaks.item.ItemMagnet;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class KusItems {
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

    
    public static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(new ItemMagnet());
        registry.register(new ItemExperienceAbsorber());
        registry.register(new ItemInfiniteAntiGravPack("infinite_antigravpack", 123456789));
        registry.register(new Item().setRegistryName("evil_essence").setCreativeTab(CreativeTabs.MATERIALS).setTranslationKey(KusTweaksMod.MOD_ID + ".evil_essence"));
        registry.register(new Item().setRegistryName("chaotic_amethyst").setCreativeTab(CreativeTabs.MATERIALS).setTranslationKey(KusTweaksMod.MOD_ID + ".chaotic_amethyst"));

    }
}
