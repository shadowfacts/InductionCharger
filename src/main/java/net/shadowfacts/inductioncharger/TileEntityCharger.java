package net.shadowfacts.inductioncharger;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowfacts.inductioncharger.adapter.FUTeslaAdapter;
import net.shadowfacts.inductioncharger.adapter.RFTeslaAdapter;
import net.shadowfacts.shadowmc.ShadowMC;
import net.shadowfacts.shadowmc.capability.CapHolder;
import net.shadowfacts.shadowmc.network.PacketRequestTEUpdate;
import net.shadowfacts.shadowmc.tileentity.BaseTileEntity;

/**
 * @author shadowfacts
 */
public class TileEntityCharger extends BaseTileEntity implements ITickable, IItemHandler, IEnergyReceiver {

	@CapHolder(capabilities = IItemHandler.class, sides = EnumFacing.DOWN)
	private ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected int getStackLimit(int slot, ItemStack stack) {
			return 1;
		}
	};

	@CapHolder(capabilities = {ITeslaHolder.class, ITeslaConsumer.class}, sides = EnumFacing.DOWN)
	private BaseTeslaContainer tesla = new BaseTeslaContainer(16000, 100, 100);

	@CapHolder(capabilities = net.minecraftforge.energy.IEnergyStorage.class, sides = EnumFacing.DOWN)
	private FUTeslaAdapter fu = new FUTeslaAdapter(tesla);

	private RFTeslaAdapter rf = new RFTeslaAdapter(tesla);

	private boolean firstTick = true;
	int ticks = 0;

	@Override
	public void update() {
		if (worldObj.isRemote) { // client
			if (firstTick) {
				firstTick = false;
				ShadowMC.network.sendToServer(new PacketRequestTEUpdate(this));
			}
			if (inventory.getStackInSlot(0) != null) {
				ticks++;
			}
		} else { // server
			if (inventory.getStackInSlot(0) != null) {
				ItemStack stack = inventory.getStackInSlot(0);
				boolean result = false;
				if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null) && tesla.getStoredPower() > 0) {
					ITeslaConsumer consumer = stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
					tesla.takePower(consumer.givePower(tesla.takePower(tesla.getStoredPower(), true), false), false);
					result = true;
				} else if (stack.getItem() instanceof IEnergyContainerItem && rf.getEnergyStored() > 0) {
					IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();
					rf.extractEnergy(container.receiveEnergy(stack, rf.extractEnergy(rf.getEnergyStored(), true), false), false);
					result = true;
				} else if (stack.hasCapability(CapabilityEnergy.ENERGY, null) && fu.getEnergyStored() > 0) {
					net.minecraftforge.energy.IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
					fu.extractEnergy(storage.receiveEnergy(rf.extractEnergy(rf.getEnergyStored(), true), false), false);
					result = true;
				}
				if (result) {
					save();
				}
			}
		}
	}

	void save() {
		markDirty();
		sync();
	}

	private EnumFacing getFacing() {
		return worldObj.getBlockState(pos).getValue(BlockCharger.FACING);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Inventory", inventory.serializeNBT());
		tag.setTag("Tesla", tesla.serializeNBT());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.deserializeNBT(tag.getCompoundTag("Inventory"));
		tesla.deserializeNBT(tag.getCompoundTag("Tesla"));
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == EnumFacing.DOWN || from == getFacing() || from == null;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		if (canConnectEnergy(from)) {
			return rf.getEnergyStored();
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if (canConnectEnergy(from)) {
			return rf.getMaxEnergyStored();
		}
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (canConnectEnergy(from)) {
			return rf.receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int getSlots() {
		return inventory.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null) || stack.getItem() instanceof IEnergyContainerItem || stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			return inventory.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = inventory.extractItem(slot, amount, simulate);

		if (getStackInSlot(0) == null) ticks = 0;

		return stack;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (facing == getFacing()) {
			if (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
				return true;
			} else if (capability == CapabilityEnergy.ENERGY) {
				return true;
			}
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing == getFacing()) {
			if (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
				return (T)tesla;
			} else if (capability == CapabilityEnergy.ENERGY) {
				return (T)fu;
			}
		}
		return super.getCapability(capability, facing);
	}

}
