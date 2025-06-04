package io.github.viciscat.kustweaks.sound;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class KusSoundEvents {

    @GameRegistry.ObjectHolder("kus_tweaks:beam_loop")
    public static SoundEvent BEAM_LOOP;

    @GameRegistry.ObjectHolder("kus_tweaks:beam_charge")
    public static SoundEvent BEAM_CHARGE;
}
