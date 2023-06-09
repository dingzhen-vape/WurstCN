/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
import net.wurstclient.mixinterface.IScreen;
import net.wurstclient.other_features.VanillaSpoofOtf;
import net.wurstclient.settings.CheckboxSetting;

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
			() -> "点击好友: "
				+ (middleClickFriends.isChecked() ? "开启" : "关闭"),
			middleClickFriends.getWrappedDescription(200),
			b -> middleClickFriends
				.setChecked(!middleClickFriends.isChecked()));
		
		new WurstOptionsButton(-154, 48,
			() -> "统计用户: " + (analytics.isEnabled() ? "开启" : "关闭"),
			"统计有多少人在使用Wurst\n"
				+ "以及哪些版本最受欢迎。\n"
				+ "我们用这些数据来决定何时停止\n"
				+ "支持旧的Minecraft版本。\n\n"
				+ "我们使用一个随机ID来区分用户\n"
				+ "这样这些数据就永远不会与\n"
				+ "你的Minecraft账号相关联。为了确保\n"
				+ "你的匿名性，随机ID每3天会\n"
				+ "改变一次。",
			b -> analytics.setEnabled(!analytics.isEnabled()));
		
		new WurstOptionsButton(-154, 72,
			() -> "伪装原版: "
				+ (vanillaSpoofOtf.isEnabled() ? "开启" : "关闭"),
			vanillaSpoofOtf.getWrappedDescription(200),
			b -> vanillaSpoofOtf.doPrimaryAction());
		
		new WurstOptionsButton(-154, 96,
			() -> "翻译: " + (!forceEnglish.isChecked() ? "开启" : "关闭"),
			"§c这是一个实验性的功能！\n"
				+ "我们还没有很多翻译。如果你\n"
				+ "会说英语和其他语言，请帮助我们\n"
				+ "添加更多的翻译。",
			b -> forceEnglish.setChecked(!forceEnglish.isChecked()));
	}
	
	private void addManagerButtons()
	{
		XRayHack xRayHack = WurstClient.INSTANCE.getHax().xRayHack;
		
		new WurstOptionsButton(-50, 24, () -> "按键绑定",
			"按键绑定让你可以通过简单地按下一个按钮来切换任何黑客功能或命令。",
			b -> client.setScreen(new KeybindManagerScreen(this)));
		
		new WurstOptionsButton(-50, 48, () -> "X-Ray方块",
			"管理X-Ray将显示的方块。",
			b -> xRayHack.openBlockListEditor(this));
		
		new WurstOptionsButton(-50, 72, () -> "缩放",
			"缩放管理器让你可以改变缩放键，缩放的距离和更多。",
			b -> client.setScreen(new ZoomManagerScreen(this)));
	}
	
	private void addLinkButtons()
	{
		OperatingSystem os = Util.getOperatingSystem();
		
		new WurstOptionsButton(54, 24, () -> "Official Website",
			"WurstClient.net", b -> os.open(
				"https://www.wurstclient.net/?utm_source=Wurst+Client&utm_medium=Wurst+Options&utm_content=Official+Website"));
		
		new WurstOptionsButton(54, 48, () -> "Wurst Wiki", "Wurst.Wiki",
			b -> os.open(
				"https://wurst.wiki/?utm_source=Wurst+Client&utm_medium=Wurst+Options&utm_content=Wurst+Wiki"));
		
		new WurstOptionsButton(54, 72, () -> "Twitter", "@Wurst_Imperium",
			b -> os.open("https://www.wurstclient.net/twitter/"));
		
		new WurstOptionsButton(54, 96, () -> "Reddit", "r/WurstClient",
			b -> os.open("https://www.wurstclient.net/reddit/"));
		
		new WurstOptionsButton(54, 120, () -> "Donate",
			"WurstClient.net/donate", b -> os.open(
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
		renderBackground(context);
		renderTitles(context);
		super.render(context, mouseX, mouseY, partialTicks);
		renderButtonTooltip(context, mouseX, mouseY);
	}
	
	private void renderTitles(DrawContext context)
	{
		TextRenderer tr = client.textRenderer;
		int middleX = width / 2;
		int y1 = 40;
		int y2 = height / 4 + 24 - 28;
		
		context.drawCenteredTextWithShadow(tr, "Wurst 设置", middleX, y1,
			0xffffff);
		
		context.drawCenteredTextWithShadow(tr, "设置", middleX - 104, y2,
			0xcccccc);
		context.drawCenteredTextWithShadow(tr, "编辑器", middleX, y2,
			0xcccccc);
		context.drawCenteredTextWithShadow(tr, "链接", middleX + 104, y2,
			0xcccccc);
	}
	
	private void renderButtonTooltip(DrawContext context, int mouseX,
		int mouseY)
	{
		for(Drawable d : ((IScreen)this).getButtons())
		{
			if(!(d instanceof ClickableWidget))
				continue;
			
			ClickableWidget button = (ClickableWidget)d;
			
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
				String[] lines = tooltip.split("\n");
				
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
