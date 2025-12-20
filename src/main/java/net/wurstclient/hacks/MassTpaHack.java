/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.StringUtil;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.DontSaveState;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.TextFieldSetting;
import net.wurstclient.util.ChatUtils;

@SearchTags({"mass tpa"})
@DontSaveState
public final class MassTpaHack extends Hack
	implements UpdateListener, ChatInputListener
{
	private static final Pattern ALLOWED_COMMANDS =
		Pattern.compile("^/+[a-zA-Z0-9_\\-]+$");
	
	private final TextFieldSetting commandSetting =
		new TextFieldSetting("命令",
			"用于传送的命令。\n"
				+ "例子: /tp, /tpa, /tpahere, /tpo",
			"/tpa",
			s -> s.length() < 64 && ALLOWED_COMMANDS.matcher(s).matches());
	
	private final SliderSetting delay = new SliderSetting("延迟",
		"每次传送请求之间的延迟。", 20, 1, 200, 1,
		ValueDisplay.INTEGER.withSuffix(" 滴答").withLabel(1, "1 滴答"));
	
	private final CheckboxSetting ignoreErrors =
		new CheckboxSetting("忽略错误",
			"是否忽略服务器告诉你的消息，说"
				+ " teleportation command isn't valid or that you don't have"
				+ "当被接受时停止",
			false);
	
	private final CheckboxSetting stopWhenAccepted = new CheckboxSetting(
		"是否在有人接受你的传送", "是否在有人接受你的传送请求时停止发送更多的传送请求。"
			+ "当有人接受你的传送请求时停止发送更多的传送请求。",
		true);
	
	private final Random random = new Random();
	private final ArrayList<String> players = new ArrayList<>();
	
	private String command;
	private int index;
	private int timer;
	
	public MassTpaHack()
	{
		super("Tap光环");
		setCategory(Category.CHAT);
		addSetting(commandSetting);
		addSetting(delay);
		addSetting(ignoreErrors);
		addSetting(stopWhenAccepted);
	}
	
	@Override
	protected void onEnable()
	{
		// reset state
		players.clear();
		index = 0;
		timer = 0;
		
		// cache command in case the setting is changed mid-run
		command = commandSetting.getValue().substring(1);
		
		// collect player names
		String playerName = MC.getUser().getName();
		for(PlayerInfo info : MC.player.connection.getOnlinePlayers())
		{
			String name = info.getProfile().name();
			name = StringUtil.stripColor(name);
			
			if(name.equalsIgnoreCase(playerName))
				continue;
			
			players.add(name);
		}
		
		Collections.shuffle(players, random);
		
		EVENTS.add(ChatInputListener.class, this);
		EVENTS.add(UpdateListener.class, this);
		
		if(players.isEmpty())
		{
			ChatUtils.error("Couldn't find any players.");
			setEnabled(false);
		}
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(ChatInputListener.class, this);
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		if(index >= players.size())
		{
			setEnabled(false);
			return;
		}
		
		MC.getConnection().sendCommand(command + " " + players.get(index));
		
		index++;
		timer = delay.getValueI() - 1;
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = event.getComponent().getString().toLowerCase();
		if(message.startsWith("\u00a7c[\u00a76wurst\u00a7c]"))
			return;
		
		if(message.contains("/help") || message.contains("权限"))
		{
			if(ignoreErrors.isChecked())
				return;
			
			event.cancel();
			ChatUtils.error("This server doesn't have a "
				+ command.toUpperCase() + "支持该命令。");
			setEnabled(false);
			
		}else if(message.contains("接受") && message.contains("请求")
			|| message.contains("接受") && message.contains("请求"))
		{
			if(!stopWhenAccepted.isChecked())
				return;
			
			event.cancel();
			ChatUtils.message("有人接受了你的" + command.toUpperCase()
				+ " request. Stopping.");
			setEnabled(false);
		}
	}
}
