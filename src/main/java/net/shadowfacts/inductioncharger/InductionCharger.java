package net.shadowfacts.inductioncharger;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

//	Content
	public BlockCharger charger;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		charger = new BlockCharger();
		GameRegistry.register(charger);
		GameRegistry.register(new ItemBlock(charger).setRegistryName(charger.getRegistryName()));
		GameRegistry.registerTileEntityWithAlternatives(TileEntityCharger.class, "InductionCharger.charger");

		if (event.getSide() == Side.CLIENT) {
			preInitClient();
		}
	}

	@SideOnly(Side.CLIENT)
	private void preInitClient() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCharger.class, new TESRCharger());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(charger), 0, new ModelResourceLocation(modId + ":charger", "inventory"));
	}

}
