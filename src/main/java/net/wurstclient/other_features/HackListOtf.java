/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import java.awt.Color;
import java.util.Comparator;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.WurstClient;
import net.wurstclient.hack.Hack;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorSetting;
import net.wurstclient.settings.EnumSetting;

@SearchTags({"hack list", "HakList", "hak list", "HacksList", "hacks list",
	"HaxList", "hax list", "ArrayList", "array list", "ModList", "mod list",
	"CheatList", "cheat list"})
@DontBlock
public final class HackListOtf extends OtherFeature
{
	private final EnumSetting<Mode> mode = new EnumSetting<>("模式",
		"\u00a7l自动\u00a7r模式如果列表适合屏幕则显示整个列表。\n"
			+ "\u00a7l计数\u00a7r模式仅显示活跃黑客的数量。\n"
			+ "\u00a7l隐藏\u00a7r模式不显示任何内容。",
		Mode.values(), Mode.AUTO);
	
	private final EnumSetting<Position> position = new EnumSetting<>("位置",
		"黑客列表应显示在屏幕的哪一侧。"
			+ "\n当使用TabGUI时，请将其更改为\u00a7l右\u00a7r。",
		Position.values(), Position.LEFT);
	
	private final ColorSetting color = new ColorSetting("颜色",
		"黑客列表文本的颜色。\n"
			+ "仅当\u00a76彩虹UI\u00a7r被禁用时可见。",
		Color.WHITE);
	
	private final EnumSetting<SortBy> sortBy = new EnumSetting<>("排序依据",
		"确定黑客列表条目的排序方式。\n"
			+ "仅当\u00a76模式\u00a7r设置为\u00a76自动\u00a7r时可见。",
		SortBy.values(), SortBy.NAME);
	
	private final CheckboxSetting revSort =
		new CheckboxSetting("反转排序", false);
	
	private final CheckboxSetting animations = new CheckboxSetting("动画",
		"当启用时，条目在黑客列表中滑入滑出，以反映黑客的启用或禁用。",
		true);
	
	private SortBy prevSortBy;
	private Boolean prevRevSort;
	
	public HackListOtf()
	{
		super("黑客列表", "在屏幕上显示活跃黑客列表。");
		
		addSetting(mode);
		addSetting(position);
		addSetting(color);
		addSetting(sortBy);
		addSetting(revSort);
		addSetting(animations);
	}
	
	public Mode getMode()
	{
		return mode.getSelected();
	}
	
	public Position getPosition()
	{
		return position.getSelected();
	}
	
	public boolean isAnimations()
	{
		return animations.isChecked();
	}
	
	public Comparator<Hack> getComparator()
	{
		if(revSort.isChecked())
			return sortBy.getSelected().comparator.reversed();
		
		return sortBy.getSelected().comparator;
	}
	
	public boolean shouldSort()
	{
		try
		{
			// width of a renderName could change at any time
			// must sort the HackList every tick
			if(sortBy.getSelected() == SortBy.WIDTH)
				return true;
			
			if(sortBy.getSelected() != prevSortBy)
				return true;
			
			if(!Boolean.valueOf(revSort.isChecked()).equals(prevRevSort))
				return true;
			
			return false;
			
		}finally
		{
			prevSortBy = sortBy.getSelected();
			prevRevSort = revSort.isChecked();
		}
	}
	
	public int getColor(int alpha)
	{
		return color.getColorI(alpha);
	}
	
	public static enum Mode
	{
		AUTO("自动"),
		
		COUNT("计数"),
		
		HIDDEN("隐藏");
		
		private final String name;
		
		private Mode(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public static enum Position
	{
		LEFT("左"),
		
		RIGHT("右");
		
		private final String name;
		
		private Position(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public static enum SortBy
	{
		NAME("名称", (a, b) -> a.getName().compareToIgnoreCase(b.getName())),
		
		WIDTH("宽度", Comparator
			.comparingInt(h -> WurstClient.MC.font.width(h.getRenderName())));
		
		private final String name;
		private final Comparator<Hack> comparator;
		
		private SortBy(String name, Comparator<Hack> comparator)
		{
			this.name = name;
			this.comparator = comparator;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
