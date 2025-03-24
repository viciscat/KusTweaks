package io.github.viciscat.kustweaks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;

import javax.annotation.Nullable;

import static io.github.viciscat.kustweaks.KusTweaksMod.MOD_ID;

public final class KusAttributes {

    private KusAttributes() {
        throw new RuntimeException("This is a utility class and cannot be instantiated");
    }

    public static final IAttribute TECHGUNS_RELOAD_SPEED = new RangedAttribute(null, MOD_ID + ".tgReloadSpeed", 1, 0, 64).setShouldWatch(true);
    public static final IAttribute TECHGUNS_FIRE_RATE = new RangedAttribute(null, MOD_ID + ".tgFireRate", 1, 0, 64).setShouldWatch(true);
    public static final IAttribute TECHGUNS_AMMO_REFUND_CHANCE = new RangedAttribute(null, MOD_ID + ".tgAmmoRefundChance", 0, 0, 1).setShouldWatch(true);
    public static final IAttribute TECHGUNS_MULTISHOT_CHANCE = new RangedAttribute(null, MOD_ID + ".tgMultishotChance", 0, 0, 16);
    public static final IAttribute TECHGUNS_PROJECTILE_BOUNCE_CHANCE = new RangedAttribute(null, MOD_ID + ".tgProjectileBounceChance", 0, 0, 16);
    public static final IAttribute EXPLOSION_SIZE_MULTIPLIER = new RangedAttribute(null, MOD_ID + ".explosionSizeMultiplier", 1, 0, 16);
    public static final IAttribute EXTRA_FIRE_DAMAGE_ATTRIBUTE = new RangedAttribute(null, MOD_ID + ".extraFireDamage", 0, 0, 4096);
    public static final IAttribute LIFE_STEAL_PERCENTAGE = new RangedAttribute(null, MOD_ID + ".lifeStealPercentage", 0.0D, 0.0D, 10.0D);
    public static final IAttribute OUT_OF_WORLD_PERCENTAGE = new RangedAttribute(null, MOD_ID + ".outOfWorldPercentage", 0.0D, 0.0D, 10.0D);
    public static final IAttribute DIRECT_DAMAGE_PERCENTAGE = new RangedAttribute(null, MOD_ID + ".directDamagePercentage", 0.0D, 0.0D, 10.0D);
    public static final IAttribute HEAL_AMOUNT_PER_TICK = new RangedAttribute(null, MOD_ID + ".healPerTick", 0.0D, 0.0D, 1024.0D);
    public static final IAttribute HEAL_PERCENT_MAX_HEALTH_PER_TICK = new RangedAttribute(null, MOD_ID + ".healPercentMaxHealthPerTick", 0.0D, 0.0D, 10.0D);
    public static final IAttribute HEAL_PERCENT_DAMAGE = new RangedAttribute(null, MOD_ID + ".healPercentDamage", 0.0D, 0.0D, 1.0D);
    public static final IAttribute TRUE_HEAL_PERCENT_DAMAGE = new RangedAttribute(null, MOD_ID + ".trueHealPercentDamage", 0.0D, 0.0D, 1.0D);
    public static final IAttribute MAGIC_PERCENT_DAMAGE = new RangedAttribute(null, MOD_ID + ".magicPercentDamage", 0.0D, 0.0D, 2048.0D);



    public static final IAttribute[] ALL_ATTRIBUTES = new IAttribute[] {
            TECHGUNS_RELOAD_SPEED,
            TECHGUNS_FIRE_RATE,
            TECHGUNS_AMMO_REFUND_CHANCE,
            TECHGUNS_MULTISHOT_CHANCE,
            TECHGUNS_PROJECTILE_BOUNCE_CHANCE,
            EXPLOSION_SIZE_MULTIPLIER,
            EXTRA_FIRE_DAMAGE_ATTRIBUTE,
            LIFE_STEAL_PERCENTAGE,
            OUT_OF_WORLD_PERCENTAGE,
            DIRECT_DAMAGE_PERCENTAGE,
            HEAL_AMOUNT_PER_TICK,
            HEAL_PERCENT_MAX_HEALTH_PER_TICK,
            HEAL_PERCENT_DAMAGE,
            TRUE_HEAL_PERCENT_DAMAGE,
            MAGIC_PERCENT_DAMAGE
    };

    public static double getAttributeOrDefault(@Nullable EntityLivingBase entity, IAttribute attribute) {
        if (entity == null) return attribute.getDefaultValue();
        IAttributeInstance instance = entity.getEntityAttribute(attribute);
        //noinspection ConstantValue
        if (instance == null) return attribute.getDefaultValue();
        return instance.getAttributeValue();
    }
}
