/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.AirStrafingSpeedListener;
import net.wurstclient.events.IsPlayerInWaterListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"FlyHack", "fly hack", "flying"})
public final class FlightHack extends Hack
	implements UpdateListener, IsPlayerInWaterListener, AirStrafingSpeedListener
{
	public final SliderSetting horizontalSpeed = new SliderSetting(
		"水平速度", 1, 0.05, 10, 0.05, ValueDisplay.DECIMAL);
	
	public final SliderSetting verticalSpeed = new SliderSetting(
		"垂直速度",
		"\u00a7c\u00a7l警告：\u00a7r设置过高可能会造成摔落伤害，即使有NoFall。",
		1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);
	
	private final CheckboxSetting slowSneaking = new CheckboxSetting(
		"慢速潜行",
		"在你潜行时减少你的水平速度，以防止你出现故障。",
		true);
	
	private final CheckboxSetting antiKick = new CheckboxSetting("防踢",
		"每隔一段时间让你稍微掉落一点，以防止你被踢出。",
		false);
	
	private final SliderSetting antiKickInterval =
		new SliderSetting("防踢间隔",
			"防踢阻止你被踢出的频率。\n"
				+ "大多数服务器会在80刻后踢你。",
			30, 5, 80, 1,
			ValueDisplay.INTEGER.withSuffix(" 滴答").withLabel(1, "1 滴答"));
	
	private final SliderSetting antiKickDistance = new SliderSetting(
		"防踢距离",
		"防踢让你掉落的距离。\n"
			+ "大多数服务器至少需要0.032米才能阻止你被踢出。",
		0.07, 0.01, 0.2, 0.001, ValueDisplay.DECIMAL.withSuffix("米"));
	
	private int tickCounter = 0;
	
	public FlightHack()
	{
		super("让你飞起来");
		setCategory(Category.MOVEMENT);
		addSetting(horizontalSpeed);
		addSetting(verticalSpeed);
		addSetting(slowSneaking);
		addSetting(antiKick);
		addSetting(antiKickInterval);
		addSetting(antiKickDistance);
	}
	
	@Override
	protected void onEnable()
	{
		tickCounter = 0;
		
		WURST.getHax().creativeFlightHack.setEnabled(false);
		WURST.getHax().jetpackHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
		EVENTS.add(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
		EVENTS.remove(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		LocalPlayer player = MC.player;
		
		player.getAbilities().flying = false;
		
		player.setDeltaMovement(0, 0, 0);
		Vec3 velocity = player.getDeltaMovement();
		
		if(MC.options.keyJump.isDown())
			player.setDeltaMovement(velocity.x, verticalSpeed.getValue(),
				velocity.z);
		
		if(MC.options.keyShift.isDown())
			player.setDeltaMovement(velocity.x, -verticalSpeed.getValue(),
				velocity.z);
		
		if(antiKick.isChecked())
			doAntiKick(velocity);
	}
	
	@Override
	public void onGetAirStrafingSpeed(AirStrafingSpeedEvent event)
	{
		float speed = horizontalSpeed.getValueF();
		
		if(MC.options.keyShift.isDown() && slowSneaking.isChecked())
			speed = Math.min(speed, 0.85F);
		
		event.setSpeed(speed);
	}
	
	private void doAntiKick(Vec3 velocity)
	{
		if(tickCounter > antiKickInterval.getValueI() + 1)
			tickCounter = 0;
		
		switch(tickCounter)
		{
			case 0 ->
			{
				if(MC.options.keyShift.isDown())
					tickCounter = 2;
				else
					MC.player.setDeltaMovement(velocity.x,
						-antiKickDistance.getValue(), velocity.z);
			}
			
			case 1 -> MC.player.setDeltaMovement(velocity.x,
				antiKickDistance.getValue(), velocity.z);
		}
		
		tickCounter++;
	}
	
	@Override
	public void onIsPlayerInWater(IsPlayerInWaterEvent event)
	{
		event.setInWater(false);
	}
}
