package net.shadowfacts.inductioncharger;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author shadowfacts
 */
@Mod(modid = InductionCharger.modId, name = InductionCharger.name, version = InductionCharger.version, dependencies = "required-after:shadowmc@[3.4.0,);required-after:tesla;")
public class InductionCharger {

	public static final String modId = "inductioncharger";
	public static final String name = "Induction Charger";
	public static final String version = "@VERSION@";

	@Mod.Instance(modId)
	public static InductionCharger instance;

//	Content
	public BlockCharger charger = new BlockCharger();

	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	private void preInitClient(FMLPreInitializationEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCharger.class, new TESRCharger());
	}

	@Mod.EventBusSubscriber
	public static class EventHandler {

		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			event.getRegistry().register(instance.charger);
			GameRegistry.registerTileEntity(TileEntityCharger.class, "InductionCharger.charger");
		}

		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().register(new ItemBlock(instance.charger).setRegistryName(instance.charger.getRegistryName()));
		}

		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(instance.charger), 0, new ModelResourceLocation(modId + ":charger", "inventory"));
		}

	}

}
