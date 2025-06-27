package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiSubtitleOverlay.class)
public class GuisSubtitleOverlayMixin {

    @WrapOperation(method = "renderSubtitles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;showSubtitles:Z", opcode = Opcodes.GETFIELD))
    private boolean showSubtitles(GameSettings instance, Operation<Boolean> original) {
        return instance.getSoundLevel(SoundCategory.MASTER) <= 0 && original.call(instance);
    }
}
