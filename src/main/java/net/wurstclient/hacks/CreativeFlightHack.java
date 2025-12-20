/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.phys.Vec3;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"creative flight", "CreativeFly", "creative fly"})
public final class CreativeFlightHack extends Hack implements UpdateListener
{
	private final CheckboxSetting antiKick = new CheckboxSetting("防踢",
		"每隔一段时间让你稍微掉落一点，以防止你被踢出。",
		false);
	
	private final SliderSetting antiKickInterval =
		new SliderSetting("防踢间隔",
			"防踢阻止你被踢出的频率。\n"
				+ "大多数服务器会在80刻后踢你。",
			30, 5, 80, 1, SliderSetting.ValueDisplay.INTEGER
				.withSuffix(" 滴答").withLabel(1, "1 滴答"));
	
	private final SliderSetting antiKickDistance = new SliderSetting(
		"防踢距离",
		"防踢让你掉落的距离。\n"
			+ "大多数服务器至少需要0.032米才能阻止你被踢出。",
		0.07, 0.01, 0.2, 0.001, ValueDisplay.DECIMAL.withSuffix("米"));
	
	private int tickCounter = 0;
	
	public CreativeFlightHack()
	{
		super("创造飞行");
		setCategory(Category.MOVEMENT);
		addSetting(antiKick);
		addSetting(antiKickInterval);
		addSetting(antiKickDistance);
	}
	
	@Override
	protected void onEnable()
	{
		tickCounter = 0;
		
		WURST.getHax().jetpackHack.setEnabled(false);
		WURST.getHax().flightHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		
		LocalPlayer player = MC.player;
		Abilities abilities = player.getAbilities();
		
		boolean creative = player.getAbilities().instabuild;
		abilities.flying = creative && !player.onGround();
		abilities.mayfly = creative;
		
		restoreKeyPresses();
	}
	
	@Override
	public void onUpdate()
	{
		Abilities abilities = MC.player.getAbilities();
		abilities.mayfly = true;
		
		if(antiKick.isChecked() && abilities.flying)
			doAntiKick();
	}
	
	private void doAntiKick()
	{
		if(tickCounter > antiKickInterval.getValueI() + 2)
			tickCounter = 0;
		
		switch(tickCounter)
		{
			case 0 ->
			{
				if(MC.options.keyShift.isDown() && !MC.options.keyJump.isDown())
					tickCounter = 3;
				else
					setMotionY(-antiKickDistance.getValue());
			}
			
			case 1 -> setMotionY(antiKickDistance.getValue());
			
			case 2 -> setMotionY(0);
			
			case 3 -> restoreKeyPresses();
		}
		
		tickCounter++;
	}
	
	private void setMotionY(double motionY)
	{
		MC.options.keyShift.setDown(false);
		MC.options.keyJump.setDown(false);
		
		Vec3 velocity = MC.player.getDeltaMovement();
		MC.player.setDeltaMovement(velocity.x, motionY, velocity.z);
	}
	
	private void restoreKeyPresses()
	{
		KeyMapping[] keys = {MC.options.keyJump, MC.options.keyShift};
		
		for(KeyMapping key : keys)
			IKeyBinding.get(key).resetPressedState();
	}
}
