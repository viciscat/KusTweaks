package io.github.viciscat.kustweaks;

import arekkuusu.enderskills.common.skill.DynamicModifier;
import arekkuusu.enderskills.common.skill.ModEffects;
import arekkuusu.enderskills.common.skill.SkillHelper;
import baubles.api.BaublesApi;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.srpcotesia.handler.EnhancedMobHandler;
import com.srpcotesia.init.SRPCAttributes;
import com.srpcotesia.util.ParasiteInteractions;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import io.github.viciscat.kustweaks.block.HyaloclastiteBlock;
import io.github.viciscat.kustweaks.block.RespawnAnchorBlock;
import io.github.viciscat.kustweaks.potion.DrownierPotion;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CommonEvents {

    private static final DynamicModifier NORMAL_ATTRIBUTE = new DynamicModifier(
            "411ce9ac-5bcf-4a7f-8682-2f1a0e8d88aa",
            KusTweaksMod.MOD_ID + ":ender_skills_invulnerable",
            KusAttributes.HEAL_PERCENT_DAMAGE,
            Constants.AttributeModifierOperation.ADD
    );
    private static final DynamicModifier TRUE_ATTRIBUTE = new DynamicModifier(
            "411ce9ac-5bcf-4a7f-8682-2f1a0e8d88aa",
            KusTweaksMod.MOD_ID + ":ender_skills_invulnerable",
            KusAttributes.TRUE_HEAL_PERCENT_DAMAGE,
            Constants.AttributeModifierOperation.ADD
    );
    
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

        KusItems.registerItems(event.getRegistry());
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
        if (event.side == Side.CLIENT) return;
        if (event.player.getHealth() == event.player.getMaxHealth()) return;
        float flatHealAmount = (float) event.player.getAttributeMap().getAttributeInstance(KusAttributes.HEAL_AMOUNT_PER_TICK).getAttributeValue();
        float percentHealAmount = (float) event.player.getAttributeMap().getAttributeInstance(KusAttributes.HEAL_PERCENT_MAX_HEALTH_PER_TICK).getAttributeValue();

        event.player.heal(flatHealAmount + percentHealAmount * event.player.getMaxHealth());
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if ((event.getSource().isFireDamage() || KusConfig.kindlingDamageSources.contains(event.getSource().damageType))&& !event.getSource().isDamageAbsolute()) {
            double multiplier = KusAttributes.getAttributeOrDefault(event.getEntityLiving(), KusAttributes.EXTRA_FIRE_DAMAGE_ATTRIBUTE) + 1;
            event.setAmount((float) (event.getAmount() * multiplier));
        }
        float damage = event.getAmount();
        Entity entity = event.getEntity();
        Entity src = event.getSource().getImmediateSource();

        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase mob = (EntityLivingBase) entity;

        //logger.debug(damage + " " + event.getSource().getDamageType() + " pre armor");
        if (src instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) src;
            float damagePercentOOF = (float) player.getAttributeMap().getAttributeInstance(KusAttributes.OUT_OF_WORLD_PERCENTAGE).getAttributeValue();
            float damagePercentDirect = (float) player.getAttributeMap().getAttributeInstance(KusAttributes.DIRECT_DAMAGE_PERCENTAGE).getAttributeValue();
            if (damagePercentOOF > 0.0F){
                entity.hurtResistantTime = 0;
                //logger.debug(damage + ", " + damagePercent + ", " + damage * damagePercent);
                mob.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage * damagePercentOOF);
            }
            if (damagePercentDirect > 0.0F){
                entity.hurtResistantTime = 0;
                //logger.debug(damage + ", " + damagePercent + ", " + damage * damagePercent);
                mob.setLastAttackedEntity(null);
                mob.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage * damagePercentDirect);
            }
        }
        if (event.getSource().getDamageType().equals("drown")) {
            PotionEffect effect = mob.getActivePotionEffect(DrownierPotion.INSTANCE);
            if (effect != null) {
                event.setAmount(event.getAmount() * (1.1f + effect.getAmplifier() * 0.1f));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!ModEffects.INVULNERABLE.isClientWorld(entity)) {
            if (SkillHelper.isActive(entity, ModEffects.INVULNERABLE)) {
                NORMAL_ATTRIBUTE.apply(entity, 1);
                TRUE_ATTRIBUTE.apply(entity, 1);
            } else {
                NORMAL_ATTRIBUTE.remove(entity);
                TRUE_ATTRIBUTE.remove(entity);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityAttackedExplosion(LivingHurtEvent event) { // A bit too lazy to merge with existing method so good enoughMore actions
        float damage = event.getAmount();
        Entity entity = event.getEntity();
        Entity src = event.getSource().getTrueSource();

        if (!(entity instanceof EntityLivingBase)) return;

        if (src instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) src;
            float damagePercentExplosion = (float) KusAttributes.getAttributeOrDefault(player, KusAttributes.EXPLOSION_DAMAGE);

            if (damagePercentExplosion > 0.0F && event.getSource().isExplosion()) {
                // Apply custom effect for explosion damage
                float modifiedDamage = damage * (1.0F + damagePercentExplosion);
                event.setAmount(modifiedDamage);
            }
        }
    }

    @SubscribeEvent
    public static void livingDamaged(LivingDamageEvent event) {
        float damage = event.getAmount();
        Entity src = event.getSource().getImmediateSource();
        //logger.debug(damage + " " + event.getSource().getDamageType() + " post armor");
        if (src instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) src;
            //logger.debug(src.getName() + " dealt " + damage);
            float healPercent = (float) player.getAttributeMap().getAttributeInstance(KusAttributes.LIFE_STEAL_PERCENTAGE).getAttributeValue();
            player.heal(damage * healPercent);
        }
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            float healPercent = (float) player.getAttributeMap().getAttributeInstance(KusAttributes.HEAL_PERCENT_DAMAGE).getAttributeValue();
            player.heal(damage * healPercent);
        }
    }

    // Code taken from SRP cotesia by roguetictac with permission
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingAttack(LivingAttackEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (victim.world.isRemote) return;
        DamageSource source = event.getSource();
        Entity e = source.getTrueSource();

        if (!source.isMagicDamage() || victim instanceof EntityPMalleable || !(e instanceof EntityLivingBase)) return;

        EntityLivingBase living = (EntityLivingBase) e;

        if (ParasiteInteractions.isParasite(living) && ParasiteInteractions.isParasite(victim)) return;

        float pd = (float) KusAttributes.getAttributeOrDefault(living, KusAttributes.MAGIC_PERCENT_DAMAGE);
        float minDamage = event.getAmount() * pd;
        if (minDamage < 1.0E-7) return;
        minDamage = EnhancedMobHandler.blockMiniDamage(living, victim, minDamage);
        if (minDamage < 1.0E-7) return;
        SRPCAttributes.trueDamage(living, victim, source, minDamage);





    }

    @SubscribeEvent
    public static void onEntityConstructing(EntityEvent.EntityConstructing event) {
        Entity eventEntity = event.getEntity();
        if (eventEntity instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) eventEntity;
            AbstractAttributeMap attributeMap = entity.getAttributeMap();
            for (IAttribute attribute : KusAttributes.ALL_ATTRIBUTES) {
                attributeMap.registerAttribute(attribute);
            }
        } else if (eventEntity instanceof EntityElderGuardian) {
            EntityElderGuardian elderGuardian = (EntityElderGuardian) eventEntity;
            elderGuardian.addPotionEffect(new PotionEffect(PotionRecoil.INSTANCE, Integer.MAX_VALUE, 0, false, false));
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

    private static int roundAverage(float value)
    {
        double floor = Math.floor(value);
        return (int) floor + (Math.random() < value - floor ? 1 : 0);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperienceDropped(LivingExperienceDropEvent event) {
        EntityPlayer player = event.getAttackingPlayer();
        if (player == null) return;
        int experience = event.getDroppedExperience();
        if (BaublesApi.isBaubleEquipped(player, KusItems.itemExperienceAbsorber) >= 0) {
            event.setCanceled(true);
            ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, player);

            if (!itemstack.isEmpty() && itemstack.isItemDamaged())
            {
                float ratio = itemstack.getItem().getXpRepairRatio(itemstack);
                int i = Math.min(roundAverage(experience * ratio), itemstack.getItemDamage());
                experience -= roundAverage(i / ratio);
                itemstack.setItemDamage(itemstack.getItemDamage() - i);
            }

            if (experience > 0)
            {
                player.addExperience(experience);
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
        event.getDrops().add(event.getEntityLiving().dropItem(KusItems.itemEvilEssence, 1));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void removeExperienceCursedMob(LivingExperienceDropEvent event) {
        NBTTagCompound data = event.getEntityLiving().getEntityData();
        if (data.hasKey("CursedEarth") || data.getBoolean("CannotDropLoot"))
            event.setCanceled(true);
    }

    private static void removeSoulboundModifiers(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            NBTTagList tagList = TagUtil.getBaseModifiersTagList(stack);
            int index = TinkerUtil.getIndexInList(tagList, "soulbound");
            if (index >= 0) tagList.removeTag(index);
            index = TinkerUtil.getIndexInList(tagList, "soulbound_armor");
            if (index >= 0) tagList.removeTag(index);

            tagList = TagUtil.getModifiersTagList(stack);
            index = TinkerUtil.getIndexInList(tagList, "soulbound");
            if (index >= 0) tagList.removeTag(index);
            index = TinkerUtil.getIndexInList(tagList, "soulbound_armor");
            if (index >= 0) tagList.removeTag(index);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if(!event.isWasDeath() || event.isCanceled()) {
            return;
        }
        if(event.getOriginal() == null || event.getEntityPlayer() == null || event.getEntityPlayer() instanceof FakePlayer) {
            return;
        }
        if(event.getEntityPlayer().getEntityWorld().getGameRules().getBoolean("keepInventory")) {
            return;
        }
        removeSoulboundModifiers(event.getEntityPlayer());
    }
}
