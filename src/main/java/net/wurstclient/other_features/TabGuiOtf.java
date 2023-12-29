/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.EnumSetting;

@SearchTags({"tab gui", "HackMenu", "hack menu", "SideBar", "side bar",
	"blocks movement combat render chat fun items other"})
@DontBlock
public final class TabGuiOtf extends OtherFeature
{
	private final EnumSetting<Status> status =
		new EnumSetting<>("状态", Status.values(), Status.DISABLED);
	
	public TabGuiOtf()
	{
		super("TabGUI", "让你能够在玩游戏时快速切换黑客功能。\n" + "使用方向键来导航。\n\n"
			+ "将\u00a76HackList \u00a76位置\u00a7r设置改为\u00a76右边\u00a7r，以防止TabGUI与HackList重叠。");
		
		addSetting(status);
	}
	
	public boolean isHidden()
	{
		return status.getSelected() == Status.DISABLED;
	}
	
	private enum Status
	{
		ENABLED("启用"),
		DISABLED("禁用");
		
		private final String name;
		
		private Status(String name)
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
