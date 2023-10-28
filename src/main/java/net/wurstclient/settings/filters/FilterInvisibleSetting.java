/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;

public final class FilterInvisibleSetting extends EntityFilterCheckbox
{
	public FilterInvisibleSetting(String description, boolean checked)
	{
		super("过滤隐形", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		return !e.isInvisible();
	}
	
	public static FilterInvisibleSetting genericCombat(boolean checked)
	{
		return new FilterInvisibleSetting("不会攻击隐形的实体。",
			checked);
	}
	
	public static FilterInvisibleSetting genericVision(boolean checked)
	{
		return new FilterInvisibleSetting("不会显示隐形的实体。",
			checked);
	}
}
