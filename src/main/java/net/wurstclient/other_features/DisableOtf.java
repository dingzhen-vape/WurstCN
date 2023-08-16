/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"turn off", "hide wurst logo", "ghost mode", "stealth mode",
	"vanilla Minecraft"})
@DontBlock
public final class DisableOtf extends OtherFeature
{
	private final CheckboxSetting hideEnableButton = new CheckboxSetting(
		"隐藏启用按钮",
		"关闭统计界面后，移除\"Enable Wurst\"按钮。"
			+ "你将需要重启游戏才能重新启用Wurst。",
		false);
	
	public DisableOtf()
	{
		super("禁用Wurst",
			"要禁用Wurst，进入统计界面并按下\"Disable Wurst\"按钮。\n"
				+ "一旦按下，它会变成一个\"Enable Wurst\"按钮。");
		addSetting(hideEnableButton);
	}
	
	public boolean shouldHideEnableButton()
	{
		return !WURST.isEnabled() && hideEnableButton.isChecked();
	}
}
