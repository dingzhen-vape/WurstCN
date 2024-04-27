/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.ChatUtils;

@SearchTags({"AntiBlindness", "NoBlindness", "anti blindness", "no blindness",
	"AntiDarkness", "NoDarkness", "anti darkness", "no darkness",
	"AntiWardenEffect", "anti warden effect", "NoWardenEffect",
	"no warden effect"})
public final class AntiBlind1Hack extends Hack
{
	private Text component;
	
	private void showLink(String text, String url)
	{
		ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
		component = Text.literal(text).styled(s -> s.withClickEvent(event));
	}
	
	public AntiBlind1Hack()
	{
		super("kono_yalu翻译");
		setCategory(Category.OTHER);
	}
	
	@Override
	public void onEnable()
	{
		String text =
			"译者主页链接(点我):https://space.bilibili.com/432060575?spm_id_from=333.1007.0.0";
		String url =
			"https://space.bilibili.com/432060575?spm_id_from=333.1007.0.0";
		showLink(text, url);
		ChatUtils.component(component);
	}
	
	// See BackgroundRendererMixin, LightTextureManagerMixin, WorldRendererMixin
}
