package io.github.viciscat.kustweaks.injected;

import uvmidnight.totaltinkers.newweapons.potion.PotionHemorrhageEffect;

public interface ExtendedHemorrhageEffect {
    void kus_tweaks$setToolDamage(float toolDamage);

    static ExtendedHemorrhageEffect of(PotionHemorrhageEffect effect) {
        return (ExtendedHemorrhageEffect) effect;
    }
}
