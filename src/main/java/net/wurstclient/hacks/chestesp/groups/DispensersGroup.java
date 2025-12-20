/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.chestesp.groups;

import java.awt.Color;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.wurstclient.hacks.chestesp.ChestEspBlockGroup;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorSetting;

public final class DispensersGroup extends ChestEspBlockGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("包括发射器", false);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("发射器颜色",
			"发射器会以这种颜色突出显示。",
			new Color(0xFF8000));
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof DispenserBlockEntity
			&& !(be instanceof DropperBlockEntity);
	}
}
