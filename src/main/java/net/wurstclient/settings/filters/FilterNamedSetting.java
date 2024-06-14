/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;

public final class FilterNamedSetting extends EntityFilterCheckbox
{
	public FilterNamedSetting(String description, boolean checked)
	{
		super("过滤命名标记生物", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		return !e.hasCustomName();
	}
	
	public static FilterNamedSetting genericCombat(boolean checked)
	{
		return new FilterNamedSetting("不会攻击命名标记的生物.", checked);
	}
	
	public static FilterNamedSetting genericVision(boolean checked)
	{
		return new FilterNamedSetting("不会显示命名标记的生物.", checked);
	}
}
