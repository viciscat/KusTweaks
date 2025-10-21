package io.github.viciscat.kustweaks.potion;

import arekkuusu.enderskills.common.skill.attribute.offense.ResistancePenetration;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.potion.Potion;

public class ResistancePenetrationPotion extends Potion {

    public static final ResistancePenetrationPotion INSTANCE = new ResistancePenetrationPotion();

    protected ResistancePenetrationPotion() {
        super(false, 0x064000);
        setPotionName("kus_tweaks.effect.penetrator");
        setRegistryName(KusTweaksMod.MOD_ID, "penetrator");
        registerPotionAttributeModifier(ResistancePenetration.RESISTANCE_PENETRATION_POWER, "f22ae996-cfd6-4509-ad07-d72d19538c6c", 0.05, 0);


    }
}
