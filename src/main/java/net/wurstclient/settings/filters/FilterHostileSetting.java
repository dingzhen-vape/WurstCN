/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.Piglin;

public final class FilterHostileSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT = "\n\n这个过滤器不"
		+ "影响末影人、未被激怒的猪灵和僵尸猪灵.";
	
	public FilterHostileSetting(String description, boolean checked)
	{
		super("过滤敌意生物", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out neutral mobs (including piglins)
		if(e instanceof NeutralMob || e instanceof Piglin)
			return true;
		
		return !(e instanceof Enemy);
	}
	
	public static FilterHostileSetting genericCombat(boolean checked)
	{
		return new FilterHostileSetting(
			"Won't attack hostile mobs like zombies and creepers.", checked);
	}
	
	public static FilterHostileSetting genericVision(boolean checked)
	{
		return new FilterHostileSetting(
			"Won't show hostile mobs like zombies and creepers.", checked);
	}
}
