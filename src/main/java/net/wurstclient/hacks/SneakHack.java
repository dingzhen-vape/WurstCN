/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.PostMotionListener;
import net.wurstclient.events.PreMotionListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;

@SearchTags({"AutoSneaking"})
public final class SneakHack extends Hack
	implements PreMotionListener, PostMotionListener
{
	private final EnumSetting<SneakMode> mode = new EnumSetting<>("模式",
		"\u00a7l数据包\u00a7r 模式让你看起来像是在潜行,但不会让你变慢。\n"
			+ "\u00a7l合法\u00a7r 模式真的让你潜行。",
		SneakMode.values(), SneakMode.LEGIT);
	
	private final CheckboxSetting offWhileFlying = new CheckboxSetting("飞行时关闭",
		"自动在你飞行或使用" + "自由视角时禁用合法潜行,这样它就不会强制你向下飞。\n\n"
			+ "请记住,这也意味着你在做这些事情时不会对其他玩家隐藏起来。" + "潜行",
		false);
	
	public SneakHack()
	{
		super("保持潜行");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(offWhileFlying);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " ]" + mode.getSelected() + "数据包";
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(PreMotionListener.class, this);
		EVENTS.add(PostMotionListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(PreMotionListener.class, this);
		EVENTS.remove(PostMotionListener.class, this);
		
		switch(mode.getSelected())
		{
			case LEGIT:
			IKeyBinding.get(MC.options.sneakKey).resetPressedState();
			break;
			
			case PACKET:
			sendSneakPacket(Mode.RELEASE_SHIFT_KEY);
			break;
		}
	}
	
	@Override
	public void onPreMotion()
	{
		IKeyBinding sneakKey = IKeyBinding.get(MC.options.sneakKey);
		
		switch(mode.getSelected())
		{
			case LEGIT:
			if(offWhileFlying.isChecked() && isFlying())
				sneakKey.resetPressedState();
			else
				sneakKey.setPressed(true);
			break;
			
			case PACKET:
			sneakKey.resetPressedState();
			sendSneakPacket(Mode.PRESS_SHIFT_KEY);
			sendSneakPacket(Mode.RELEASE_SHIFT_KEY);
			break;
		}
	}
	
	@Override
	public void onPostMotion()
	{
		if(mode.getSelected() != SneakMode.PACKET)
			return;
		
		sendSneakPacket(Mode.RELEASE_SHIFT_KEY);
		sendSneakPacket(Mode.PRESS_SHIFT_KEY);
	}
	
	private boolean isFlying()
	{
		if(MC.player.getAbilities().flying)
			return true;
		
		if(WURST.getHax().flightHack.isEnabled())
			return true;
		
		if(WURST.getHax().freecamHack.isEnabled())
			return true;
		
		return false;
	}
	
	private void sendSneakPacket(Mode mode)
	{
		ClientPlayerEntity player = MC.player;
		ClientCommandC2SPacket packet =
			new ClientCommandC2SPacket(player, mode);
		player.networkHandler.sendPacket(packet);
	}
	
	private enum SneakMode
	{
		PACKET("合法"),
		LEGIT("Legit");
		
		private final String name;
		
		private SneakMode(String name)
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
