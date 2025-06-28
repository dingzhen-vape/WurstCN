/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import java.awt.Color;
import java.util.function.BooleanSupplier;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.ColorSetting;
import net.wurstclient.settings.EnumSetting;

@SearchTags({"wurst logo", "top left corner"})
@DontBlock
public final class WurstLogoOtf extends OtherFeature
{
	private final ColorSetting bgColor = new ColorSetting("背景",
		"背景颜色。\n" + "仅当\u00a76彩虹UI\u00a7r被禁用时可见。", Color.WHITE);
	
	private final ColorSetting txtColor =
		new ColorSetting("文本", "文本颜色。", Color.BLACK);
	
	private final EnumSetting<Visibility> visibility =
		new EnumSetting<>("是否可见", Visibility.values(), Visibility.ALWAYS);
	
	public WurstLogoOtf()
	{
		super("WurstLogo", "显示Wurst的标志和版本号在屏幕上。");
		addSetting(bgColor);
		addSetting(txtColor);
		addSetting(visibility);
	}
	
	public boolean isVisible()
	{
		return visibility.getSelected().isVisible();
	}
	
	public int getBackgroundColor()
	{
		return bgColor.getColorI(128);
	}
	
	public int getTextColor()
	{
		return txtColor.getColorI();
	}
	
	public static enum Visibility
	{
		ALWAYS("是", () -> true),
		
		ONLY_OUTDATED("否", () -> false);
		
		private final String name;
		private final BooleanSupplier visible;
		
		private Visibility(String name, BooleanSupplier visible)
		{
			this.name = name;
			this.visible = visible;
		}
		
		public boolean isVisible()
		{
			return visible.getAsBoolean();
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
