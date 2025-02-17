package io.github.viciscat.kustweaks;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import io.github.viciscat.kustweaks.block.HyaloclastiteBlock;
import io.github.viciscat.kustweaks.block.RespawnAnchorBlock;
import io.github.viciscat.kustweaks.injected.ExtendedPlayer;
import io.github.viciscat.kustweaks.item.ItemInfiniteAntiGravPack;
import io.github.viciscat.kustweaks.item.ItemMagnet;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new HyaloclastiteBlock());
        event.getRegistry().register(new RespawnAnchorBlock());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new HyaloclastiteBlock.Item(KusTweaksMod.hyaloclastiteBlock));
        //noinspection DataFlowIssue
        event.getRegistry().register(new ItemBlock(KusTweaksMod.respawnAnchorBlock).setRegistryName(KusTweaksMod.respawnAnchorBlock.getRegistryName()));
        event.getRegistry().register(new ItemMagnet());
        event.getRegistry().register(new ItemInfiniteAntiGravPack("infinite_antigravpack", 123456789));
        event.getRegistry().register(new Item().setRegistryName("evil_essence").setCreativeTab(CreativeTabs.MATERIALS).setTranslationKey(KusTweaksMod.MOD_ID + ".evil_essence"));
    }

    @SubscribeEvent
    public static void changeCobbleToHyaloclastite(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getState().equals(Blocks.COBBLESTONE.getDefaultState()) || event.getState().equals(Blocks.STONE.getDefaultState()))
            event.setNewState(KusTweaksMod.hyaloclastiteBlock.getDefaultState());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        DimensionType dimensionType = event.world.provider.getDimensionType();
        List<KusConfig.Debuff> debuffs = KusConfig.getDebuffs(dimensionType);
        for (EntityPlayer playerEntity : event.world.playerEntities) {
            if (playerEntity.dimension != dimensionType.getId() || playerEntity.capabilities.isCreativeMode) continue;
            int ticksExisted = playerEntity.ticksExisted;
            for (KusConfig.Debuff debuff : debuffs) {
                if (ticksExisted % debuff.getTimer() == 0) {
                    playerEntity.addPotionEffect(new PotionEffect(debuff.getPotionEffect()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        ExtendedPlayer.of(event.player).kusTweaks$setMagnetTicked(false);
    }

    @SubscribeEvent
    public static void onEntityAttacked(LivingHurtEvent event) {
        if ((event.getSource().isFireDamage() || KusConfig.kindlingDamageSources.contains(event.getSource().damageType))&& !event.getSource().isDamageAbsolute()) {
            double multiplier = event.getEntityLiving().getEntityAttribute(KusAttributes.EXTRA_FIRE_DAMAGE_ATTRIBUTE).getAttributeValue() + 1;
            event.setAmount((float) (event.getAmount() * multiplier));
        }
    }

    @SubscribeEvent
    public static void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            AbstractAttributeMap attributeMap = entity.getAttributeMap();
            for (IAttribute attribute : KusAttributes.ALL_ATTRIBUTES) {
                attributeMap.registerAttribute(attribute);
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @SubscribeEvent
    public static void onChunkLoaded(ChunkEvent.Load event) {
        if (event.getWorld().isRemote) return;
        Multimap<String, Entity> entityMultimap = MultimapBuilder.hashKeys().hashSetValues().build();
        for (ClassInheritanceMultiMap<Entity> entityList : event.getChunk().getEntityLists()) {
            for (Entity entity : entityList) {
                ResourceLocation key = EntityList.getKey(entity);
                if (key == null) continue;
                String string = key.toString();
                if (!KusConfig.chunkLoadEntityCap.containsKey(string)) continue;
                entityMultimap.put(string, entity);
            }
        }
        for (String s : entityMultimap.keySet()) {
            int i = KusConfig.chunkLoadEntityCap.getInt(s);
            if (entityMultimap.get(s).size() > i) {
                entityMultimap.get(s).forEach(event.getWorld()::removeEntity);
            }
        }
    }

    private static final Object2FloatMap<UUID> playerToHealth = new Object2FloatArrayMap<>(8);

    @SubscribeEvent
    public static void onEntityChangeDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity().getEntityWorld().isRemote) return;
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            playerToHealth.put(player.getUniqueID(), player.getHealth());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player.getEntityWorld().isRemote) return;
        if (!playerToHealth.containsKey(event.player.getUniqueID())) return;
        float aFloat = playerToHealth.removeFloat(event.player.getUniqueID());
        event.player.setHealth(aFloat);
    }

    // Evil Essence cursed earth shenanigans and respawn golem

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void modifyCursedMobDrops(LivingDropsEvent event) {
        NBTTagCompound data = event.getEntityLiving().getEntityData();
        if (data.getBoolean("CannotDropLoot")) {
            event.setCanceled(true);
            return;
        }
        if (!data.hasKey("CursedEarth")) return;
        event.getDrops().clear();
        event.getDrops().add(event.getEntityLiving().dropItem(KusTweaksMod.itemEvilEssence, 1));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void removeExperienceCursedMob(LivingExperienceDropEvent event) {
        NBTTagCompound data = event.getEntityLiving().getEntityData();
        if (data.hasKey("CursedEarth") || data.getBoolean("CannotDropLoot"))
            event.setCanceled(true);
    }
}
