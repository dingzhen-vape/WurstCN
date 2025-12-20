/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.monster.Enemy;

public final class FilterBabiesSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT = "\n\n这个过滤器不"
		+ "影响小僵尸和其他敌意婴儿生物.";
	
	public FilterBabiesSetting(String description, boolean checked)
	{
		super("过滤婴儿", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out hostile mobs (including hoglins)
		if(e instanceof Enemy)
			return true;
		
		// filter out passive entity babies
		if(e instanceof AgeableMob pe && pe.isBaby())
			return false;
		
		// filter out tadpoles
		if(e instanceof Tadpole)
			return false;
		
		return true;
	}
	
	public static FilterBabiesSetting genericCombat(boolean checked)
	{
		return new FilterBabiesSetting(
			"Won't attack baby pigs, baby villagers, etc.", checked);
	}
}
