/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.ChatUtils;

import java.net.URI;

public final class AntiBlind1Hack extends Hack
{
	private Text component;
	
	private void showLink(String text, URI url)
	{
		ClickEvent event = new ClickEvent.OpenUrl(url);
		component = Text.literal(text).styled(s -> s.withClickEvent(event));
	}
	
	public AntiBlind1Hack()
	{
		super("食我压路汉化");
		setCategory(Category.OTHER);
	}
	
	@Override
	public void onEnable()
	{
		String text =
			"译者主页链接(点我):https://space.bilibili.com/432060575?spm_id_from=333.1007.0.0";
		URI url = URI.create(
			"https://space.bilibili.com/432060575?spm_id_from=333.1007.0.0");
		showLink(text, url);
		ChatUtils.component(component);
	}
	
	// See BackgroundRendererMixin, LightTextureManagerMixin, WorldRendererMixin
}
