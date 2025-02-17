package io.github.viciscat.kustweaks.item;

import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import techguns.items.additionalslots.ItemAntiGravPack;

public class ItemInfiniteAntiGravPack extends ItemAntiGravPack {
    public ItemInfiniteAntiGravPack(String unlocalizedName, int dur) {
        super(unlocalizedName, 1, dur);
        setTranslationKey(KusTweaksMod.MOD_ID + "." + unlocalizedName);
    }

    @Override
    public void onPlayerTick(ItemStack item, TickEvent.PlayerTickEvent event) {
        if (item.getItemDamage() + 1 >= item.getMaxDamage()) {
            this.tryReloadAndRepair(item, event.player);
        }
    }
}
