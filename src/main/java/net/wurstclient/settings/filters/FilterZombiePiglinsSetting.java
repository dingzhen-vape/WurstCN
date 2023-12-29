/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;

public final class FilterZombiePiglinsSetting
	extends AttackDetectingEntityFilter
{
	private FilterZombiePiglinsSetting(String description, Mode selected,
		boolean checked)
	{
		super("过滤僵尸猪灵", description, selected, checked);
	}
	
	public FilterZombiePiglinsSetting(String description, Mode selected)
	{
		this(description, selected, false);
	}
	
	@Override
	public boolean onTest(Entity e)
	{
		return !(e instanceof ZombifiedPiglinEntity);
	}
	
	@Override
	public boolean ifCalmTest(Entity e)
	{
		return !(e instanceof ZombifiedPiglinEntity zpe) || zpe.isAttacking();
	}
	
	public static FilterZombiePiglinsSetting genericCombat(Mode selected)
	{
		return new FilterZombiePiglinsSetting("当设置为\u00a7l开\u00a7r时，"
			+ "僵尸猪灵不会被攻击。 " + "当设置为\u00a7l如果平静\u00a7r时，僵尸猪灵不会被"
			+ "攻击直到他们先攻击。要注意的是，这个过滤器" + "无法检测僵尸猪灵是在攻击你还是别人。" + " "
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，" + "僵尸猪灵可以被攻击。", selected);
	}
	
	public static FilterZombiePiglinsSetting genericVision(Mode selected)
	{
		return new FilterZombiePiglinsSetting("当设置为\u00a7l开\u00a7r时，"
			+ "僵尸猪灵不会被显示。 " + "当设置为\u00a7l如果平静\u00a7r时，僵尸猪灵不会被" + "显示直到他们攻击某物。 "
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，" + "僵尸猪灵可以被显示。", selected);
	}
	
	public static FilterZombiePiglinsSetting onOffOnly(String description,
		boolean onByDefault)
	{
		return new FilterZombiePiglinsSetting(description, null, onByDefault);
	}
}
