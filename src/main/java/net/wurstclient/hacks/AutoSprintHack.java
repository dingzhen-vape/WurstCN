/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"auto sprint"})
public final class AutoSprintHack extends Hack implements UpdateListener
{
	private final CheckboxSetting hungry =
		new CheckboxSetting("饥饿冲刺", "即使在低饥饿时也能冲刺。", false);
	
	public AutoSprintHack()
	{
		super("自动疾跑");
		setCategory(Category.MOVEMENT);
		addSetting(hungry);
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		
		if(player.horizontalCollision || player.isSneaking())
			return;
		
		if(player.isInsideWaterOrBubbleColumn() || player.isSubmergedInWater())
			return;
		
		if(player.forwardSpeed > 0)
			player.setSprinting(true);
	}
	
	public boolean shouldSprintHungry()
	{
		return isEnabled() && hungry.isChecked();
	}
}
