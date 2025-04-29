package io.github.viciscat.kustweaks.sound;

import io.github.viciscat.kustweaks.entity.EntityWitherSkullBeam;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BeamSound extends MovingSound {

    @GameRegistry.ObjectHolder("kus_tweaks:beam_loop")
    private static SoundEvent beamLoop;
    private final EntityWitherSkullBeam source;

    public BeamSound(EntityWitherSkullBeam source) {
        super(beamLoop, SoundCategory.HOSTILE);
        this.source = source;
        attenuationType = AttenuationType.NONE;
        repeat = true;
        repeatDelay = 0;
        volume = 64.0f;
    }

    @Override
    public void update() {
        if (source.isDead) {
            donePlaying = true;
            return;
        }
        xPosF = (float) source.posX;
        yPosF = (float) source.posY;
        zPosF = (float) source.posZ;
    }
}
