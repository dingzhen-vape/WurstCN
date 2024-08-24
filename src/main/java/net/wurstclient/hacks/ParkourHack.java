/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.util.math.Box;
import net.wurstclient.Category;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

public final class ParkourHack extends Hack implements UpdateListener
{
	private final SliderSetting minDepth = new SliderSetting("最小深度",
		"如果坑不够深，就不会跳过去。\n" + "增加这个值可以阻止 Parkour 从楼梯跳下去。\n"
			+ "减少这个值可以让 Parkour 在地毯边缘跳跃。",
		0.5, 0.05, 10, 0.05, ValueDisplay.DECIMAL.withSuffix("米"));
	
	private final SliderSetting edgeDistance =
		new SliderSetting("边缘距离", "Parkour 在跳跃前让你离边缘有多近。", 0.001, 0.001, 0.25,
			0.001, ValueDisplay.DECIMAL.withSuffix("米"));
	
	private final CheckboxSetting sneak =
		new CheckboxSetting("潜行时跳跃",
			"即使你在潜行，也保持 Parkour 活跃。\n"
				+ "使用这个选项时，你可能想要增加 \u00a7l边缘 \u00a7l距离\u00a7r" + " 滑块的值。",
			false);
	
	public ParkourHack()
	{
		super("简单跑酷");
		setCategory(Category.MOVEMENT);
		addSetting(minDepth);
		addSetting(edgeDistance);
		addSetting(sneak);
	}
	
	@Override
	protected void onEnable()
	{
		WURST.getHax().safeWalkHack.setEnabled(false);
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!MC.player.isOnGround() || MC.options.jumpKey.isPressed())
			return;
		
		if(!sneak.isChecked()
			&& (MC.player.isSneaking() || MC.options.sneakKey.isPressed()))
			return;
		
		Box box = MC.player.getBoundingBox();
		Box adjustedBox = box.stretch(0, -minDepth.getValue(), 0)
			.expand(-edgeDistance.getValue(), 0, -edgeDistance.getValue());
		
		if(!MC.world.isSpaceEmpty(MC.player, adjustedBox))
			return;
		
		MC.player.jump();
	}
}
