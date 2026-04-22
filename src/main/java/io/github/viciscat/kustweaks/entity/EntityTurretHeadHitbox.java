package io.github.viciscat.kustweaks.entity;

import io.github.viciscat.kustweaks.injected.ExtendedTurretHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityTurretHeadHitbox extends EntityLivingBase implements IEntityOwnable {
	private static final DataParameter<Integer> FACING = EntityDataManager.createKey(EntityTurretHeadHitbox.class, DataSerializers.VARINT);
	private static final int TICKS_BEFORE_HEALING = 20 * 60;

	private static final TurretHead PLACEHOLDER = new TurretHead(999) {
		@Override
		protected boolean requiresAmmo() {
			return false;
		}

		@Override
		protected boolean requiresSpecificAmmo() {
			return false;
		}

		@Nullable
		@Override
		public ItemStack getAmmo() {
			return null;
		}

		@Nonnull
		@Override
		protected SoundEvent getLaunchSoundEffect() {
			return SoundEvents.AMBIENT_CAVE;
		}

		@Override
		public void update() {

		}
	};
	private @Nullable TurretHead turretHead;
	private int noTurretHeadFor;
	private int notAttackedFor;

	@SuppressWarnings("unused")
	public EntityTurretHeadHitbox(World worldIn) {
		this(worldIn, null);
	}

	public EntityTurretHeadHitbox(World world, @Nullable TurretHead turretHead) {
		super(world);
		this.turretHead = turretHead;
		conceal();
		noClip = true;
		setSize(0, 0);
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	public void conceal() {
		dataManager.set(FACING, -1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;
		if (turretHead == null) {
			TileEntity tileEntity = world.getTileEntity(getPosition());
			if (tileEntity instanceof TurretHead head) {
				ExtendedTurretHead extendedTurretHead = ExtendedTurretHead.of(head);
				if (extendedTurretHead.kusTweaks$getHitbox() == null) {
					extendedTurretHead.kusTweaks$setHitbox(this);
					noTurretHeadFor = 0;
				} else if (extendedTurretHead.kusTweaks$getHitbox() != this) {
					setDead();
					return;
				}
				turretHead = head;
			} else {
				if (noTurretHeadFor++ > 5) setDead();
				return;
			}
		}
		notAttackedFor++;
		BlockPos ownerPos = turretHead.getPos();
		setPosition(ownerPos.getX() + 0.5, ownerPos.getY() + 0.5, ownerPos.getZ() + 0.5);
		if (turretHead.isInvalid()) setDead();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30 + ExtendedTurretHead.of(turretHead).kusTweaks$getBulwarkUpgradeCount() * 30);
		if (notAttackedFor >= TICKS_BEFORE_HEALING) {
			heal(0.5f + ExtendedTurretHead.of(turretHead).kusTweaks$getBulwarkUpgradeCount() * 0.03125f);
		}
	}

	@Override
	public BlockPos getPosition() {
		return new BlockPos(posX, posY, posZ);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (dataManager.get(FACING) == -1) return false;
		if (super.attackEntityFrom(source, amount)) {
			notAttackedFor = 0;
			return true;
		}

		return false;
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		if (!this.dead || turretHead == PLACEHOLDER || world.isRemote) return; // dead check in case the forge event cancels it.
		if (turretHead == null) return;
		world.destroyBlock(turretHead.getPos(), true);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return List.of();
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {}

	@Nullable
	@Override
	public UUID getOwnerId() {
		if (turretHead == null) return null;
		return turretHead.getOwner() == null ? null : turretHead.getOwner().getUuid();
	}



	@Nullable
	@Override
	public Entity getOwner() {
		if (turretHead == null) return null;
		return turretHead.getOwner().getEntityPlayer();
	}

	public TurretHead getTurretHead() {
		return turretHead;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public EnumHandSide getPrimaryHand() {
		return EnumHandSide.RIGHT;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		getDataManager().register(FACING, EnumFacing.DOWN.getIndex());
	}

	@Override
	public void setDead() {
		super.setDead();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0f);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300); // temporary high number
	}

	public void setFacing(@Nonnull EnumFacing facing) {
		dataManager.set(FACING, facing.getIndex());
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (key == FACING) {
			if (dataManager.get(FACING) == -1) {
				setSize(0, 0);
			} else {
				setSize(0.9f, 0.9f);
				EnumFacing facing = EnumFacing.byIndex(dataManager.get(FACING));
				final double offset = 1/16d;
				setEntityBoundingBox(new AxisAlignedBB(getPositionVector(), getPositionVector()).grow(7/16d).expand(facing.getXOffset() * offset, facing.getYOffset() * offset, facing.getZOffset() * offset));
			}
		}
	}

	public boolean isConcealed() {
		return dataManager.get(FACING) == -1;
	}

	@Override
	protected void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setFloat("Health", this.getHealth());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("Health", 99))
		{
			this.setHealth(compound.getFloat("Health"));
		}
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
	}

	@Override
	public float getEyeHeight() {
		return 0;
	}

	@Override
	public double getMountedYOffset() {
		return height / 2;
	}
	@Override
	public void setPosition(double x, double y, double z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		if (this.isAddedToWorld() && !this.world.isRemote) this.world.updateEntityWithOptionalForce(this, false); // Forge - Process chunk registration after moving.
		float hw = this.width / 2.0F;
		float hh = this.height / 2.0F;
		this.setEntityBoundingBox(new AxisAlignedBB(x - hw, y - hh, z - hw, x + hw, y + hh, z + hw));
	}

	@Override
	public void resetPositionToBB() {
		AxisAlignedBB bb = getEntityBoundingBox();
		this.posX = (bb.minX + bb.maxX) / 2.0D;
		this.posY = (bb.minY + bb.maxY) / 2.0D;
		this.posZ = (bb.minZ + bb.maxZ) / 2.0D;
		if (this.isAddedToWorld() && !this.world.isRemote) this.world.updateEntityWithOptionalForce(this, false);
	}
}
