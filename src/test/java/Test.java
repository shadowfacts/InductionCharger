import net.darkhax.tesla.api.implementation.BaseTeslaContainer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.shadowfacts.inductioncharger.adapter.FUTeslaAdapter;
import net.shadowfacts.shadowmc.item.ItemBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author shadowfacts
 */
@Mod(modid = "test")
public class Test {

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ItemTest item = GameRegistry.register(new ItemTest());
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("test:test", "inventory"));

		ItemFUTest fuItem = GameRegistry.register(new ItemFUTest());
		ModelLoader.setCustomModelResourceLocation(fuItem, 0, new ModelResourceLocation("test:test", "inventory"));
	}

	public static class ItemTest extends ItemBase {

		public ItemTest() {
			super("test");
			setHasSubtypes(true);
		}

		@Override
		public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
			ITeslaHolder tesla = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
			tooltip.add(String.format("%d / %d Tesla", tesla.getStoredPower(), tesla.getCapacity()));
		}

		@Override
		public void getSubItems(@Nonnull Item item, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
			subItems.add(new ItemStack(this));
			ItemStack stack2 = new ItemStack(this);
			stack2.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null).givePower(1000, false);
		}

		@Override
		public double getDurabilityForDisplay(ItemStack stack) {
			ITeslaHolder tesla = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
			return tesla.getStoredPower() / (double)tesla.getCapacity();
		}

		@Nonnull
		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
			return new TestCapabilities();
		}
	}

	public static class TestCapabilities implements ICapabilitySerializable<NBTTagCompound> {

		private BaseTeslaContainer tesla = new BaseTeslaContainer(1000, 1000, 1000);

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
			if (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
				return (T)tesla;
			}
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return tesla.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			tesla.deserializeNBT(tag);
		}
	}

	public static class ItemFUTest extends ItemBase {

		public ItemFUTest() {
			super("fuTest");
			setHasSubtypes(true);
		}

		@Override
		public void addInformation(@Nonnull  ItemStack stack, @Nonnull EntityPlayer player, @Nonnull List<String> tooltip, boolean advanced) {
			IEnergyStorage fu = stack.getCapability(CapabilityEnergy.ENERGY, null);
			tooltip.add(String.format("%d / %d FU", fu.getEnergyStored(), fu.getMaxEnergyStored()));
		}

		@Override
		public void getSubItems(@Nonnull Item item, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
			subItems.add(new ItemStack(this));
			ItemStack stack2 = new ItemStack(this);
			stack2.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(1000, false);
		}

		@Nonnull
		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
			return new FUCapabilities();
		}
	}

	public static class FUCapabilities implements ICapabilitySerializable<NBTTagCompound> {

		private TestFUStorage storage = new TestFUStorage();

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityEnergy.ENERGY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityEnergy.ENERGY ? (T)storage : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return storage.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			storage.deserializeNBT(tag);
		}

	}

	private static class TestFUStorage extends EnergyStorage {

		public TestFUStorage() {
			super(1000);
		}

		private NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("energy", energy);
			tag.setInteger("capacity", capacity);
			tag.setInteger("maxReceive", maxReceive);
			tag.setInteger("maxExtract", maxExtract);
			return tag;
		}

		private void deserializeNBT(NBTTagCompound tag) {
			energy = tag.getInteger("energy");
			capacity = tag.getInteger("capacity");
			maxReceive = tag.getInteger("maxReceive");
			maxExtract = tag.getInteger("maxExtract");
		}

	}

}
