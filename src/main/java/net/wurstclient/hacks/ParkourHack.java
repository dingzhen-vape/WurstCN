/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.world.phys.AABB;
import net.wurstclient.Category;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

public final class ParkourHack extends Hack implements UpdateListener
{
	private final SliderSetting minDepth = new SliderSetting("最小深度",
		"Won't jump over a pit if it isn't at least this deep.\n"
			+ "减少这个值可以让 Parkour 在地毯边缘跳跃。"
			+ "米",
		0.5, 0.05, 10, 0.05, ValueDisplay.DECIMAL.withSuffix("米"));
	
	private final SliderSetting edgeDistance =
		new SliderSetting("Parkour 在跳跃前让你离边缘有多近。",
			"米",
			0.001, 0.001, 0.25, 0.001, ValueDisplay.DECIMAL.withSuffix("米"));
	
	private final CheckboxSetting sneak = new CheckboxSetting(
		"即使你在潜行，也保持 Parkour 活跃。\n",
		"使用这个选项时，你可能想要增加 \u00a7l边缘 \u00a7l距离\u00a7r"
			+ " 滑块的值。"
			+ "简单跑酷",
		false);
	
	public ParkourHack()
	{
		super("自动跑酷");
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
		if(!MC.player.onGround() || MC.options.keyJump.isDown())
			return;
		
		if(!sneak.isChecked()
			&& (MC.player.isShiftKeyDown() || MC.options.keyShift.isDown()))
			return;
		
		AABB box = MC.player.getBoundingBox();
		AABB adjustedBox = box.expandTowards(0, -minDepth.getValue(), 0)
			.inflate(-edgeDistance.getValue(), 0, -edgeDistance.getValue());
		
		if(!MC.level.noCollision(MC.player, adjustedBox))
			return;
		
		MC.player.jumpFromGround();
	}
}
