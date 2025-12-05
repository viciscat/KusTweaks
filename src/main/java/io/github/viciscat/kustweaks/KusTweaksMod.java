package io.github.viciscat.kustweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.viciscat.kustweaks.block.HyaloclastiteBlock;
import io.github.viciscat.kustweaks.block.RespawnAnchorBlock;
import io.github.viciscat.kustweaks.capability.GunExperienceTracker;
import io.github.viciscat.kustweaks.capability.GunExperienceTrackerImpl;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import io.github.viciscat.kustweaks.capability.UpgradableGunImpl;
import io.github.viciscat.kustweaks.enchants.ExperiencedEnchantment;
import io.github.viciscat.kustweaks.entity.EntityWitherSkullBeam;
import io.github.viciscat.kustweaks.entity.render.RenderWitherSkullBeam;
import io.github.viciscat.kustweaks.network.KusNetwork;
import io.github.viciscat.kustweaks.potion.DrownierPotion;
import io.github.viciscat.kustweaks.potion.KindlingPotion;
import io.github.viciscat.kustweaks.potion.RedirectionPotion;
import io.github.viciscat.kustweaks.potion.ResistancePenetrationPotion;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import techguns.tileentities.operation.UpgradeBenchRecipes;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

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

    public static final DamageSource POISON = new DamageSource("kus_poison").setMagicDamage().setDamageBypassesArmor();
    public static final DamageSource PARASITE_BLEED = new DamageSource("kus_parableed").setDamageBypassesArmor();


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ForgeRegistries.POTIONS.register(KindlingPotion.INSTANCE);
        ForgeRegistries.POTIONS.register(RedirectionPotion.INSTANCE);
        ForgeRegistries.POTIONS.register(ResistancePenetrationPotion.INSTANCE);
        ForgeRegistries.POTIONS.register(DrownierPotion.INSTANCE);
        ForgeRegistries.ENCHANTMENTS.register(ExperiencedEnchantment.INSTANCE);

        ResourceLocation location = new ResourceLocation(MOD_ID, "beam_charge");
        ForgeRegistries.SOUND_EVENTS.register(new SoundEvent(location).setRegistryName(location));
        location = new ResourceLocation(MOD_ID, "beam_loop");
        ForgeRegistries.SOUND_EVENTS.register(new SoundEvent(location).setRegistryName(location));

        File configFile = new File(event.getModConfigurationDirectory(), MOD_ID + ".json");
        KusConfig.loadConfig(configFile);
        KusNetwork.init();

		CapabilityManager.INSTANCE.register(UpgradableGun.class, UpgradableGun.Storage.INSTANCE, UpgradableGunImpl::new);
		CapabilityManager.INSTANCE.register(GunExperienceTracker.class, GunExperienceTracker.Storage.INSTANCE, GunExperienceTrackerImpl::new);

		UpgradeBenchRecipes.recipes.add(new GunUpgradeRecipe());

        // Every entity in our mod has an ID (local to this mod)
        int id = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "wither_skull_beam"), EntityWitherSkullBeam.class, "wither_skull_beam", id++, this, 64, 3, true);
        if (event.getSide() == Side.CLIENT) {
            RenderingRegistry.registerEntityRenderingHandler(EntityWitherSkullBeam.class, RenderWitherSkullBeam::new);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Dirt: " + Blocks.DIRT.getTranslationKey());
        new SplashTrait().addItem(KusItems.itemChaoticAmethyst);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        KusConfig.finalizeConfig();
		DDDRegistries.distributions.register(new WitherBeamDistribution());
    }
}
