/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.serverfinder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.wurstclient.mixinterface.IMultiplayerScreen;

public class CleanUpScreen extends Screen
{
	private MultiplayerScreen prevScreen;
	private ButtonWidget cleanUpButton;
	
	private boolean removeAll;
	private boolean cleanupFailed = true;
	private boolean cleanupOutdated = true;
	private boolean cleanupRename = true;
	private boolean cleanupUnknown = true;
	private boolean cleanupGriefMe;
	
	public CleanUpScreen(MultiplayerScreen prevScreen)
	{
		super(Text.literal(""));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		addDrawableChild(
			new CleanUpButton(width / 2 - 100, height / 4 + 168 + 12,
				() -> "取消", "", b -> client.setScreen(prevScreen)));
		
		addDrawableChild(
			cleanUpButton = new CleanUpButton(width / 2 - 100,
				height / 4 + 144 + 12, () -> "清理", "使用您上面指定的设置开始清理。\n"
					+ "您上面指定的设置。\n" + "游戏可能会看起来没有" + "响应，持续几秒钟。",
				b -> cleanUp()));
		
		addDrawableChild(new CleanUpButton(width / 2 - 100,
			height / 4 - 24 + 12, () -> "未知主机：" + removeOrKeep(cleanupUnknown),
			"Servers that clearly don't exist.",
			b -> cleanupUnknown = !cleanupUnknown));
		
		addDrawableChild(new CleanUpButton(width / 2 - 100, height / 4 + 0 + 12,
			() -> "过时的服务器：" + removeOrKeep(cleanupOutdated),
			"运行不同Minecraft\n" + "版本的服务器。",
			b -> cleanupOutdated = !cleanupOutdated));
		
		addDrawableChild(new CleanUpButton(width / 2 - 100,
			height / 4 + 24 + 12, () -> "Ping失败：" + removeOrKeep(cleanupFailed),
			"所有在最后一次Ping中失败的服务器。\n" + "请确保最后一次Ping已完成\n" + "在此之前。这意味着：返回，"
				+ "按下刷新按钮，等待直到" + "所有服务器完成刷新。",
			b -> cleanupFailed = !cleanupFailed));
		
		addDrawableChild(
			new CleanUpButton(width / 2 - 100, height / 4 + 48 + 12,
				() -> "\"Grief me\" 服务器：" + removeOrKeep(cleanupGriefMe),
				"所有名称以\"Grief me\"\n" + "有用，用于删除ServerFinder找到的服务器。",
				b -> cleanupGriefMe = !cleanupGriefMe));
		
		addDrawableChild(new CleanUpButton(width / 2 - 100,
			height / 4 + 72 + 12, () -> "\u00a7c删除所有服务器：" + yesOrNo(removeAll),
			"这将完全清除您的服务器\n" + "列表。\u00a7c谨慎使用！\u00a7r",
			b -> removeAll = !removeAll));
		
		addDrawableChild(new CleanUpButton(width / 2 - 100,
			height / 4 + 96 + 12, () -> "重命名所有服务器：" + yesOrNo(cleanupRename),
			"将您的服务器重命名为\"Grief me #1\",\n" + "\"Grief me #2\", 等。",
			b -> cleanupRename = !cleanupRename));
	}
	
	private String yesOrNo(boolean b)
	{
		return b ? "是" : "否";
	}
	
	private String removeOrKeep(boolean b)
	{
		return b ? "移除" : "保留";
	}
	
	private void cleanUp()
	{
		for(int i = prevScreen.getServerList().size() - 1; i >= 0; i--)
		{
			ServerInfo server = prevScreen.getServerList().get(i);
			
			if(removeAll || shouldRemove(server))
				prevScreen.getServerList().remove(server);
		}
		
		if(cleanupRename)
			for(int i = 0; i < prevScreen.getServerList().size(); i++)
			{
				ServerInfo server = prevScreen.getServerList().get(i);
				server.name = "Grief我 #" + (i + 1);
			}
		
		saveServerList();
		client.setScreen(prevScreen);
	}
	
	private boolean shouldRemove(ServerInfo server)
	{
		if(server == null)
			return false;
		
		if(cleanupUnknown && isUnknownHost(server))
			return true;
		
		if(cleanupOutdated && !isSameProtocol(server))
			return true;
		
		if(cleanupFailed && isFailedPing(server))
			return true;
		
		if(cleanupGriefMe && isGriefMeServer(server))
			return true;
		
		return false;
	}
	
	private boolean isUnknownHost(ServerInfo server)
	{
		if(server.label == null)
			return false;
		
		if(server.label.getString() == null)
			return false;
		
		return server.label.getString()
			.equals("\u00a74Can\'t resolve hostname");
	}
	
	private boolean isSameProtocol(ServerInfo server)
	{
		return server.protocolVersion == SharedConstants.getGameVersion()
			.getProtocolVersion();
	}
	
	private boolean isFailedPing(ServerInfo server)
	{
		return server.ping != -2L && server.ping < 0L;
	}
	
	private boolean isGriefMeServer(ServerInfo server)
	{
		return server.name != null && server.name.startsWith("Grief我");
	}
	
	private void saveServerList()
	{
		prevScreen.getServerList().saveFile();
		
		MultiplayerServerListWidget serverListSelector =
			((IMultiplayerScreen)prevScreen).getServerListSelector();
		
		serverListSelector.setSelected(null);
		serverListSelector.setServers(prevScreen.getServerList());
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int int_3)
	{
		if(keyCode == GLFW.GLFW_KEY_ENTER)
			cleanUpButton.onPress();
		
		return super.keyPressed(keyCode, scanCode, int_3);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(context, mouseX, mouseY, partialTicks);
		context.drawCenteredTextWithShadow(textRenderer, "清理", width / 2, 20,
			16777215);
		context.drawCenteredTextWithShadow(textRenderer, "请选择您要删除的服务器：",
			width / 2, 36, 10526880);
		
		for(Drawable drawable : drawables)
			drawable.render(context, mouseX, mouseY, partialTicks);
		
		renderButtonTooltip(context, mouseX, mouseY);
	}
	
	private void renderButtonTooltip(DrawContext context, int mouseX,
		int mouseY)
	{
		for(ClickableWidget button : Screens.getButtons(this))
		{
			if(!button.isSelected() || !(button instanceof CleanUpButton))
				continue;
			
			CleanUpButton cuButton = (CleanUpButton)button;
			
			if(cuButton.tooltip.isEmpty())
				continue;
			
			context.drawTooltip(textRenderer, cuButton.tooltip, mouseX, mouseY);
			break;
		}
	}
	
	private final class CleanUpButton extends ButtonWidget
	{
		private final Supplier<String> messageSupplier;
		private final List<Text> tooltip;
		
		public CleanUpButton(int x, int y, Supplier<String> messageSupplier,
			String tooltip, PressAction pressAction)
		{
			super(x, y, 200, 20, Text.literal(messageSupplier.get()),
				pressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
			this.messageSupplier = messageSupplier;
			
			if(tooltip.isEmpty())
				this.tooltip = Arrays.asList();
			else
			{
				String[] lines = tooltip.split("\n");
				
				Text[] lines2 = new Text[lines.length];
				for(int i = 0; i < lines.length; i++)
					lines2[i] = Text.literal(lines[i]);
				
				this.tooltip = Arrays.asList(lines2);
			}
		}
		
		@Override
		public void onPress()
		{
			super.onPress();
			setMessage(Text.literal(messageSupplier.get()));
		}
	}
}
