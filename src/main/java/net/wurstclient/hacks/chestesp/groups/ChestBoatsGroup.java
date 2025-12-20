/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.chestesp.groups;

import java.awt.Color;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.wurstclient.hacks.chestesp.ChestEspEntityGroup;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorSetting;

public final class ChestBoatsGroup extends ChestEspEntityGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("包括箱子船", true);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("箱子船颜色",
			"装有箱子的船会以这种颜色突出显示。",
			Color.YELLOW);
	}
	
	@Override
	protected boolean matches(Entity e)
	{
		return e instanceof AbstractChestBoat;
	}
}
