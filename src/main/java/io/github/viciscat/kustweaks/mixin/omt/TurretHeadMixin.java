package io.github.viciscat.kustweaks.mixin.omt;

import io.github.viciscat.kustweaks.KusItems;
import io.github.viciscat.kustweaks.entity.EntityTurretHeadHitbox;
import io.github.viciscat.kustweaks.injected.ExtendedTurretHead;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import omtteam.openmodularturrets.util.TurretType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static omtteam.omlib.util.inventory.InvUtil.getStackSize;

@Mixin(value = TurretHead.class, remap = false)
public abstract class TurretHeadMixin extends TileEntityBase implements ExtendedTurretHead {
	@Shadow
	private EnumFacing turretBase;
	@Shadow
	protected boolean hasSetSide;
	@Shadow
	public boolean shouldConceal;

    @Shadow protected TurretBase base;

	@Shadow
	@Nonnull
	protected abstract SoundEvent getLaunchSoundEffect();

	@Shadow
	protected int ticks;
    @Shadow public Entity target;

	@Shadow
	protected ItemStack getAmmoStack() {return null;};

	@Shadow
	public int getTurretBaseFireRate() {return 0;};

	@Shadow
	public abstract TurretType getTurretType();

	@Shadow
	public abstract double getTurretDamageAmpBonus();

	@Shadow
	protected abstract boolean requiresAmmo();

	@Shadow
	protected boolean autoFire;

	@Shadow
	public abstract double getActualTurretAccuracy();

	@Unique
	@Nullable
	private EntityTurretHeadHitbox kusTweaks$hitbox = null;
	@Unique
	@Nullable
	private EnumFacing kusTweaks$hitboxFacing = null;

	@Inject(method = "setSide", at = @At("HEAD"))
	private void updateHitbox(CallbackInfoReturnable<Boolean> cir) {
		if (getWorld().isRemote || isInvalid()) return;
		if (kusTweaks$hitbox == null || kusTweaks$hitbox.isDead) kusTweaks$createHitbox();
		if (shouldConceal) {
			if (kusTweaks$hitboxFacing != null) {
				kusTweaks$hitboxFacing = null;
				kusTweaks$hitbox.conceal();
			}
		} else if (hasSetSide && turretBase != null && turretBase != kusTweaks$hitboxFacing) {
			kusTweaks$hitboxFacing = turretBase;
			kusTweaks$hitbox.setFacing(kusTweaks$hitboxFacing);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		kusTweaks$removeHitbox();
	}

	@Override
	public void kusTweaks$setHitbox(EntityTurretHeadHitbox hitbox) {
		this.kusTweaks$hitbox = hitbox;
	}

	@Override
	public @Nullable EntityTurretHeadHitbox kusTweaks$getHitbox() {
		return kusTweaks$hitbox;
	}

	@Unique
	private void kusTweaks$createHitbox() {
		if (getWorld().isRemote) return;
		if (kusTweaks$hitbox != null) kusTweaks$hitbox.setDead();
		kusTweaks$hitbox = new EntityTurretHeadHitbox(getWorld(), (TurretHead) (Object) this);
		kusTweaks$hitbox.setPosition(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
		getWorld().spawnEntity(kusTweaks$hitbox);
	}

	@Unique
	private void kusTweaks$removeHitbox() {
		if (kusTweaks$hitbox == null) return;
		kusTweaks$hitbox.setDead();
		kusTweaks$hitbox = null;
		kusTweaks$hitboxFacing = null;
	}

	@Unique
	protected int kusTweaks$getDamage() {
		return (int) (getTurretType().getSettings().baseDamage * (1 + getTurretDamageAmpBonus() * TurretHeadUtil.getAmpLevel(base)));
	}

	@Override
	public int kusTweaks$getBulwarkUpgradeCount() {
		int level = 0;

		if (base == null) {
			return level;
		}

		int tier = base.getTier();

		if (tier == 1) {
			return level;
		}

		int slotId = 11;
		if (base.getInventory().getStackInSlot(slotId) != ItemStack.EMPTY) {
			Item item = base.getInventory().getStackInSlot(slotId).getItem();
			if (item == KusItems.itemBulwarkUpgrade) {
				level += getStackSize(base.getInventory().getStackInSlot(slotId));
			}
		}

		slotId++;
		if (base.getInventory().getStackInSlot(slotId) != ItemStack.EMPTY) {
			if (base.getInventory().getStackInSlot(slotId).getItem() == KusItems.itemBulwarkUpgrade) {
				level += getStackSize(base.getInventory().getStackInSlot(slotId));
			}
		}

		return level;
	}
}
