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
import net.wurstclient.events.KnockbackListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"anti knockback", "AntiVelocity", "anti velocity", "NoKnockback",
	"no knockback", "AntiKB", "anti kb"})
public final class AntiKnockbackHack extends Hack implements KnockbackListener
{
	private final SliderSetting hStrength =
		new SliderSetting("水平强度",
			"减少水平击退的距离。\n"
				+ "-100% = 双倍击退\n" + "0% = 正常击退\n"
				+ "100% = 无击退\n" + ">100% = 反向击退",
			1, -1, 2, 0.01, ValueDisplay.PERCENTAGE);
	
	private final SliderSetting vStrength =
		new SliderSetting("垂直强度",
			"减少垂直击退的距离。\n"
				+ "-100% = 双倍击退\n" + "0% = 正常击退\n"
				+ "100% = 无击退\n" + ">100% = 反向击退",
			1, -1, 2, 0.01, ValueDisplay.PERCENTAGE);
	
	public AntiKnockbackHack()
	{
		super("反击退");
		setCategory(Category.COMBAT);
		addSetting(hStrength);
		addSetting(vStrength);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(KnockbackListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(KnockbackListener.class, this);
	}
	
	@Override
	public void onKnockback(KnockbackEvent event)
	{
		double verticalMultiplier = 1 - vStrength.getValue();
		double horizontalMultiplier = 1 - hStrength.getValue();
		
		event.setX(event.getDefaultX() * horizontalMultiplier);
		event.setY(event.getDefaultY() * verticalMultiplier);
		event.setZ(event.getDefaultZ() * horizontalMultiplier);
	}
}
