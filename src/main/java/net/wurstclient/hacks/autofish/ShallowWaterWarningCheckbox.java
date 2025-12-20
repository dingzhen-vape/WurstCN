/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autofish;

import net.minecraft.world.entity.projectile.FishingHook;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

public class ShallowWaterWarningCheckbox extends CheckboxSetting
{
	private boolean hasAlreadyWarned;
	
	public ShallowWaterWarningCheckbox()
	{
		super("浅水警告",
			"当你在浅水中钓鱼时，在聊天中显示警告信息"
				+ " 水。",
			true);
	}
	
	public void reset()
	{
		hasAlreadyWarned = false;
	}
	
	public void checkWaterType()
	{
		FishingHook bobber = WurstClient.MC.player.fishing;
		if(bobber.calculateOpenWater(bobber.blockPosition()))
		{
			hasAlreadyWarned = false;
			return;
		}
		
		if(isChecked() && !hasAlreadyWarned)
		{
			ChatUtils.warning("你现在正在浅水中钓鱼。");
			ChatUtils.message(
				"You can't get any treasure items while fishing like this.");
			
			if(!WurstClient.INSTANCE.getHax().openWaterEspHack.isEnabled())
				ChatUtils.message("使用OpenWaterESP来寻找开阔水域。");
			
			hasAlreadyWarned = true;
		}
	}
}
