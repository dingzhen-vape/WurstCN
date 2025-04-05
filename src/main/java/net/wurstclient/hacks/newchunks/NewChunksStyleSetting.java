/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.newchunks;

import net.wurstclient.settings.EnumSetting;

public final class NewChunksStyleSetting
	extends EnumSetting<NewChunksStyleSetting.Style>
{
	public NewChunksStyleSetting()
	{
		super("样式", Style.values(), Style.OUTLINE);
	}
	
	public static enum Style
	{
		OUTLINE("轮廓", new NewChunksOutlineRenderer()),
		SQUARE("方形", new NewChunksSquareRenderer());
		
		private final String name;
		private final NewChunksChunkRenderer chunkRenderer;
		
		private Style(String name, NewChunksChunkRenderer chunkRenderer)
		{
			this.name = name;
			this.chunkRenderer = chunkRenderer;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
		public NewChunksChunkRenderer getChunkRenderer()
		{
			return chunkRenderer;
		}
	}
}
