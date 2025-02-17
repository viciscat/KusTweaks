package io.github.viciscat.kustweaks.mixin;

import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.srpcotesia.capability.CapabilityParasitePlayer;
import com.srpcotesia.capability.ParasitePlayer;
import com.tmtravlr.potioncore.potion.PotionMagicShield;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import com.tmtravlr.potioncore.potion.PotionRevival;
import electroblob.wizardry.registry.WizardryItems;
import io.github.viciscat.kustweaks.KusConfig;
import io.github.viciscat.kustweaks.KusTweaksMod;
import io.github.viciscat.kustweaks.injected.ExtendedPlayerMP;
import io.github.viciscat.wither.DecayPotion;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @WrapOperation(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z"))
    private boolean allowRespawnWithRespawnAnchor(WorldProvider instance, Operation<Boolean> original, @Local World world, @Local(argsOnly = true) EntityPlayerMP player, @Local(argsOnly = true) int dimension, @Local(argsOnly = true) boolean conqueredEnd) {
        if (KusConfig.respawnDisabledDimensions.contains(DimensionType.getById(dimension))) return false;
        if (conqueredEnd || original.call(instance)) return true;
        BlockPos bedLocation = player.getBedLocation(dimension);
        //noinspection ConstantValue
        if (bedLocation == null) return false;
        return world.getBlockState(bedLocation).getBlock() == KusTweaksMod.respawnAnchorBlock;
    }

    @Unique
    private final Map<UUID, LongList> kus_tweaks$deathTimes = new HashMap<>();


    @Inject(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getChunkProvider()Lnet/minecraft/world/gen/ChunkProviderServer;"))
    public void randomizeSpawn(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd, CallbackInfoReturnable<EntityPlayerMP> cir, @Local(ordinal = 1) EntityPlayerMP entityplayermp) {
        ExtendedPlayerMP wrapped = ExtendedPlayerMP.of(playerIn);

        if (wrapped.kus_tweaks$getRandomRespawn()) {
            wrapped.kus_tweaks$setRandomRespawn(false);
            int rand = 70;
            int halfRand = rand / 2;
            Random random = entityplayermp.world.rand;
            BlockPos topSolidOrLiquidBlock = entityplayermp.world.getTopSolidOrLiquidBlock(entityplayermp.getPosition().add(
                    halfRand - random.nextInt(rand),
                    0,
                    halfRand - random.nextInt(rand)));
            entityplayermp.setPosition(topSolidOrLiquidBlock.getX() + 0.5, topSolidOrLiquidBlock.getY() + 0.5, topSolidOrLiquidBlock.getZ() + 0.5);
        }

        long timeMillis = System.currentTimeMillis();
        LongList longs = kus_tweaks$deathTimes.computeIfAbsent(entityplayermp.getUniqueID(), uuid -> new LongArrayList(5));
        longs.removeIf(l -> timeMillis - l > 1000 * 5 * 60);
        longs.add(timeMillis);


        if (longs.size() > 3) {
            longs.clear();

            EntityLiving entityIronGolem = kusTweaks$getGolem(entityplayermp);
            entityIronGolem.setPosition(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ);
            kusTweaks$addEffectsToGolem(entityIronGolem);
            entityplayermp.getServerWorld().spawnEntity(entityIronGolem);

            kusTweaks$addEffectsToPlayer(entityplayermp);

            entityplayermp.sendMessage(new TextComponentString("<§k???§r> S§ke§rems you ne§ke§rd s§ko§rme §l§ehelp§r!"));

        }
    }

    @Unique
    private static EntityLiving kusTweaks$getGolem(EntityPlayerMP entity) {
        ParasitePlayer capability = entity.getCapability(CapabilityParasitePlayer.PARASITE_PLAYER_CAPABILITY, null);
        if (capability == null || !capability.isParasite()) {
            return new EntityIronGolem(entity.getServerWorld());
        } else {
            return new EntityHiGolem(entity.getServerWorld());
        }
    }

    @Unique
    private static void kusTweaks$addEffectsToGolem(EntityLivingBase entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(DecayPotion.INSTANCE      , 30*20*60, 0, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE     , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.SPEED          , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.HASTE          , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(PotionRecoil.INSTANCE     , 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(PotionMagicShield.INSTANCE, 30*20*60, 1, false, false));
        entityLiving.addPotionEffect(new PotionEffect(SRPPotions.EPEL_E         , 30*20*60, 0, false, false));
        entityLiving.getEntityData().setBoolean("CannotDropLoot", true);
    }
    @Unique
    private static void kusTweaks$addEffectsToPlayer(EntityPlayerMP entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(PotionRevival.INSTANCE, 60*20, 0, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 15*20, 4, false, false));
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 60*20, 2, false, false));

        if (entityLiving.inventory.armorInventory.get(0).isEmpty()) entityLiving.inventory.armorInventory.set(0, new ItemStack(WizardryItems.spectral_boots));
        if (entityLiving.inventory.armorInventory.get(1).isEmpty()) entityLiving.inventory.armorInventory.set(1, new ItemStack(WizardryItems.spectral_leggings));
        if (entityLiving.inventory.armorInventory.get(2).isEmpty()) entityLiving.inventory.armorInventory.set(2, new ItemStack(WizardryItems.spectral_chestplate));
        if (entityLiving.inventory.armorInventory.get(3).isEmpty()) entityLiving.inventory.armorInventory.set(3, new ItemStack(WizardryItems.spectral_helmet));
    }
}
