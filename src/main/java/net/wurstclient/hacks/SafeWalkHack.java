/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Box;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"safe walk"})
public final class SafeWalkHack extends Hack
{
	private final CheckboxSetting sneak =
		new CheckboxSetting("在边缘潜行", "在边缘可见地潜行。", false);
	
	private final SliderSetting edgeDistance = new SliderSetting("潜行边缘距离",
		"SafeWalk会让你离边缘多近才开始潜行。\n\n" + "这个设置只在\"Sneak at edges\"开启时生效。", 0.05,
		0.05, 0.25, 0.001, ValueDisplay.DECIMAL.withSuffix("m"));
	
	private boolean sneaking;
	
	public SafeWalkHack()
	{
		super("安全行走");
		setCategory(Category.MOVEMENT);
		addSetting(sneak);
		addSetting(edgeDistance);
	}
	
	@Override
	protected void onEnable()
	{
		WURST.getHax().parkourHack.setEnabled(false);
		sneaking = false;
	}
	
	@Override
	protected void onDisable()
	{
		if(sneaking)
			setSneaking(false);
	}
	
	public void onClipAtLedge(boolean clipping)
	{
		ClientPlayerEntity player = MC.player;
		
		if(!isEnabled() || !sneak.isChecked() || !player.isOnGround())
		{
			if(sneaking)
				setSneaking(false);
			
			return;
		}
		
		Box box = player.getBoundingBox();
		Box adjustedBox = box.stretch(0, -player.getStepHeight(), 0)
			.expand(-edgeDistance.getValue(), 0, -edgeDistance.getValue());
		
		if(MC.world.isSpaceEmpty(player, adjustedBox))
			clipping = true;
		
		setSneaking(clipping);
	}
	
	private void setSneaking(boolean sneaking)
	{
		KeyBinding sneakKey = MC.options.sneakKey;
		
		if(sneaking)
			sneakKey.setPressed(true);
		else
			((IKeyBinding)sneakKey).resetPressedState();
		
		this.sneaking = sneaking;
	}
	
	// See ClientPlayerEntityMixin
}
