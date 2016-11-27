package net.shadowfacts.inductioncharger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

/**
 * @author shadowfacts
 */
public class TESRCharger extends TileEntitySpecialRenderer<TileEntityCharger> {

	@Override
	public void renderTileEntityAt(TileEntityCharger te, double x, double y, double z, float partialTicks, int destroyStage) {
		ItemStack stack = te.getStackInSlot(0);
		if (!stack.isEmpty()) {
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1f);
			GlStateManager.enableBlend();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5d, y + 0.5d + (Math.sin(Math.toRadians(te.ticks)) * 0.2), z + 0.5d);
			GL11.glRotated(te.ticks % 360 * 2, 0, 1, 0);

			IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);

			model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GROUND, false);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);

			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
	}

}
