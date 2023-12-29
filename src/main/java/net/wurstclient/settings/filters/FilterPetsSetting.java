/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;

public final class FilterPetsSetting extends EntityFilterCheckbox
{
	public FilterPetsSetting(String description, boolean checked)
	{
		super("过滤宠物", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		return !(e instanceof TameableEntity && ((TameableEntity)e).isTamed())
			&& !(e instanceof AbstractHorseEntity
				&& ((AbstractHorseEntity)e).isTame());
	}
	
	public static FilterPetsSetting genericCombat(boolean checked)
	{
		return new FilterPetsSetting("不会攻击驯服的狼，驯服的马等。", checked);
	}
	
	public static FilterPetsSetting genericVision(boolean checked)
	{
		return new FilterPetsSetting("不会显示驯服的狼，驯服的马等。", checked);
	}
}
