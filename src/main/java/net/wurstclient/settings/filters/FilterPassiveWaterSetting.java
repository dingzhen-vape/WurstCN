/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PufferfishEntity;

public final class FilterPassiveWaterSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT = "\n\n这个过滤器不影响守卫者、溺尸和河豚。";
	
	public FilterPassiveWaterSetting(String description, boolean checked)
	{
		super("过滤非攻击性水生生物", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out pufferfish
		if(e instanceof PufferfishEntity)
			return true;
		
		return !(e instanceof WaterCreatureEntity
			|| e instanceof AxolotlEntity);
	}
	
	public static FilterPassiveWaterSetting genericCombat(boolean checked)
	{
		return new FilterPassiveWaterSetting("不会攻击非攻击性水生生物" + "如鱼、鱿鱼、海豚和阿维洛特尔。",
			checked);
	}
	
	public static FilterPassiveWaterSetting genericVision(boolean checked)
	{
		return new FilterPassiveWaterSetting("不会显示非攻击性水生生物" + "如鱼、鱿鱼、海豚和阿维洛特尔。",
			checked);
	}
}
