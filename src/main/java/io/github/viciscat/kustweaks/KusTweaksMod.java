package io.github.viciscat.kustweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.viciscat.kustweaks.block.HyaloclastiteBlock;
import io.github.viciscat.kustweaks.block.RespawnAnchorBlock;
import io.github.viciscat.kustweaks.item.ItemExperienceAbsorber;
import io.github.viciscat.kustweaks.item.ItemInfiniteAntiGravPack;
import io.github.viciscat.kustweaks.item.ItemMagnet;
import io.github.viciscat.kustweaks.network.KusNetwork;
import io.github.viciscat.kustweaks.potion.DrownierPotion;
import io.github.viciscat.kustweaks.potion.KindlingPotion;
import io.github.viciscat.kustweaks.potion.RedirectionPotion;
import io.github.viciscat.kustweaks.potion.ResistancePenetrationPotion;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = KusTweaksMod.MOD_ID, useMetadata=true)
public class KusTweaksMod {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String MOD_ID = "kus_tweaks";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @GameRegistry.ObjectHolder("kus_tweaks:hyaloclastite")
    public static HyaloclastiteBlock hyaloclastiteBlock;

    @GameRegistry.ObjectHolder("kus_tweaks:respawn_anchor")
    public static RespawnAnchorBlock respawnAnchorBlock;

    @GameRegistry.ObjectHolder("kus_tweaks:item_magnet")
    public static ItemMagnet itemMagnet;

    @GameRegistry.ObjectHolder("kus_tweaks:infinite_antigravpack")
    public static ItemInfiniteAntiGravPack itemInfiniteAntiGravPack;

    @GameRegistry.ObjectHolder("kus_tweaks:evil_essence")
    public static Item itemEvilEssence;

    @GameRegistry.ObjectHolder("kus_tweaks:experience_absorber")
    public static ItemExperienceAbsorber itemExperienceAbsorber;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ForgeRegistries.POTIONS.register(KindlingPotion.INSTANCE);
        ForgeRegistries.POTIONS.register(RedirectionPotion.INSTANCE);
        ForgeRegistries.POTIONS.register(ResistancePenetrationPotion.INSTANCE);
        ForgeRegistries.POTIONS.register(DrownierPotion.INSTANCE);
        File configFile = new File(event.getModConfigurationDirectory(), MOD_ID + ".json");
        KusConfig.loadConfig(configFile);
        KusNetwork.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Dirt: " + Blocks.DIRT.getTranslationKey());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        KusConfig.finalizeConfig();
    }
}
