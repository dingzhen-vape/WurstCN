/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;

public final class FilterVillagersSetting extends EntityFilterCheckbox
{
	public FilterVillagersSetting(String description, boolean checked)
	{
		super("过滤村民", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		return !(e instanceof MerchantEntity);
	}
	
	public static FilterVillagersSetting genericCombat(boolean checked)
	{
		return new FilterVillagersSetting(
			"不会攻击村民和流浪商人。", checked);
	}
	
	public static FilterVillagersSetting genericVision(boolean checked)
	{
		return new FilterVillagersSetting(
			"不会显示村民和流浪商人。", checked);
	}
}
