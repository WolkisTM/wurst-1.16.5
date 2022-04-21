/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.wurstclient.WurstClient;

import java.util.ArrayList;
import java.util.stream.Stream;

public enum BlockUtils
{
	;
	
	private static final Minecraft MC = WurstClient.MC;
	
	public static BlockState getState(BlockPos pos)
	{
		return MC.world.getBlockState(pos);
	}
	
	public static Block getBlock(BlockPos pos)
	{
		return getState(pos).getBlock();
	}
	
	public static int getId(BlockPos pos)
	{
		return Block.getStateId(getState(pos));
	}
	
	public static String getName(BlockPos pos)
	{
		return getName(getBlock(pos));
	}
	
	public static String getName(Block block)
	{
		return Registry.BLOCK.getKey(block).toString();
	}
	
	public static Block getBlockFromName(String name)
	{
		try
		{
			return Registry.BLOCK.getOrDefault(new ResourceLocation(name));
			
		}catch(ResourceLocationException e)
		{
			return Blocks.AIR;
		}
	}
	
	public static float getHardness(BlockPos pos)
	{
		return getState(pos).getPlayerRelativeBlockHardness(MC.player, MC.world, pos);
	}
	
	private static VoxelShape getOutlineShape(BlockPos pos)
	{
		return getState(pos).getShape(MC.world, pos);
	}
	
	public static AxisAlignedBB getBoundingBox(BlockPos pos)
	{
		return getOutlineShape(pos).getBoundingBox().offset(pos);
	}
	
	public static boolean canBeClicked(BlockPos pos)
	{
		return getOutlineShape(pos) != VoxelShapes.empty();
	}
	
	public static ArrayList<BlockPos> getAllInBox(BlockPos from, BlockPos to)
	{
		ArrayList<BlockPos> blocks = new ArrayList<>();
		
		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()),
			Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()),
			Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
		
		for(int x = min.getX(); x <= max.getX(); x++)
			for(int y = min.getY(); y <= max.getY(); y++)
				for(int z = min.getZ(); z <= max.getZ(); z++)
					blocks.add(new BlockPos(x, y, z));
				
		return blocks;
	}

	public static Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to)
	{
		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()),
				Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()),
				Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

		Stream<BlockPos> stream = Stream.<BlockPos> iterate(min, pos -> {

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			x++;

			if(x > max.getX())
			{
				x = min.getX();
				y++;
			}

			if(y > max.getY())
			{
				y = min.getY();
				z++;
			}

			if(z > max.getZ())
				throw new IllegalStateException("Stream limit didn't work.");

			return new BlockPos(x, y, z);
		});

		int limit = (max.getX() - min.getX() + 1)
				* (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);

		return stream.limit(limit);
	}
}
