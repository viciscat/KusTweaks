package io.github.viciscat.kustweaks.mixin;

import bettercombat.mod.client.handler.EventHandlersClient;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EventHandlersClient.class, remap = false)
public class BetterCombatEventHandlersClientMixin {

	@Inject(method = "onMouseLeftClick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToServer(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;)V"))
	private static void onMouseLeftClick(MouseEvent event, CallbackInfo ci, @Local EntityPlayer player) {
		player.resetCooldown();
	}
}
