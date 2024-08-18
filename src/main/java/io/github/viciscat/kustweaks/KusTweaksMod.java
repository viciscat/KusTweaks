package io.github.viciscat.kustweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

@Mod(modid = KusTweaksMod.MOD_ID, useMetadata=true)
public class KusTweaksMod {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String MOD_ID = "kus_tweaks";

    @GameRegistry.ObjectHolder("kus_tweaks:hyaloclastite")
    public static HyaloclastiteBlock hyaloclastiteBlock;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), MOD_ID + ".json");
        KusConfig.loadConfig(configFile);
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
