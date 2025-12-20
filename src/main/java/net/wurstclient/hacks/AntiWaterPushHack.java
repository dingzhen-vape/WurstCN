/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.IsPlayerInWaterListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.VelocityFromFluidListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"anti water push", "NoWaterPush", "no water push"})
public final class AntiWaterPushHack extends Hack implements UpdateListener,
	VelocityFromFluidListener, IsPlayerInWaterListener
{
	private final CheckboxSetting preventSlowdown = new CheckboxSetting(
		"防止减速", "让你可以在水下以全速行走。\n"
			+ "一些服务器会认为这是速度hack。",
		false);
	
	public AntiWaterPushHack()
	{
		super("防止水流推动");
		setCategory(Category.MOVEMENT);
		addSetting(preventSlowdown);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(VelocityFromFluidListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(VelocityFromFluidListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!preventSlowdown.isChecked())
			return;
		
		if(!MC.options.keyJump.isDown())
			return;
		
		if(!MC.player.onGround())
			return;
		
		if(!IMC.getPlayer().isTouchingWaterBypass())
			return;
		
		MC.player.jumpFromGround();
	}
	
	@Override
	public void onVelocityFromFluid(VelocityFromFluidEvent event)
	{
		if(event.getEntity() == MC.player)
			event.cancel();
	}
	
	@Override
	public void onIsPlayerInWater(IsPlayerInWaterEvent event)
	{
		if(preventSlowdown.isChecked())
			event.setInWater(false);
	}
	
	public boolean isPreventingSlowdown()
	{
		return preventSlowdown.isChecked();
	}
}
