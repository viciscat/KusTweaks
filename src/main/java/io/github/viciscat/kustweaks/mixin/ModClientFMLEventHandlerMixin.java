package io.github.viciscat.kustweaks.mixin;

import moe.plushie.armourers_workshop.client.handler.ModClientFMLEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModClientFMLEventHandler.class, remap = false)
public class ModClientFMLEventHandlerMixin {

	@Shadow
	private boolean shownMoBendsWarning;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		shownMoBendsWarning = true;
	}
}
