/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.filterlists.EntityFilterList.EntityFilter;

public final class FilterFlyingSetting extends SliderSetting
	implements EntityFilter
{
	public FilterFlyingSetting(String description, double value)
	{
		super("过滤飞行假人", description, value, 0, 2, 0.05,
			ValueDisplay.DECIMAL.withLabel(0, "关闭"));
	}
	
	@Override
	public boolean test(Entity e)
	{
		if(!(e instanceof PlayerEntity))
			return true;
		
		Box box = e.getBoundingBox();
		box = box.union(box.offset(0, -getValue(), 0));
		return !WurstClient.MC.world.isSpaceEmpty(box);
	}
	
	@Override
	public boolean isFilterEnabled()
	{
		return getValue() > 0;
	}
	
	@Override
	public Setting getSetting()
	{
		return this;
	}
	
	public static FilterFlyingSetting genericCombat(double value)
	{
		return new FilterFlyingSetting(
			"不会攻击跳至少给定高度以上的玩家。\n\n" + "有用于服务器试图通过放置一个跳动机器人附近你来检测你的黑客行为。",
			value);
	}
}
