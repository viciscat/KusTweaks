package io.github.viciscat.kustweaks.mixin;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import cofh.core.proxy.EventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.capabilities.TGExtendedPlayer;

@Pseudo
@Mixin(value = EventHandler.class, remap = false)
public abstract class CofhEventHandlerMixin {
    @Shadow(remap = false) public abstract boolean isSoulbound(ItemStack stack);

    @Shadow(remap = false)
    public static boolean addToPlayerInventory(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Inject(method = "handlePlayerDropsEvent", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/player/PlayerDropsEvent;getDrops()Ljava/util/List;", remap = false, shift = At.Shift.AFTER), remap = false)
    private void baublesCompat(PlayerDropsEvent event, CallbackInfo ci) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(event.getEntityPlayer());
        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack item = baubles.getStackInSlot(i);
            if (isSoulbound(item)) {
                if (addToPlayerInventory(event.getEntityPlayer(), item)) {
                    baubles.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }

        TGExtendedPlayer tgExtendedPlayer = TGExtendedPlayer.get(event.getEntityPlayer());
        IInventory tgInventory = tgExtendedPlayer.getTGInventory();
        for (int i = 0; i < tgInventory.getSizeInventory(); i++) {
            ItemStack item = tgInventory.getStackInSlot(i);
            if (isSoulbound(item)) {
                if (addToPlayerInventory(event.getEntityPlayer(), item)) {
                    tgInventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
