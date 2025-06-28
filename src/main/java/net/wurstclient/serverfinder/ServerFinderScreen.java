/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.serverfinder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.wurstclient.mixinterface.IMultiplayerScreen;
import net.wurstclient.util.MathUtils;

public class ServerFinderScreen extends Screen
{
	private final MultiplayerScreen prevScreen;
	
	private TextFieldWidget ipBox;
	private TextFieldWidget maxThreadsBox;
	private ButtonWidget searchButton;
	
	private ServerFinderState state;
	private int maxThreads;
	private int checked;
	private int working;
	
	public ServerFinderScreen(MultiplayerScreen prevScreen)
	{
		super(Text.literal("服务器查找器"));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		addDrawableChild(searchButton =
			ButtonWidget.builder(Text.literal("寻找方块"), b -> searchOrCancel())
				.dimensions(width / 2 - 100, height / 4 + 96 + 12, 200, 20)
				.build());
		searchButton.active = false;
		
		addDrawableChild(
			ButtonWidget
				.builder(Text.literal("教程"),
					b -> Util.getOperatingSystem().open(
						"https://www.wurstclient.net/serverfinder-tutorial/"))
				.dimensions(width / 2 - 100, height / 4 + 120 + 12, 200, 20)
				.build());
		
		addDrawableChild(ButtonWidget.builder(Text.literal("返回"), b -> close())
			.dimensions(width / 2 - 100, height / 4 + 144 + 12, 200, 20)
			.build());
		
		ipBox = new TextFieldWidget(textRenderer, width / 2 - 100,
			height / 4 + 34, 200, 20, Text.empty());
		ipBox.setMaxLength(200);
		addSelectableChild(ipBox);
		setFocused(ipBox);
		
		maxThreadsBox = new TextFieldWidget(textRenderer, width / 2 - 32,
			height / 4 + 58, 26, 12, Text.empty());
		maxThreadsBox.setMaxLength(3);
		maxThreadsBox.setText("128");
		addSelectableChild(maxThreadsBox);
		
		state = ServerFinderState.NOT_RUNNING;
	}
	
	private void searchOrCancel()
	{
		if(state.isRunning())
		{
			state = ServerFinderState.CANCELLED;
			ipBox.active = true;
			maxThreadsBox.active = true;
			searchButton.setMessage(Text.literal("寻找方块"));
			return;
		}
		
		state = ServerFinderState.RESOLVING;
		maxThreads = Integer.parseInt(maxThreadsBox.getText());
		ipBox.active = false;
		maxThreadsBox.active = false;
		searchButton.setMessage(Text.literal("取消"));
		checked = 0;
		working = 0;
		
		new Thread(this::findServers, "服务器查找器").start();
	}
	
	private void findServers()
	{
		try
		{
			InetAddress addr =
				InetAddress.getByName(ipBox.getText().split(":")[0].trim());
			
			int[] ipParts = new int[4];
			for(int i = 0; i < 4; i++)
				ipParts[i] = addr.getAddress()[i] & 0xff;
			
			state = ServerFinderState.SEARCHING;
			ArrayList<WurstServerPinger> pingers = new ArrayList<>();
			int[] changes = {0, 1, -1, 2, -2, 3, -3};
			for(int change : changes)
				for(int i2 = 0; i2 <= 255; i2++)
				{
					if(state == ServerFinderState.CANCELLED)
						return;
					
					int[] ipParts2 = ipParts.clone();
					ipParts2[2] = ipParts[2] + change & 0xff;
					ipParts2[3] = i2;
					String ip = ipParts2[0] + "." + ipParts2[1] + "."
						+ ipParts2[2] + "." + ipParts2[3];
					
					WurstServerPinger pinger = new WurstServerPinger();
					pinger.ping(ip);
					pingers.add(pinger);
					while(pingers.size() >= maxThreads)
					{
						if(state == ServerFinderState.CANCELLED)
							return;
						
						updatePingers(pingers);
					}
				}
			while(pingers.size() > 0)
			{
				if(state == ServerFinderState.CANCELLED)
					return;
				
				updatePingers(pingers);
			}
			state = ServerFinderState.DONE;
			
		}catch(UnknownHostException e)
		{
			state = ServerFinderState.UNKNOWN_HOST;
			
		}catch(Exception e)
		{
			e.printStackTrace();
			state = ServerFinderState.ERROR;
		}
	}
	
	private void updatePingers(ArrayList<WurstServerPinger> pingers)
	{
		for(int i = 0; i < pingers.size(); i++)
		{
			WurstServerPinger pinger = pingers.get(i);
			if(pinger.isStillPinging())
				continue;
			
			checked++;
			if(pinger.isWorking())
			{
				working++;
				String name = "Grief我 #" + working;
				String ip = pinger.getServerIP();
				addServerToList(name, ip);
			}
			
			pingers.remove(i);
			i--;
		}
	}
	
	// Basically what MultiplayerScreen.addEntry() does,
	// but without changing the current screen.
	private void addServerToList(String name, String ip)
	{
		ServerList serverList = prevScreen.getServerList();
		if(serverList.get(ip) != null)
			return;
		
		serverList.add(new ServerInfo(name, ip, ServerType.OTHER), false);
		serverList.saveFile();
		
		MultiplayerServerListWidget selector =
			((IMultiplayerScreen)prevScreen).getServerListSelector();
		selector.setSelected(null);
		selector.setServers(serverList);
	}
	
	@Override
	public void tick()
	{
		searchButton.active = MathUtils.isInteger(maxThreadsBox.getText())
			&& !ipBox.getText().isEmpty();
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int int_3)
	{
		if(keyCode == GLFW.GLFW_KEY_ENTER)
			searchButton.onPress();
		
		return super.keyPressed(keyCode, scanCode, int_3);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_4)
		{
			close();
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY,
		float partialTicks)
	{
		context.drawCenteredTextWithShadow(textRenderer, "服务器查找器", width / 2,
			20, 16777215);
		context.drawCenteredTextWithShadow(textRenderer, "这将搜索与您输入的IP地址相似的服务器",
			width / 2, 40, 10526880);
		context.drawCenteredTextWithShadow(textRenderer, "到您在下方字段中输入的IP地址。",
			width / 2, 50, 10526880);
		context.drawCenteredTextWithShadow(textRenderer,
			"它找到的服务器将被添加到您的服务器列表中。", width / 2, 60, 10526880);
		
		context.drawTextWithShadow(textRenderer, "服务器地址：", width / 2 - 100,
			height / 4 + 24, 10526880);
		ipBox.render(context, mouseX, mouseY, partialTicks);
		
		context.drawTextWithShadow(textRenderer, "最大线程数：", width / 2 - 100,
			height / 4 + 60, 10526880);
		maxThreadsBox.render(context, mouseX, mouseY, partialTicks);
		
		context.drawCenteredTextWithShadow(textRenderer, state.toString(),
			width / 2, height / 4 + 73, 10526880);
		
		context.drawTextWithShadow(textRenderer, "已检查：" + checked + " / 1792",
			width / 2 - 100, height / 4 + 84, 10526880);
		context.drawTextWithShadow(textRenderer, "正在工作：" + working,
			width / 2 - 100, height / 4 + 94, 10526880);
		
		for(Drawable drawable : drawables)
			drawable.render(context, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void close()
	{
		state = ServerFinderState.CANCELLED;
		client.setScreen(prevScreen);
	}
	
	enum ServerFinderState
	{
		NOT_RUNNING(""),
		SEARCHING("\u00a72Searching..."),
		RESOLVING("\u00a72解析中..."),
		UNKNOWN_HOST("\u00a74未知主机！"),
		CANCELLED("\u00a74已取消！"),
		DONE("\u00a72完成！"),
		ERROR("\u00a74发生错误！");
		
		private final String name;
		
		private ServerFinderState(String name)
		{
			this.name = name;
		}
		
		public boolean isRunning()
		{
			return this == SEARCHING || this == RESOLVING;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
