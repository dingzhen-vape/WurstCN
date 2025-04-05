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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OperatingSystem;
import net.wurstclient.WurstClient;
import net.wurstclient.analytics.WurstAnalytics;
import net.wurstclient.commands.FriendsCmd;
import net.wurstclient.hacks.XRayHack;
import net.wurstclient.other_features.VanillaSpoofOtf;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

public class WurstOptionsScreen extends Screen
{
	private Screen prevScreen;
	
	public WurstOptionsScreen(Screen prevScreen)
	{
		super(Text.literal(""));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		addDrawableChild(ButtonWidget
			.builder(Text.literal("返回"), b -> client.setScreen(prevScreen))
			.dimensions(width / 2 - 100, height / 4 + 144 - 16, 200, 20)
			.build());
		
		addSettingButtons();
		addManagerButtons();
		addLinkButtons();
	}
	
	private void addSettingButtons()
	{
		WurstClient wurst = WurstClient.INSTANCE;
		FriendsCmd friendsCmd = wurst.getCmds().friendsCmd;
		CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
		WurstAnalytics analytics = wurst.getAnalytics();
		VanillaSpoofOtf vanillaSpoofOtf = wurst.getOtfs().vanillaSpoofOtf;
		CheckboxSetting forceEnglish =
			wurst.getOtfs().translationsOtf.getForceEnglish();
		
		new WurstOptionsButton(-154, 24,
			() -> "点击好友: " + (middleClickFriends.isChecked() ? "开启" : "关闭"),
			middleClickFriends.getWrappedDescription(200),
			b -> middleClickFriends
				.setChecked(!middleClickFriends.isChecked()));
		
		new WurstOptionsButton(-154, 48,
			() -> "统计用户: " + (analytics.isEnabled() ? "开启" : "关闭"),
			"统计有多少人使用Wurst及其版本" + "的最受欢迎程度。我们使用这些数据来决定何时停止"
				+ " 支持旧版本的Minecraft。\n\n" + "我们使用随机ID来区分用户，以确保这些数据"
				+ " 永远不能链接到您的Minecraft账户。随机ID" + " 每3天更改一次，以确保您仍然" + " 匿名。",
			b -> analytics.setEnabled(!analytics.isEnabled()));
		
		new WurstOptionsButton(-154, 72,
			() -> "伪装成官方: " + (vanillaSpoofOtf.isEnabled() ? "开启" : "关闭"),
			vanillaSpoofOtf.getDescription(),
			b -> vanillaSpoofOtf.doPrimaryAction());
		
		new WurstOptionsButton(-154, 96,
			() -> "翻译: " + (!forceEnglish.isChecked() ? "开启" : "关闭"),
			"允许Wurst中的文本以其他语言显示，" + " 除了英语。它将使用Minecraft设置的" + " 语言。\n\n"
				+ "这是一个实验性功能！",
			b -> forceEnglish.setChecked(!forceEnglish.isChecked()));
	}
	
	private void addManagerButtons()
	{
		XRayHack xRayHack = WurstClient.INSTANCE.getHax().xRayHack;
		
		new WurstOptionsButton(-50, 24, () -> "快捷键",
			"快捷键允许您通过简单地按下按钮来切换任何作弊或命令" + " 按钮。",
			b -> client.setScreen(new KeybindManagerScreen(this)));
		
		new WurstOptionsButton(-50, 48, () -> "X射线方块", "管理X射线将显示的方块。",
			b -> xRayHack.openBlockListEditor(this));
		
		new WurstOptionsButton(-50, 72, () -> "缩放",
			"缩放管理器允许你更改缩放键以及缩放的距离" + "将缩放到的程度。",
			b -> client.setScreen(new ZoomManagerScreen(this)));
	}
	
	private void addLinkButtons()
	{
		OperatingSystem os = Util.getOperatingSystem();
		
		new WurstOptionsButton(54, 24, () -> "官方网站", "§n§lWurstClient.net",
			b -> os.open(
				"https://www.wurstclient.net/?utm_source=Wurst+Client&utm_medium=Wurst+Options&utm_content=Official+Website"));
		
		new WurstOptionsButton(54, 48, () -> "Wurst维基",
			"§n§lWurst.Wiki\n" + "我们正在寻找志愿者帮助我们扩展" + "维基并保持其与最新Wurst更新同步。",
			b -> os.open(
				"https://wurst.wiki/?utm_source=Wurst+Client&utm_medium=Wurst+Options&utm_content=Wurst+Wiki"));
		
		new WurstOptionsButton(54, 72, () -> "Wurst论坛", "§n§lWurstForum.net",
			b -> os.open(
				"https://wurstforum.net/?utm_source=Wurst+Client&utm_medium=Wurst+Options&utm_content=WurstForum"));
		
		new WurstOptionsButton(54, 96, () -> "推特", "@Wurst_Imperium",
			b -> os.open("https://www.wurstclient.net/twitter/"));
		
		new WurstOptionsButton(54, 120, () -> "捐赠",
			"§n§lWurstClient.net/donate\n" + "现在捐赠以帮助我保持Wurst客户端活跃并免费"
				+ "供所有人使用。\n\n" + "每一笔捐款都帮助很大，非常感激！你还可以获得" + "一些额外的奖励。",
			b -> os.open(
				"https://www.wurstclient.net/donate/?utm_source=Wurst+Client&utm_medium=Wurst+Options&utm_content=Donate"));
	}
	
	@Override
	public void close()
	{
		client.setScreen(prevScreen);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(context, mouseX, mouseY, partialTicks);
		renderTitles(context);
		
		for(Drawable drawable : drawables)
			drawable.render(context, mouseX, mouseY, partialTicks);
		
		renderButtonTooltip(context, mouseX, mouseY);
	}
	
	private void renderTitles(DrawContext context)
	{
		TextRenderer tr = client.textRenderer;
		int middleX = width / 2;
		int y1 = 40;
		int y2 = height / 4 + 24 - 28;
		
		context.drawCenteredTextWithShadow(tr, "Wurst选项", middleX, y1,
			0xffffff);
		
		context.drawCenteredTextWithShadow(tr, "设置", middleX - 104, y2,
			0xcccccc);
		context.drawCenteredTextWithShadow(tr, "管理器", middleX, y2, 0xcccccc);
		context.drawCenteredTextWithShadow(tr, "链接", middleX + 104, y2,
			0xcccccc);
	}
	
	private void renderButtonTooltip(DrawContext context, int mouseX,
		int mouseY)
	{
		for(ClickableWidget button : Screens.getButtons(this))
		{
			if(!button.isSelected() || !(button instanceof WurstOptionsButton))
				continue;
			
			WurstOptionsButton woButton = (WurstOptionsButton)button;
			
			if(woButton.tooltip.isEmpty())
				continue;
			
			context.drawTooltip(textRenderer, woButton.tooltip, mouseX, mouseY);
			break;
		}
	}
	
	private final class WurstOptionsButton extends ButtonWidget
	{
		private final Supplier<String> messageSupplier;
		private final List<Text> tooltip;
		
		public WurstOptionsButton(int xOffset, int yOffset,
			Supplier<String> messageSupplier, String tooltip,
			PressAction pressAction)
		{
			super(WurstOptionsScreen.this.width / 2 + xOffset,
				WurstOptionsScreen.this.height / 4 - 16 + yOffset, 100, 20,
				Text.literal(messageSupplier.get()), pressAction,
				ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
			
			this.messageSupplier = messageSupplier;
			
			if(tooltip.isEmpty())
				this.tooltip = Arrays.asList();
			else
			{
				String[] lines = ChatUtils.wrapText(tooltip, 200).split("\n");
				
				Text[] lines2 = new Text[lines.length];
				for(int i = 0; i < lines.length; i++)
					lines2[i] = Text.literal(lines[i]);
				
				this.tooltip = Arrays.asList(lines2);
			}
			
			addDrawableChild(this);
		}
		
		@Override
		public void onPress()
		{
			super.onPress();
			setMessage(Text.literal(messageSupplier.get()));
		}
	}
}
