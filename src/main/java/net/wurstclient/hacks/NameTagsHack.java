/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"name tags"})
public final class NameTagsHack extends Hack
{
	private final CheckboxSetting unlimitedRange =
		new CheckboxSetting("无限范围",
			"移除了名牌的64方块距离限制。", true);
	
	private final CheckboxSetting seeThrough = new CheckboxSetting(
		"透视模式",
		"在透视文字层上渲染名牌。这使得它们"
			+ "在墙后更容易阅读，但在水"
			+ "和其他透明的东西后更难阅读。",
		false);
	
	private final CheckboxSetting forceNametags = new CheckboxSetting(
		"强制名牌",
		"强制所有玩家的名牌可见，甚至是你自己的。", false);
	
	public NameTagsHack()
	{
		super("名字标签");
		setCategory(Category.RENDER);
		addSetting(unlimitedRange);
		addSetting(seeThrough);
		addSetting(forceNametags);
	}
	
	public boolean isUnlimitedRange()
	{
		return isEnabled() && unlimitedRange.isChecked();
	}
	
	public boolean isSeeThrough()
	{
		return isEnabled() && seeThrough.isChecked();
	}
	
	public boolean shouldForceNametags()
	{
		return isEnabled() && forceNametags.isChecked();
	}
	
	// See LivingEntityRendererMixin and
	// EntityRendererMixin.wurstRenderLabelIfPresent()
}
