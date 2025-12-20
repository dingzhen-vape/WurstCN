/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.nukers;

import net.wurstclient.settings.EnumSetting;

public final class NukerModeSetting
	extends EnumSetting<NukerModeSetting.NukerMode>
{
	public NukerModeSetting()
	{
		super("模式",
			"\u00a7lNormal\u00a7r 模式简单地破坏你周围的方块。\n\n"
				+ "\u00a7lID\u00a7r 模式只破坏选定的方块类型。"
				+ " 左键点击一个方块以选择它。\n\n"
				+ "\u00a7lMultiID\u00a7r 模式只破坏MultiID列表中的"
				+ " 您的MultiID列表中的方块类型。\n\n"
				+ "\u00a7lSmash\u00a7r 模式只破坏可以瞬间破坏的方块"
				+ " (例如高草)。",
			NukerMode.values(), NukerMode.NORMAL);
	}
	
	public enum NukerMode
	{
		NORMAL("普通"),
		ID("ID"),
		MULTI_ID("MultiID"),
		SMASH("Smash");
		
		private final String name;
		
		private NukerMode(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
