/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.InventoryUtils;

@SearchTags({"auto leave", "AutoDisconnect", "auto disconnect", "AutoQuit",
	"auto quit"})
public final class AutoLeaveHack extends Hack implements UpdateListener
{
	private final SliderSetting health =
		new SliderSetting("生命值", "当您的生命值达到此值或低于此值时离开服务器。", 4, 0.5, 9.5, 0.5,
			ValueDisplay.DECIMAL.withSuffix(" 心"));
	
	public final EnumSetting<Mode> mode = new EnumSetting<>("模式",
		"\u00a7l退出\u00a7r 模式只是正常退出游戏。\n" + "绕过 NoCheat+ 但不绕过 CombatLog。\n\n"
			+ "\u00a7l字符\u00a7r 模式发送一个特殊的聊天消息，" + " 使服务器将您踢出。\n"
			+ "绕过 NoCheat+ 和某些版本的 CombatLog。\n\n" + "\u00a7l自我伤害\u00a7r 模式发送攻击"
			+ " 另一个玩家的包，但将自己设为攻击者和" + " 目标，使服务器将您踢出。\n"
			+ "绕过 CombatLog 和 NoCheat+。",
		Mode.values(), Mode.QUIT);
	
	private final CheckboxSetting disableAutoReconnect = new CheckboxSetting(
		"禁用自动重连", "当 " + " AutoLeave 使您离开服务器时自动关闭自动重连。", true);
	
	private final SliderSetting totems = new SliderSetting("图腾",
		"Won't leave the server until the number of totems you have reaches"
			+ " 此值或低于此值。\n\n" + "11 = 始终可以离开",
		11, 0, 11, 1, ValueDisplay.INTEGER.withSuffix(" 图腾")
			.withLabel(1, "1 图腾").withLabel(11, " 忽略"));
	
	public AutoLeaveHack()
	{
		super("自动逃逸");
		setCategory(Category.COMBAT);
		addSetting(health);
		addSetting(mode);
		addSetting(disableAutoReconnect);
		addSetting(totems);
	}
	
	@Override
	public String getRenderName()
	{
		if(MC.player.getAbilities().creativeMode)
			return getName() + " (暂停)";
		
		return getName() + " [" + mode.getSelected() + "]";
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check gamemode
		if(MC.player.getAbilities().creativeMode)
			return;
		
		// check health
		float currentHealth = MC.player.getHealth();
		if(currentHealth <= 0F || currentHealth > health.getValueF() * 2F)
			return;
		
		// check totems
		if(totems.getValueI() < 11 && InventoryUtils
			.count(Items.TOTEM_OF_UNDYING, 40, true) > totems.getValueI())
			return;
		
		// leave server
		mode.getSelected().leave.run();
		
		// disable
		setEnabled(false);
		
		if(disableAutoReconnect.isChecked())
			WURST.getHax().autoReconnectHack.setEnabled(false);
	}
	
	public static enum Mode
	{
		QUIT("退出", () -> MC.world.disconnect()),
		
		CHARS("字符", () -> MC.getNetworkHandler().sendChatMessage("\u00a7")),
		
		SELFHURT("自我伤害",
			() -> MC.getNetworkHandler()
				.sendPacket(PlayerInteractEntityC2SPacket.attack(MC.player,
					MC.player.isSneaking())));
		
		private final String name;
		private final Runnable leave;
		
		private Mode(String name, Runnable leave)
		{
			this.name = name;
			this.leave = leave;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
