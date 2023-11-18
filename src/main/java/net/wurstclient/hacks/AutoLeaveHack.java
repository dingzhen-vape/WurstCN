/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"auto leave", "AutoDisconnect", "auto disconnect", "AutoQuit",
	"auto quit"})
public final class AutoLeaveHack extends Hack implements UpdateListener
{
	private final SliderSetting health = new SliderSetting("生命值",
		"当你的生命值达到或低于这个值时，离开服务器。",
		4, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL.withSuffix(" 心"));
	
	public final EnumSetting<Mode> mode = new EnumSetting<>("模式",
		"\u00a7l退出\u00a7r模式只是正常地退出游戏。\n"
			+ "绕过NoCheat+，但不绕过CombatLog。\n\n"
			+ "\u00a7l字符\u00a7r模式发送一个特殊的聊天消息，让服务器把你踢出去。\n"
			+ "绕过NoCheat+和一些版本的CombatLog。\n\n"
			+ "\u00a7lTP\u00a7r模式把你传送到一个无效的位置，让服务器把你踢出去。\n"
			+ "绕过CombatLog，但不绕过NoCheat+。\n\n"
			+ "\u00a7l自残\u00a7r模式发送攻击另一个玩家的数据包，但是把自己作为攻击者和目标。这会让服务器把你踢出去。\n"
			+ "绕过CombatLog和NoCheat+。",
		Mode.values(), Mode.QUIT);
	
	private final CheckboxSetting disableAutoReconnect = new CheckboxSetting(
		"禁用自动重连", "当"
			+ " AutoLeave让你离开服务器时，自动关闭AutoReconnect。",
		true);
	
	public AutoLeaveHack()
	{
		super("自动逃逸");
		setCategory(Category.COMBAT);
		addSetting(health);
		addSetting(mode);
		addSetting(disableAutoReconnect);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + mode.getSelected() + "]";
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check gamemode
		if(MC.player.getAbilities().creativeMode)
			return;
		
		// check for other players
		if(MC.isInSingleplayer()
			&& MC.player.networkHandler.getPlayerList().size() == 1)
			return;
		
		// check health
		float currentHealth = MC.player.getHealth();
		if(currentHealth <= 0F || currentHealth > health.getValueF() * 2F)
			return;
		
		// leave server
		switch(mode.getSelected())
		{
			case QUIT:
			MC.world.disconnect();
			break;
			
			case CHARS:
			MC.getNetworkHandler().sendChatMessage("\u00a7");
			break;
			
			case TELEPORT:
			MC.player.networkHandler
				.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(3.1e7,
					100, 3.1e7, false));
			break;
			
			case SELFHURT:
			MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket
				.attack(MC.player, MC.player.isSneaking()));
			break;
		}
		
		// disable
		setEnabled(false);
		
		if(disableAutoReconnect.isChecked())
			WURST.getHax().autoReconnectHack.setEnabled(false);
	}
	
	public static enum Mode
	{
		QUIT("退出"),
		
		CHARS("字符"),
		
		TELEPORT("TP"),
		
		SELFHURT("自残");
		
		private final String name;
		
		private Mode(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
