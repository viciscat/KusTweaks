package io.github.viciscat.kustweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class KindlingPotion extends Potion {

    private static final ResourceLocation TEXTURE = new ResourceLocation(KusTweaksMod.MOD_ID, "textures/gui/kindling.png");

    public static final KindlingPotion INSTANCE = new KindlingPotion();

    protected KindlingPotion() {
        super(true, 0xC21202);
        setPotionName("kus_tweaks.effect.kindling");
        setRegistryName(KusTweaksMod.MOD_ID, "kindling");
        registerPotionAttributeModifier(KusAttributes.EXTRA_FIRE_DAMAGE_ATTRIBUTE, "fb02f24f-25ec-4a73-97fe-1014f7180194", 0.1, 0);
    }

    @Override
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        super.renderHUDEffect(effect, gui, x, y, z, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        super.renderInventoryEffect(effect, gui, x, y, z);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 16, 16, 16, 16);
    }
}
