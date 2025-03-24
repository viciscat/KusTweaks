package io.github.viciscat.kustweaks.mixin;

import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.srpcotesia.capability.CapabilityParasitePlayer;
import com.srpcotesia.capability.ParasitePlayer;
import com.tmtravlr.potioncore.potion.PotionMagicShield;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import com.tmtravlr.potioncore.potion.PotionRevival;
import electroblob.wizardry.registry.WizardryItems;
import io.github.viciscat.kustweaks.KusConfig;
import io.github.viciscat.kustweaks.KusTweaksMod;
import io.github.viciscat.kustweaks.MixinMethods;
import io.github.viciscat.kustweaks.injected.ExtendedPlayerMP;
import io.github.viciscat.wither.DecayPotion;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTracker;removePlayerFromTrackers(Lnet/minecraft/entity/player/EntityPlayerMP;)V"))
    private void recreatePlayerEntity(CallbackInfoReturnable<EntityPlayerMP> cir, @Local(argsOnly = true) EntityPlayerMP playerIn, @Local(argsOnly = true) LocalIntRef dimension) {
        if (!playerIn.hasSpawnDimension()) return;
        int newDim = playerIn.getSpawnDimension();
        if (KusConfig.respawnDisabledDimensions.contains(DimensionType.getById(newDim))) return;
        WorldServer world = server.getWorld(newDim);
        if (world == null) return;
        BlockPos bedLocation = playerIn.getBedLocation(newDim);
        if (bedLocation == null) return;
        if (world.getBlockState(bedLocation).getBlock() != KusTweaksMod.respawnAnchorBlock) return;
        dimension.set(newDim);

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

            EntityLiving entityIronGolem = MixinMethods.getGolem(entityplayermp);
            entityIronGolem.setPosition(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ);
            MixinMethods.addEffectsToGolem(entityIronGolem);
            entityplayermp.getServerWorld().spawnEntity(entityIronGolem);

            MixinMethods.addEffectsToPlayer(entityplayermp);

            entityplayermp.sendMessage(new TextComponentString("<§k???§r> S§ke§rems you ne§ke§rd s§ko§rme §l§ehelp§r!"));

        }
    }
}
