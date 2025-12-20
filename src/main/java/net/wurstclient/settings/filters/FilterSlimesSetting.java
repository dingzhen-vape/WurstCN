/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;

public final class FilterSlimesSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT =
		"\n\n这个过滤器不影响岩浆史莱姆。";
	
	public FilterSlimesSetting(String description, boolean checked)
	{
		super("过滤史莱姆", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		return !(e instanceof Slime) || e instanceof MagmaCube;
	}
	
	public static FilterSlimesSetting genericCombat(boolean checked)
	{
		return new FilterSlimesSetting("Won't attack slimes.", checked);
	}
	
	public static FilterSlimesSetting genericVision(boolean checked)
	{
		return new FilterSlimesSetting("Won't show slimes.", checked);
	}
}
