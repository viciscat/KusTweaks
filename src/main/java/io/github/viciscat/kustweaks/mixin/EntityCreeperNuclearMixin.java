package io.github.viciscat.kustweaks.mixin;

import com.hbm.entity.mob.EntityCreeperNuclear;
import com.hbm.explosion.ExplosionNukeSmall;
import io.github.viciscat.kustweaks.MixinMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EntityCreeperNuclear.class, remap = false)
public class EntityCreeperNuclearMixin {

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lcom/hbm/entity/logic/EntityNukeExplosionMK5;statFac(Lnet/minecraft/world/World;IDDD)Lcom/hbm/entity/logic/EntityNukeExplosionMK5;", remap = false), index = 1, remap = true)
	private int increaseSizePowered(int r) {
		float multiplier = MixinMethods.getExplosionMultiplier((EntityCreeperNuclear) (Object) this);
		return (int) (r * multiplier);
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lcom/hbm/explosion/ExplosionNukeSmall;explode(Lnet/minecraft/world/World;DDDLcom/hbm/explosion/ExplosionNukeSmall$MukeParams;)V", remap = false), remap = true, index = 4)
	private ExplosionNukeSmall.MukeParams increaseSize(ExplosionNukeSmall.MukeParams params) {
		float multiplier = MixinMethods.getExplosionMultiplier((EntityCreeperNuclear) (Object) this);
		ExplosionNukeSmall.MukeParams newParams = new ExplosionNukeSmall.MukeParams();
		newParams.blastRadius = params.blastRadius * multiplier;
		newParams.killRadius = params.killRadius * multiplier;
		newParams.radiationLevel = params.radiationLevel;
		if (newParams.blastRadius >= 35) {
			newParams.shrapnelCount = 0;
			newParams.miniNuke = false;
		}
		return newParams;
	}
}
