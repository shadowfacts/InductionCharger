package net.shadowfacts.inductioncharger.adapter;

import cofh.api.energy.IEnergyStorage;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;

/**
 * @author shadowfacts
 */
public class RFTeslaAdapter implements IEnergyStorage {

	private final BaseTeslaContainer tesla;

	public RFTeslaAdapter(BaseTeslaContainer tesla) {
		this.tesla = tesla;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int)tesla.givePower(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return (int)tesla.takePower(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return (int)tesla.getStoredPower();
	}

	@Override
	public int getMaxEnergyStored() {
		return (int)tesla.getCapacity();
	}

}
