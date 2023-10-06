/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autofish;

import net.minecraft.entity.projectile.FishingBobberEntity;
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
	
	public void checkWaterAround(FishingBobberEntity bobber)
	{
		if(bobber.isOpenOrWaterAround(bobber.getBlockPos()))
		{
			hasAlreadyWarned = false;
			return;
		}
		
		if(isChecked() && !hasAlreadyWarned)
		{
			ChatUtils.warning("你现在正在浅水中钓鱼。");
			ChatUtils.message(
				"你不能在这样的水中钓到任何宝藏物品。");
			
			if(!WurstClient.INSTANCE.getHax().openWaterEspHack.isEnabled())
				ChatUtils.message("使用OpenWaterESP来寻找开阔水域。");
			
			hasAlreadyWarned = true;
		}
	}
}
