/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.WurstClient;
import net.wurstclient.events.DeathListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"auto respawn", "AutoRevive", "auto revive"})
public final class AutoRespawnHack extends Hack implements DeathListener
{
	private final CheckboxSetting button = new CheckboxSetting("死亡屏幕按钮",
		"在死亡" + "屏幕上显示一个按钮，让你快速启用AutoRespawn。", true);
	
	public AutoRespawnHack()
	{
		super("自动重生");
		setCategory(Category.COMBAT);
		addSetting(button);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(DeathListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(DeathListener.class, this);
	}
	
	@Override
	public void onDeath()
	{
		MC.player.requestRespawn();
		MC.setScreen(null);
	}
	
	public boolean shouldShowButton()
	{
		return WurstClient.INSTANCE.isEnabled() && !isEnabled()
			&& button.isChecked();
	}
}
