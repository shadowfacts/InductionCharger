package net.shadowfacts.inductioncharger;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowfacts.shadowmc.block.BlockTE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author shadowfacts
 */
public class BlockCharger extends BlockTE<TileEntityCharger> {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 3/16d, 1);

	public BlockCharger() {
		super(Material.ROCK, "charger");

		setHardness(0.7f);

		setCreativeTab(CreativeTabs.MISC);

		setDefaultState(getDefaultState()
				.withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Nonnull
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, getDirection(pos, placer).getOpposite());
	}

	private EnumFacing getDirection(BlockPos pos, EntityLivingBase entity) {
		return EnumFacing.getFacingFromVector(
				(float)(entity.posX - pos.getX()),
				0,
				(float)(entity.posZ - pos.getZ()));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta |= state.getValue(FACING).getHorizontalIndex();
		meta |= 0b1000; // set the 4th bit indicating the new metadata format
		return meta;
	}

	@Nonnull
	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta) {
		if ((meta >> 3) == 1) {
			meta = meta & ~(1 << 3); // unset the 4th bit
		} else {
			meta -= 2; // old index instead of horizontal index
		}
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityCharger te = getTileEntity(world, pos);
			if (te.getStackInSlot(0) == null) { // insert
				if (heldItem != null && te.insertItem(0, heldItem, true) != heldItem) {
					player.setHeldItem(hand, te.insertItem(0, heldItem, false));
					te.save();
					return true;
				}
			} else { // extract
				if (player.isSneaking()) {
					ItemStack stack = te.extractItem(0, 1, false);
					boolean result = player.inventory.addItemStackToInventory(stack);
					if (!result) {
						player.dropItem(stack, false);
					}
					te.save();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOX;
	}

	@Nonnull
	@Override
	public TileEntityCharger createTileEntity(World world, IBlockState state) {
		return new TileEntityCharger();
	}

	@Override
	public Class<TileEntityCharger> getTileEntityClass() {
		return TileEntityCharger.class;
	}

}
