/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.wurstclient.WurstClient;
import net.wurstclient.analytics.PlausibleAnalytics;
import net.wurstclient.commands.FriendsCmd;
import net.wurstclient.hacks.XRayHack;
import net.wurstclient.other_features.VanillaSpoofOtf;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.WurstColors;

public class WurstOptionsScreen extends Screen
{
	private Screen prevScreen;
	
	public WurstOptionsScreen(Screen prevScreen)
	{
		super(Component.literal(""));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		addRenderableWidget(Button
			.builder(Component.literal("返回"),
				b -> minecraft.setScreen(prevScreen))
			.bounds(width / 2 - 100, height / 4 + 144 - 16, 200, 20).build());
		
		addSettingButtons();
		addManagerButtons();
		addLinkButtons();
	}
	
	private void addSettingButtons()
	{
		WurstClient wurst = WurstClient.INSTANCE;
		FriendsCmd friendsCmd = wurst.getCmds().friendsCmd;
		CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
		PlausibleAnalytics plausible = wurst.getPlausible();
		VanillaSpoofOtf vanillaSpoofOtf = wurst.getOtfs().vanillaSpoofOtf;
		CheckboxSetting forceEnglish =
			wurst.getOtfs().translationsOtf.getForceEnglish();
		
		new WurstOptionsButton(-154, 24,
			() -> "点击好友: "
				+ (middleClickFriends.isChecked() ? "开启" : "关闭"),
			middleClickFriends.getWrappedDescription(200),
			b -> middleClickFriends
				.setChecked(!middleClickFriends.isChecked()));
		
		new WurstOptionsButton(-154, 48,
			() -> "统计用户: " + (plausible.isEnabled() ? "开启" : "关闭"),
			"统计有多少人使用Wurst及其版本"
				+ " most popular. This data helps me to decide when I can stop"
				+ " supporting old versions.\n\n"
				+ "These statistics are completely anonymous, never sold, and"
				+ " stay in the EU (I'm self-hosting Plausible in Germany)."
				+ " There are no cookies or persistent identifiers"
				+ " (see plausible.io).",
			b -> plausible.setEnabled(!plausible.isEnabled()));
		
		new WurstOptionsButton(-154, 72,
			() -> "伪装成官方: "
				+ (vanillaSpoofOtf.isEnabled() ? "开启" : "关闭"),
			vanillaSpoofOtf.getDescription(),
			b -> vanillaSpoofOtf.doPrimaryAction());
		
		new WurstOptionsButton(-154, 96,
			() -> "翻译: " + (!forceEnglish.isChecked() ? "开启" : "关闭"),
			"允许Wurst中的文本以其他语言显示，"
				+ " 除了英语。它将使用Minecraft设置的"
				+ " 语言。\n\n" + "这是一个实验性功能！",
			b -> forceEnglish.setChecked(!forceEnglish.isChecked()));
	}
	
	private void addManagerButtons()
	{
		XRayHack xRayHack = WurstClient.INSTANCE.getHax().xRayHack;
		
		new WurstOptionsButton(-50, 24, () -> "快捷键",
			"快捷键允许您通过简单地按下按钮来切换任何作弊或命令"
				+ " 按钮。",
			b -> minecraft.setScreen(new KeybindManagerScreen(this)));
		
		new WurstOptionsButton(-50, 48, () -> "X射线方块",
			"管理X射线将显示的方块。",
			b -> xRayHack.openBlockListEditor(this));
		
		new WurstOptionsButton(-50, 72, () -> "缩放",
			"缩放管理器允许你更改缩放键以及缩放的距离"
				+ "将缩放到的程度。",
			b -> minecraft.setScreen(new ZoomManagerScreen(this)));
	}
	
	private void addLinkButtons()
	{
		OS os = Util.getPlatform();
		
		new WurstOptionsButton(54, 24, () -> "官方网站",
			"§n§lWurstClient.net",
			b -> os.openUri("https://www.wurstclient.net/options-website/"));
		
		new WurstOptionsButton(54, 48, () -> "Wurst维基", "§n§lWurst.Wiki",
			b -> os.openUri("https://www.wurstclient.net/options-wiki/"));
		
		new WurstOptionsButton(54, 72, () -> "Wurst论坛", "§n§lWurstForum.net",
			b -> os.openUri("https://www.wurstclient.net/options-forum/"));
		
		new WurstOptionsButton(54, 96, () -> "推特", "@Wurst_Imperium",
			b -> os.openUri("https://www.wurstclient.net/options-twitter/"));
		
		new WurstOptionsButton(54, 120, () -> "捐赠",
			"§n§lWurstClient.net/donate\n"
				+ "现在捐赠以帮助我保持Wurst客户端活跃并免费"
				+ "供所有人使用。\n\n"
				+ "每一笔捐款都帮助很大，非常感激！你还可以获得"
				+ "一些额外的奖励。",
			b -> os.openUri("https://www.wurstclient.net/options-donate/"));
	}
	
	@Override
	public void onClose()
	{
		minecraft.setScreen(prevScreen);
	}
	
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderTitles(context);
		
		for(Renderable drawable : renderables)
			drawable.render(context, mouseX, mouseY, partialTicks);
		
		renderButtonTooltip(context, mouseX, mouseY);
	}
	
	private void renderTitles(GuiGraphics context)
	{
		Font tr = minecraft.font;
		int middleX = width / 2;
		int y1 = 40;
		int y2 = height / 4 + 24 - 28;
		
		context.drawCenteredString(tr, "Wurst选项", middleX, y1,
			CommonColors.WHITE);
		
		context.drawCenteredString(tr, "设置", middleX - 104, y2,
			WurstColors.VERY_LIGHT_GRAY);
		context.drawCenteredString(tr, "管理器", middleX, y2,
			WurstColors.VERY_LIGHT_GRAY);
		context.drawCenteredString(tr, "链接", middleX + 104, y2,
			WurstColors.VERY_LIGHT_GRAY);
	}
	
	private void renderButtonTooltip(GuiGraphics context, int mouseX,
		int mouseY)
	{
		for(AbstractWidget button : Screens.getButtons(this))
		{
			if(!button.isHoveredOrFocused()
				|| !(button instanceof WurstOptionsButton))
				continue;
			
			WurstOptionsButton woButton = (WurstOptionsButton)button;
			
			if(woButton.tooltip.isEmpty())
				continue;
			
			context.setComponentTooltipForNextFrame(font, woButton.tooltip,
				mouseX, mouseY);
			break;
		}
	}
	
	private final class WurstOptionsButton extends Button
	{
		private final Supplier<String> messageSupplier;
		private final List<net.minecraft.network.chat.Component> tooltip;
		
		public WurstOptionsButton(int xOffset, int yOffset,
			Supplier<String> messageSupplier, String tooltip,
			OnPress pressAction)
		{
			super(WurstOptionsScreen.this.width / 2 + xOffset,
				WurstOptionsScreen.this.height / 4 - 16 + yOffset, 100, 20,
				net.minecraft.network.chat.Component
					.literal(messageSupplier.get()),
				pressAction, Button.DEFAULT_NARRATION);
			
			this.messageSupplier = messageSupplier;
			
			if(tooltip.isEmpty())
				this.tooltip = Arrays.asList();
			else
			{
				String[] lines = ChatUtils.wrapText(tooltip, 200).split("\n");
				
				net.minecraft.network.chat.Component[] lines2 =
					new net.minecraft.network.chat.Component[lines.length];
				for(int i = 0; i < lines.length; i++)
					lines2[i] =
						net.minecraft.network.chat.Component.literal(lines[i]);
				
				this.tooltip = Arrays.asList(lines2);
			}
			
			addRenderableWidget(this);
		}
		
		@Override
		public void onPress(InputWithModifiers context)
		{
			super.onPress(context);
			setMessage(net.minecraft.network.chat.Component
				.literal(messageSupplier.get()));
		}
		
		@Override
		protected void renderContents(GuiGraphics drawContext, int i, int j,
			float f)
		{
			renderDefaultSprite(drawContext);
			renderDefaultLabel(drawContext.textRendererForWidget(this,
				GuiGraphics.HoveredTextEffects.NONE));
		}
	}
}
