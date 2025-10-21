package io.github.viciscat.kustweaks.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExperiencedEnchantment extends Enchantment {

    public static final ExperiencedEnchantment INSTANCE = new ExperiencedEnchantment();

    protected ExperiencedEnchantment() {
        super(Rarity.UNCOMMON, EnumHelper.addEnchantmentType("all_but_armors", item -> !(item instanceof ItemArmor)), new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("experienced");
        this.setRegistryName("experienced");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @SubscribeEvent
    public void onXpDrop(LivingExperienceDropEvent event) {
        EntityPlayer player = event.getAttackingPlayer();
        if(player != null) {
            int level = EnchantmentHelper.getMaxEnchantmentLevel(this, player);
            if (level == 0) return;
            event.setDroppedExperience(event.getDroppedExperience() * (level + 1));
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if(player != null) {
            int level = EnchantmentHelper.getMaxEnchantmentLevel(this, player);
            if (level == 0) return;
            event.setExpToDrop(event.getExpToDrop() * (level + 1));
        }
    }
}
