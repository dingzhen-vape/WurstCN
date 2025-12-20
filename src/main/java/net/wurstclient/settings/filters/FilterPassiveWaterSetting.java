/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.WaterAnimal;

public final class FilterPassiveWaterSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT =
		"\n\n这个过滤器不影响守卫者、溺尸和河豚。";
	
	public FilterPassiveWaterSetting(String description, boolean checked)
	{
		super("过滤非攻击性水生生物", description + EXCEPTIONS_TEXT,
			checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out pufferfish
		if(e instanceof Pufferfish)
			return true;
		
		return !(e instanceof WaterAnimal || e instanceof AgeableWaterCreature
			|| e instanceof Axolotl);
	}
	
	public static FilterPassiveWaterSetting genericCombat(boolean checked)
	{
		return new FilterPassiveWaterSetting("Won't attack passive water mobs"
			+ "如鱼、鱿鱼、海豚和阿维洛特尔。", checked);
	}
	
	public static FilterPassiveWaterSetting genericVision(boolean checked)
	{
		return new FilterPassiveWaterSetting("Won't show passive water mobs"
			+ "如鱼、鱿鱼、海豚和阿维洛特尔。", checked);
	}
}
