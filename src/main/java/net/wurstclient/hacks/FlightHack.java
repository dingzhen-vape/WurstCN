/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
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
		"\u00a7c\u00a7l警告:\u00a7r 设置这个太高可能会造成摔落伤害，即使有防摔。",
		1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);
	
	private final CheckboxSetting slowSneaking = new CheckboxSetting(
		"慢速潜行",
		"在你潜行时降低你的水平速度，以防止你出现故障。",
		true);
	
	private final CheckboxSetting antiKick = new CheckboxSetting("防踢",
		"让你每隔一段时间稍微下降一点，以防止你被踢出。",
		false);
	
	private final SliderSetting antiKickInterval =
		new SliderSetting("防踢间隔",
			"防踢应该多久阻止你被踢出一次。\n"
				+ "大多数服务器会在80个刻后踢出你。",
			30, 5, 80, 1, ValueDisplay.INTEGER.withSuffix(" 刻"));
	
	private final SliderSetting antiKickDistance = new SliderSetting(
		"防踢距离",
		"防踢应该让你下降多远。\n"
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
	public void onEnable()
	{
		tickCounter = 0;
		
		WURST.getHax().creativeFlightHack.setEnabled(false);
		WURST.getHax().jetpackHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
		EVENTS.add(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
		EVENTS.remove(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		
		player.getAbilities().flying = false;
		
		player.setVelocity(0, 0, 0);
		Vec3d velocity = player.getVelocity();
		
		if(MC.options.jumpKey.isPressed())
			player.setVelocity(velocity.x, verticalSpeed.getValue(),
				velocity.z);
		
		if(MC.options.sneakKey.isPressed())
			player.setVelocity(velocity.x, -verticalSpeed.getValue(),
				velocity.z);
		
		if(antiKick.isChecked())
			doAntiKick(velocity);
	}
	
	@Override
	public void onGetAirStrafingSpeed(AirStrafingSpeedEvent event)
	{
		float speed = horizontalSpeed.getValueF();
		
		if(MC.options.sneakKey.isPressed() && slowSneaking.isChecked())
			speed = Math.min(speed, 0.85F);
		
		event.setSpeed(speed);
	}
	
	private void doAntiKick(Vec3d velocity)
	{
		if(tickCounter > antiKickInterval.getValueI() + 1)
			tickCounter = 0;
		
		switch(tickCounter)
		{
			case 0 ->
			{
				if(MC.options.sneakKey.isPressed())
					tickCounter = 2;
				else
					MC.player.setVelocity(velocity.x,
						-antiKickDistance.getValue(), velocity.z);
			}
			
			case 1 -> MC.player.setVelocity(velocity.x,
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
