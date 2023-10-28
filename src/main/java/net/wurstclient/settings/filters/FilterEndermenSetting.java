/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;

public final class FilterEndermenSetting extends AttackDetectingEntityFilter
{
	private FilterEndermenSetting(String description, Mode selected,
		boolean checked)
	{
		super("过滤末影人", description, selected, checked);
	}
	
	public FilterEndermenSetting(String description, Mode selected)
	{
		this(description, selected, false);
	}
	
	@Override
	public boolean onTest(Entity e)
	{
		return !(e instanceof EndermanEntity);
	}
	
	@Override
	public boolean ifCalmTest(Entity e)
	{
		return !(e instanceof EndermanEntity ee) || ee.isAttacking();
	}
	
	public static FilterEndermenSetting genericCombat(Mode selected)
	{
		return new FilterEndermenSetting("当设置为\u00a7l开\u00a7r时，"
			+ "末影人不会被攻击。 "
			+ "当设置为\u00a7l如果平静\u00a7r时，末影人不会被攻击"
			+ "直到他们先攻击。要注意的是，这个过滤器无法"
			+ "检测末影人是在攻击你还是别人。 "
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，"
			+ "末影人可以被攻击。", selected);
	}
	
	public static FilterEndermenSetting genericVision(Mode selected)
	{
		return new FilterEndermenSetting("当设置为\u00a7l开\u00a7r时，"
			+ "末影人不会被显示。 "
			+ "当设置为\u00a7l如果平静\u00a7r时，末影人不会被显示"
			+ "直到他们攻击某物。 "
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，"
			+ "末影人可以被显示。", selected);
	}
	
	public static FilterEndermenSetting onOffOnly(String description,
		boolean onByDefault)
	{
		return new FilterEndermenSetting(description, null, onByDefault);
	}
}
