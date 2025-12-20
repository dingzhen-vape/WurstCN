/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.wurstclient.WurstClient;

public final class ChunkAreaSetting
	extends EnumSetting<ChunkAreaSetting.ChunkArea>
{
	private static final Minecraft MC = WurstClient.MC;
	
	public ChunkAreaSetting(String name, String description)
	{
		super(name, description, ChunkArea.values(), ChunkArea.A11);
	}
	
	public ChunkAreaSetting(String name, String description, ChunkArea selected)
	{
		super(name, description, ChunkArea.values(), selected);
	}
	
	public ArrayList<ChunkAccess> getChunksInRange()
	{
		return getSelected().getChunksInRange();
	}
	
	public boolean isInRange(ChunkPos pos)
	{
		return getSelected().isInRange(pos);
	}
	
	public enum ChunkArea
	{
		A3("3x3区块", 1),
		A5("5x5区块", 2),
		A7("7x7区块", 3),
		A9("9x9区块", 4),
		A11("11x11区块", 5),
		A13("13x13区块", 6),
		A15("15x15区块", 7),
		A17("17x17区块", 8),
		A19("19x19区块", 9),
		A21("21x21区块", 10),
		A23("23x23区块", 11),
		A25("25x25区块", 12),
		A27("27x27区块", 13),
		A29("29x29区块", 14),
		A31("31x31区块", 15),
		A33("33x33区块", 16);
		
		private final String name;
		private final int chunkRange;
		
		private ChunkArea(String name, int chunkRange)
		{
			this.name = name;
			this.chunkRange = chunkRange;
		}
		
		public ArrayList<ChunkAccess> getChunksInRange()
		{
			ChunkPos center = MC.player.chunkPosition();
			ArrayList<ChunkAccess> chunksInRange = new ArrayList<>();
			
			for(int x = center.x - chunkRange; x <= center.x + chunkRange; x++)
				for(int z = center.z - chunkRange; z <= center.z
					+ chunkRange; z++)
				{
					ChunkAccess chunk = MC.level.getChunk(x, z);
					if(chunk instanceof EmptyLevelChunk)
						continue;
					
					chunksInRange.add(chunk);
				}
			
			return chunksInRange;
		}
		
		public boolean isInRange(ChunkPos pos)
		{
			ChunkPos center = MC.player.chunkPosition();
			return Math.abs(pos.x - center.x) <= chunkRange
				&& Math.abs(pos.z - center.z) <= chunkRange;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
