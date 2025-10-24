package io.github.viciscat.kustweaks;

import io.github.viciscat.kustweaks.network.KusNetwork;
import io.github.viciscat.kustweaks.network.RandomRespawnPacket;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import techguns.client.models.armor.ModelAntiGravPack;
import techguns.client.render.AdditionalSlotRenderRegistry;
import techguns.client.render.RenderAdditionalSlotItem;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEvents {

	@SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerBlockItemModel(KusTweaksMod.hyaloclastiteBlock);
        registerBlockItemModel(KusTweaksMod.respawnAnchorBlock);

        registerItemModel(KusItems.itemMagnet);
        registerItemModel(KusItems.itemExperienceAbsorber);
        registerItemModel(KusItems.itemInfiniteAntiGravPack);
        registerItemModel(KusItems.itemEvilEssence);
        registerItemModel(KusItems.itemChaoticAmethyst);
        registerItemModel(KusItems.itemGlassHelmet);
        registerItemModel(KusItems.itemGlassChestplate);
        registerItemModel(KusItems.itemGlassLeggings);
        registerItemModel(KusItems.itemGlassBoots);
        AdditionalSlotRenderRegistry.register(KusItems.itemInfiniteAntiGravPack, new RenderAdditionalSlotItem(new ModelAntiGravPack(), new ResourceLocation(KusTweaksMod.MOD_ID, "textures/armors/infinite_antigravpack.png")));
    }
    
    private static void registerItemModel(Item item) {
        ResourceLocation registryName = item.getRegistryName();
        if (registryName == null) {
            KusTweaksMod.LOGGER.error("Couldn't find registry name for item {}", item.getClass().getName());
            return;
        }
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(registryName, "inventory"));
    }

    private static void registerBlockItemModel(Block block) {
        registerItemModel(Item.getItemFromBlock(block));
    }

    @SubscribeEvent
    public static void gameOverScreenRandomRespawn(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();
        if (!(gui instanceof GuiGameOver)) return;
        GuiButton button = new GuiButton(999, gui.width / 2 - 100, gui.height / 4 + 96, "Random Respawn") {

            private float t = 0.f;

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                super.drawButton(mc, mouseX, mouseY, partialTicks);
                t+=partialTicks;
                if (t >= 20.f && !enabled) enabled = true;
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (super.mousePressed(mc, mouseX, mouseY)) {
                    KusNetwork.NETWORK_WRAPPER.sendToServer(new RandomRespawnPacket());
                    mc.displayGuiScreen(null);
                    return true;
                }
                return false;
            }
        };
        button.enabled = false;
        event.getButtonList().add(1, button);
        for (int i = 2; i < event.getButtonList().size(); i++) {
            GuiButton button1 = event.getButtonList().get(i);
            button1.y += 24;
        }
    }

    @SubscribeEvent
    public static void playSound(PlaySoundEvent event){
        if (SoundEvents.ENTITY_LIGHTNING_THUNDER.getRegistryName().equals(event.getSound().getSoundLocation()) && event.getSound() instanceof PositionedSound){
            PositionedSound simpleSound = (PositionedSound) event.getSound();
            if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getPosition().distanceSq(simpleSound.getXPosF(), simpleSound.getYPosF(), simpleSound.getZPosF()) > 128*128){
                event.setResultSound(null);
            }
        }

    }
}
