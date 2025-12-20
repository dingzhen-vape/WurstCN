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
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;

public final class FilterPassiveSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT = "\n\n这个过滤器不"
		+ " 影响狼、蜜蜂、北极熊、河豚和村民。";
	
	public FilterPassiveSetting(String description, boolean checked)
	{
		super("过滤有好生物", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out hostile mobs (including hoglins)
		if(e instanceof Enemy)
			return true;
		
		// never filter out neutral mobs (including pufferfish)
		if(e instanceof NeutralMob || e instanceof Pufferfish)
			return true;
		
		return !(e instanceof Animal || e instanceof AmbientCreature
			|| e instanceof WaterAnimal || e instanceof AgeableWaterCreature);
	}
	
	public static FilterPassiveSetting genericCombat(boolean checked)
	{
		return new FilterPassiveSetting("Won't attack animals like pigs and"
			+ " 牛，环境生物如蝙蝠，以及水生生物如鱼、鱿鱼"
			+ " 和海豚。", checked);
	}
	
	public static FilterPassiveSetting genericVision(boolean checked)
	{
		return new FilterPassiveSetting("Won't show animals like pigs and"
			+ " 牛，环境生物如蝙蝠，以及水生生物如鱼、鱿鱼"
			+ " 和海豚。", checked);
	}
}
