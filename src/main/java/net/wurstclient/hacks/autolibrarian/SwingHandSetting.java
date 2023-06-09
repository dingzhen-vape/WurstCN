/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autolibrarian;

import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.EnumSetting;

public final class SwingHandSetting
	extends EnumSetting<SwingHandSetting.SwingHand>
{
	protected static final MinecraftClient MC = WurstClient.MC;
	
	public SwingHandSetting()
	{
		super("挥动手臂", "在与村民和工作站互动时如何挥动手臂。\n\n"
			+ "\u00a7l关闭\u00a7r - 不要挥动手臂。会被反作弊插件检测到。\n\n"
			+ "\u00a7l服务器端\u00a7r - 在服务器端挥动手臂，而不在客户端播放动画。\n\n"
			+ "\u00a7l客户端\u00a7r - 在客户端挥动手臂。这是最合法的选项。", SwingHand.values(),
			SwingHand.SERVER);
	}
	
	public enum SwingHand
	{
		OFF("关闭", hand -> {}),
		
		SERVER("服务器端",
			hand -> MC.player.networkHandler
				.sendPacket(new HandSwingC2SPacket(hand))),
		
		CLIENT("客户端", hand -> MC.player.swingHand(hand));
		private String name;
		private Consumer<Hand> swing;
		
		private SwingHand(String name, Consumer<Hand> swing)
		{
			this.name = name;
			this.swing = swing;
		}
		
		public void swing(Hand hand)
		{
			swing.accept(hand);
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
