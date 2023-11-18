/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.Vec3d;
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
				.withSuffix(" 刻").withLabel(1, "1刻"));
	
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
	public void onEnable()
	{
		tickCounter = 0;
		
		WURST.getHax().jetpackHack.setEnabled(false);
		WURST.getHax().flightHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		
		ClientPlayerEntity player = MC.player;
		PlayerAbilities abilities = player.getAbilities();
		
		boolean creative = player.isCreative();
		abilities.flying = creative && !player.isOnGround();
		abilities.allowFlying = creative;
		
		restoreKeyPresses();
	}
	
	@Override
	public void onUpdate()
	{
		PlayerAbilities abilities = MC.player.getAbilities();
		abilities.allowFlying = true;
		
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
				if(MC.options.sneakKey.isPressed()
					&& !MC.options.jumpKey.isPressed())
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
		MC.options.sneakKey.setPressed(false);
		MC.options.jumpKey.setPressed(false);
		
		Vec3d velocity = MC.player.getVelocity();
		MC.player.setVelocity(velocity.x, motionY, velocity.z);
	}
	
	private void restoreKeyPresses()
	{
		KeyBinding[] bindings = {MC.options.jumpKey, MC.options.sneakKey};
		
		for(KeyBinding binding : bindings)
			((IKeyBinding)binding).resetPressedState();
	}
}
