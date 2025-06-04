package io.github.viciscat.kustweaks.sound;

import io.github.viciscat.kustweaks.entity.EntityWitherSkullBeam;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BeamSound extends MovingSound {

    private final EntityWitherSkullBeam source;

    public BeamSound(EntityWitherSkullBeam source) {
        super(KusSoundEvents.BEAM_LOOP, SoundCategory.HOSTILE);
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
