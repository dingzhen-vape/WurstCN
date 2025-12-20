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
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"no fire overlay"})
public final class NoFireOverlayHack extends Hack
{
	private final SliderSetting offset =
		new SliderSetting("偏移量", "降低火焰覆盖的量。",
			0.6, 0.01, 0.6, 0.01, ValueDisplay.DECIMAL);
	
	public NoFireOverlayHack()
	{
		super("去火焰贴图");
		setCategory(Category.RENDER);
		addSetting(offset);
	}
	
	public float getOverlayOffset()
	{
		return isEnabled() ? offset.getValueF() : 0;
	}
	
	// See InGameOverlayRendererMixin.getFireOffset()
}
