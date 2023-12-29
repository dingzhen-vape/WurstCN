/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PiglinEntity;

public final class FilterPiglinsSetting extends AttackDetectingEntityFilter
{
	private static final String EXCEPTIONS_TEXT = "这个过滤器不会影响猪灵暴徒。";
	
	private FilterPiglinsSetting(String description, Mode selected,
		boolean checked)
	{
		super("过滤猪灵", description + EXCEPTIONS_TEXT, selected, checked);
	}
	
	public FilterPiglinsSetting(String description, Mode selected)
	{
		this(description, selected, false);
	}
	
	@Override
	public boolean onTest(Entity e)
	{
		return !(e instanceof PiglinEntity);
	}
	
	@Override
	public boolean ifCalmTest(Entity e)
	{
		return !(e instanceof PiglinEntity pe) || pe.isAttacking();
	}
	
	public static FilterPiglinsSetting genericCombat(Mode selected)
	{
		return new FilterPiglinsSetting("当设置为\u00a7l开\u00a7r时，" + "猪灵不会被攻击。 "
			+ "当设置为\u00a7l如果平静\u00a7r时，猪灵不会被攻击" + "直到他们先攻击。要注意的是，这个过滤器无法"
			+ "检测猪灵是在攻击你还是别人。 " + "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，"
			+ "猪灵可以被攻击。", selected);
	}
	
	public static FilterPiglinsSetting genericVision(Mode selected)
	{
		return new FilterPiglinsSetting("当设置为\u00a7l开\u00a7r时，" + "猪灵不会被显示。 "
			+ "当设置为\u00a7l如果平静\u00a7r时，猪灵不会被显示直到" + "他们攻击某物。 "
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，" + "猪灵可以被显示。", selected);
	}
	
	public static FilterPiglinsSetting onOffOnly(String description,
		boolean onByDefault)
	{
		return new FilterPiglinsSetting(description, null, onByDefault);
	}
}
