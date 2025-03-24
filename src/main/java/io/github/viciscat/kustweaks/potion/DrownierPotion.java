package io.github.viciscat.kustweaks.potion;

import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.potion.Potion;

public class DrownierPotion extends Potion {

    public static final Potion INSTANCE = new DrownierPotion();

    protected DrownierPotion() {
        super(true, 0x0003a8);
        setPotionName("kus_tweaks.effect.drownier");
        setRegistryName(KusTweaksMod.MOD_ID, "drownier");
    }
}
