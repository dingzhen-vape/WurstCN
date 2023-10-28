/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.PufferfishEntity;

public final class FilterNeutralSetting extends AttackDetectingEntityFilter
{
	private FilterNeutralSetting(String description, Mode selected,
		boolean checked)
	{
		super("过滤中立生物", description, selected, checked);
	}
	
	public FilterNeutralSetting(String description, Mode selected)
	{
		this(description, selected, false);
	}
	
	@Override
	public boolean onTest(Entity e)
	{
		return !(e instanceof Angerable || e instanceof PufferfishEntity
			|| e instanceof PiglinEntity);
	}
	
	@Override
	public boolean ifCalmTest(Entity e)
	{
		// special case for pufferfish
		if(e instanceof PufferfishEntity pfe)
			return pfe.getPuffState() > 0;
		
		if(e instanceof Angerable || e instanceof PiglinEntity)
			if(e instanceof MobEntity me)
				return me.isAttacking();
			
		return true;
	}
	
	public static FilterNeutralSetting genericCombat(Mode selected)
	{
		return new FilterNeutralSetting("当设置为\u00a7l开\u00a7r时，"
			+ "中立生物不会被攻击。 "
			+ "当设置为\u00a7l如果平静\u00a7r时，中立生物不会被"
			+ "攻击直到他们先攻击。要注意的是，这个过滤器"
			+ "无法检测中立生物是在攻击你还是别人。"
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，"
			+ "中立生物可以被攻击。", selected);
	}
	
	public static FilterNeutralSetting genericVision(Mode selected)
	{
		return new FilterNeutralSetting("当设置为\u00a7l开\u00a7r时，"
			+ "中立生物不会被显示。 "
			+ "当设置为\u00a7l如果平静\u00a7r时，中立生物不会被显示"
			+ "直到他们攻击某物。 "
			+ "当设置为\u00a7l关\u00a7r时，这个过滤器什么都不做，"
			+ "中立生物可以被显示。", selected);
	}
	
	public static FilterNeutralSetting onOffOnly(String description,
		boolean onByDefault)
	{
		return new FilterNeutralSetting(description, null, onByDefault);
	}
}
