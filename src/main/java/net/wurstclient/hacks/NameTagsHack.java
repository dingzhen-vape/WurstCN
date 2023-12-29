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
import net.wurstclient.settings.SliderSetting;

@SearchTags({"name tags"})
public final class NameTagsHack extends Hack
{
	private final SliderSetting scale = new SliderSetting("比例", "标签的大小。", 1,
		0.05, 5, 0.05, SliderSetting.ValueDisplay.PERCENTAGE);
	
	private final CheckboxSetting unlimitedRange =
		new CheckboxSetting("无限范围", "移除标签的64格距离限制。", true);
	
	private final CheckboxSetting seeThrough = new CheckboxSetting("透视模式",
		"在透视文字层渲染标签。这使得它们" + "在墙后更容易阅读，但会导致一些图形故障" + "与水和其他透明的东西。", false);
	
	private final CheckboxSetting forceMobNametags =
		new CheckboxSetting("总是显示命名的生物", "即使" + "你没有直接看着它们，也显示命名生物的标签。", true);
	
	private final CheckboxSetting forcePlayerNametags = new CheckboxSetting(
		"总是显示玩家名字", "显示你自己的标签以及任何玩家名字，即使" + "它们通常被计分板团队设置禁用。", false);
	
	public NameTagsHack()
	{
		super("名字标签");
		setCategory(Category.RENDER);
		addSetting(scale);
		addSetting(unlimitedRange);
		addSetting(seeThrough);
		addSetting(forceMobNametags);
		addSetting(forcePlayerNametags);
	}
	
	public float getScale()
	{
		return scale.getValueF();
	}
	
	public boolean isUnlimitedRange()
	{
		return isEnabled() && unlimitedRange.isChecked();
	}
	
	public boolean isSeeThrough()
	{
		return isEnabled() && seeThrough.isChecked();
	}
	
	public boolean shouldForceMobNametags()
	{
		return isEnabled() && forceMobNametags.isChecked();
	}
	
	public boolean shouldForcePlayerNametags()
	{
		return isEnabled() && forcePlayerNametags.isChecked();
	}
	
	// See EntityRendererMixin.wurstRenderLabelIfPresent(),
	// LivingEntityRendererMixin, MobEntityRendererMixin
}
