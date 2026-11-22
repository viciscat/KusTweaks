package io.github.viciscat.kustweaks;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.util.ChampionHelper;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.rejahtavi.rfp2.RFP2;
import com.srpcotesia.capability.CapabilityEnhancedMob;
import com.srpcotesia.capability.CapabilityParasitePlayer;
import com.srpcotesia.capability.EnhancedMob;
import com.srpcotesia.capability.ParasitePlayer;
import com.tmtravlr.potioncore.potion.PotionMagicShield;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import com.tmtravlr.potioncore.potion.PotionRevival;
import electroblob.wizardry.registry.WizardryItems;
import funwayguy.epicsiegemod.config.props.CfgProps;
import io.github.viciscat.wither.DecayPotion;
import laputan.capabilities.ISizeCapability;
import laputan.capabilities.SizeProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * Methods used in vanilla mixin that use mod methods, which causes a crash if in the mixin itself
 */
public class MixinMethods {

    public static EntityLiving getGolem(EntityPlayerMP entity) {
        ParasitePlayer capability = entity.getCapability(CapabilityParasitePlayer.PARASITE_PLAYER_CAPABILITY, null);
        if (capability == null || !capability.isParasite()) {
            EntityIronGolem golem = new EntityIronGolem(entity.getServerWorld());
            EnhancedMob enhancedMob = golem.getCapability(CapabilityEnhancedMob.ENHANCED_MOB_CAPABILITY, null);
            assert enhancedMob != null;
            enhancedMob.setAnte(1);
            enhancedMob.setBane(2);
            enhancedMob.setDefense(600);
            enhancedMob.setMaxDefense(600);
            enhancedMob.setEnhanced(true);
            return golem;
        } else {
            return new EntityHiGolem(entity.getServerWorld());
        }
    }

    public static void addEffectsToGolem(EntityLivingBase entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(DecayPotion.INSTANCE      , 30*20*60, 0, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE     , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.SPEED          , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.HASTE          , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(PotionRecoil.INSTANCE     , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(PotionMagicShield.INSTANCE, 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(SRPPotions.EPEL_E         , 30*20*60, 0, false, false));
        entityLiving.getEntityData().setBoolean("CannotDropLoot", true);
    }

    public static void addEffectsToPlayer(EntityPlayerMP entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(PotionRevival.INSTANCE, 60*20, 0, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 15*20, 4, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 60*20, 2, false, false));

        if (entityLiving.inventory.armorInventory.get(0).isEmpty()) entityLiving.inventory.armorInventory.set(0, new ItemStack(WizardryItems.spectral_boots));
        if (entityLiving.inventory.armorInventory.get(1).isEmpty()) entityLiving.inventory.armorInventory.set(1, new ItemStack(WizardryItems.spectral_leggings));
        if (entityLiving.inventory.armorInventory.get(2).isEmpty()) entityLiving.inventory.armorInventory.set(2, new ItemStack(WizardryItems.spectral_chestplate));
        if (entityLiving.inventory.armorInventory.get(3).isEmpty()) entityLiving.inventory.armorInventory.set(3, new ItemStack(WizardryItems.spectral_helmet));
    }

    public static int getExtraShots(EntityWither wither) {
        IChampionship capability = wither.getCapability(CapabilityChampionship.CHAMPION_CAP, null);
        if (capability != null) {
            return capability.getRank().getTier();
        } else {
            return -1;
        }
    }

	public static boolean shouldHideHead() {
		return RFP2.api.rfp2IsHeadHidden();
	}

	public static float getExplosionMultiplier(Entity entity) {
		ISizeCapability capability = entity.getCapability(SizeProvider.sizeCapability, null);
		return capability != null ? capability.getScale() : 1;
	}

	public static void bigBoomAt(EntityCreeper entity, float r) {
		if (CfgProps.CR_CENA.get(entity).doubleValue() > (double)0.0F && entity.getCustomNameTag().equalsIgnoreCase("John Cena")) {
			r *= 3;
		}
		IChampionship chp = CapabilityChampionship.getChampionship(entity);
		if (chp != null && ChampionHelper.isElite(chp.getRank())) {
			r *= 1 + 0.3f * chp.getRank().getTier();
		}
		double x = entity.posX + 0.5;
		double y = entity.posY + 0.5;
		double z = entity.posZ + 0.5;
		World world = entity.getEntityWorld();
		world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		EntityNukeExplosionMK5 mk5 = EntityNukeExplosionMK5.statFacNoRad(world, (int) r, x, y, z);
		world.spawnEntity(mk5);
		if (BombConfig.enableNukeClouds) {
			EntityNukeTorex.statFac(world, x, y, z, r);
		}
	}
}
