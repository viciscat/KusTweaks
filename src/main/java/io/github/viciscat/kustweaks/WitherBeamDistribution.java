package io.github.viciscat.kustweaks;

import io.github.viciscat.kustweaks.entity.EntityWitherSkullBeam;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.DDDPredefinedDistribution;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;
import yeelp.distinctdamagedescriptions.capability.IDamageDistribution;
import yeelp.distinctdamagedescriptions.capability.impl.DamageDistribution;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

import javax.annotation.Nonnull;
import java.util.*;

public class WitherBeamDistribution implements DDDPredefinedDistribution {
	private final DamageDistribution damageDistribution;

	public WitherBeamDistribution() {
		Object2FloatMap<DDDDamageType> map = new Object2FloatOpenHashMap<>(2);
		DDDDamageType dddMagic = DDDRegistries.damageTypes.get("ddd_magic");
		map.put(DDDBuiltInDamageType.NECROTIC, dddMagic == null ? 1f : 0.8f);
		if (dddMagic != null) map.put(dddMagic, 0.2f);
		else KusTweaksMod.LOGGER.warn("ddd_magic not found. Ignoring.");
		damageDistribution = new DamageDistribution(map);
	}

	@Override
	public boolean enabled() {
		return true;
	}

	@Override
	public Set<DDDDamageType> getTypes(@Nonnull DamageSource damageSource, @Nonnull EntityLivingBase entityLivingBase) {
		return damageSource.getDamageType().equals(getName()) ? damageDistribution.getCategories() : Collections.emptySet();
	}

	@Override
	public String getName() {
		return EntityWitherSkullBeam.DAMAGE_TYPE;
	}

	@Override
	public Optional<IDamageDistribution> getDamageDistribution(@Nonnull DamageSource damageSource, @Nonnull EntityLivingBase entityLivingBase) {
		return damageSource.getDamageType().equals(getName()) ? Optional.of(damageDistribution) : Optional.empty();
	}

	@Override
	public Source getCreationSource() {
		return Source.OTHER;
	}
}
