package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.injected.ExtendedHemorrhageEffect;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import uvmidnight.totaltinkers.newweapons.WeaponScimitar;
import uvmidnight.totaltinkers.newweapons.potion.PotionHemorrhage;
import uvmidnight.totaltinkers.newweapons.potion.PotionHemorrhageEffect;

@Mixin(value = WeaponScimitar.class, remap = false)
public class WeaponScimitarMixin {

    @WrapOperation(method = "dealDamage", at = @At(value = "NEW", target = "(Luvmidnight/totaltinkers/newweapons/potion/PotionHemorrhage;II)Luvmidnight/totaltinkers/newweapons/potion/PotionHemorrhageEffect;"))
    private PotionHemorrhageEffect injectDealtDamage(PotionHemorrhage potion, int effectDuration, int amplifier, Operation<PotionHemorrhageEffect> original, @Local(argsOnly = true) ItemStack tool) {
        PotionHemorrhageEffect effect = original.call(potion, effectDuration, amplifier);
        float damage = TagUtil.getToolTag(TagUtil.getTagSafe(tool)).getFloat(Tags.ATTACK);
        ExtendedHemorrhageEffect.of(effect).kus_tweaks$setToolDamage(damage);
        return effect;
    }
}
