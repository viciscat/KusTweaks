package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.MixinMethods;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererPlayer;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SkinLayerRendererPlayer.class, remap = false)
public class SkinLayerRendererPlayerMixin {
	@Inject(method = "getSkinDescriptorFromArmourer", at = @At("HEAD"), cancellable = true)
	private void dontRenderInFirstPerson(Entity entity, ISkinType skinType, CallbackInfoReturnable<ISkinDescriptor> cir) {
		if (MixinMethods.shouldHideHead() && skinType == SkinTypeRegistry.skinHead) cir.setReturnValue(null);
	}
}
