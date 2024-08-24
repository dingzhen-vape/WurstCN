/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TadpoleEntity;

public final class FilterBabiesSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT =
		"\n\n这个过滤器不" + "影响小僵尸和其他敌意婴儿生物.";
	
	public FilterBabiesSetting(String description, boolean checked)
	{
		super("过滤婴儿", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out hostile mobs (including hoglins)
		if(e instanceof Monster)
			return true;
		
		// filter out passive entity babies
		if(e instanceof PassiveEntity pe && pe.isBaby())
			return false;
		
		// filter out tadpoles
		if(e instanceof TadpoleEntity)
			return false;
		
		return true;
	}
	
	public static FilterBabiesSetting genericCombat(boolean checked)
	{
		return new FilterBabiesSetting("不会攻击婴儿猪、婴村民等.", checked);
	}
}
