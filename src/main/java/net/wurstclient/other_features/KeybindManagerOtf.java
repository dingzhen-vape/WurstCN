/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.options.KeybindManagerScreen;
import net.wurstclient.other_feature.OtherFeature;

@SearchTags({"KeybindManager", "keybind manager", "KeybindsManager",
	"keybinds manager"})
@DontBlock
public final class KeybindManagerOtf extends OtherFeature
{
	public KeybindManagerOtf()
	{
		super("快捷键", "这只是一个快捷方式，让你可以在GUI内打开键位管理器。通常你将前往Wurst选项 > 键位绑定。");
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "打开键位绑定管理器";
	}
	
	@Override
	public void doPrimaryAction()
	{
		MC.setScreen(new KeybindManagerScreen(MC.currentScreen));
	}
}
