package io.github.viciscat.kustweaks.capability;

import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import techguns.damagesystem.TGDamageSource;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public interface GunExperienceTracker extends INBTSerializable<NBTTagCompound> {
	@CapabilityInject(GunExperienceTracker.class)
	Capability<GunExperienceTracker> CAPABILITY = null;
	ResourceLocation ID = new ResourceLocation(KusTweaksMod.MOD_ID, "gun_experience_tracker");

	@SuppressWarnings("DataFlowIssue")
	static @Nullable GunExperienceTracker getTracker(EntityLivingBase entity) {
		return entity.getCapability(CAPABILITY, null);
	}

    void trackDamage(EntityPlayer shooter, UpgradableGun gun, float amount);

	void distributeXp(float maxHealth);

	class Storage implements Capability.IStorage<GunExperienceTracker> {

		public static final Storage INSTANCE = new Storage();
		private Storage() {}

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<GunExperienceTracker> capability, GunExperienceTracker instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<GunExperienceTracker> capability, GunExperienceTracker instance, EnumFacing side, NBTBase nbt) {
			instance.deserializeNBT((NBTTagCompound) nbt);
		}

	}

	@SubscribeEvent
	static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject().world == null || event.getObject().world.isRemote || !(event.getObject() instanceof EntityLivingBase base) || base instanceof EntityPlayer) return;
		event.addCapability(ID, new GunExperienceTrackerImpl());
	}

	@SubscribeEvent
	static void onEntityDamaged(LivingHurtEvent event) {
		if (event.getEntityLiving().world.isRemote) return;
		DamageSource source = event.getSource();
		if (!(source instanceof TGDamageSource)) return;
		Entity entity = source.getTrueSource();
		if (!(entity instanceof EntityPlayer player)) return;
		UpgradableGun gun = UpgradableGun.get(player.getHeldItemMainhand());
		if (gun == null) gun = UpgradableGun.get(player.getHeldItemOffhand());
		if (gun == null) return;
		GunExperienceTracker tracker = getTracker(event.getEntityLiving());
		if (tracker == null) return;
		tracker.trackDamage(player, gun, event.getAmount());
	}

	@SubscribeEvent
	static void onEntityDeath(LivingDeathEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living.world.isRemote || living instanceof EntityPlayer) return;
		GunExperienceTracker tracker = getTracker(living);
		if (tracker == null) return;
		tracker.distributeXp(living.getMaxHealth());
	}
}
