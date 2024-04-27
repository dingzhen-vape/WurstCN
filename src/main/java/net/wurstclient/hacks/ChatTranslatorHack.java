/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.GoogleTranslate;

@SearchTags({"chat translator", "ChatTranslate", "chat translate",
	"ChatTranslation", "chat translation", "AutoTranslate", "auto translate",
	"AutoTranslator", "auto translator", "AutoTranslation", "auto translation",
	"GoogleTranslate", "google translate", "GoogleTranslator",
	"google translator", "GoogleTranslation", "google translation"})
public final class ChatTranslatorHack extends Hack implements ChatInputListener
{
	private final EnumSetting<FromLanguage> langFrom = new EnumSetting<>(
		"从哪种语言翻译", FromLanguage.values(), FromLanguage.AUTO_DETECT);
	
	private final EnumSetting<ToLanguage> langTo =
		new EnumSetting<>("翻译成哪种语言", ToLanguage.values(), ToLanguage.ENGLISH);
	
	public ChatTranslatorHack()
	{
		super("聊天翻译");
		setCategory(Category.CHAT);
		
		addSetting(langFrom);
		addSetting(langTo);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(ChatInputListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(ChatInputListener.class, this);
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		new Thread(() -> {
			try
			{
				translate(event);
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}, "聊天翻译").start();
	}
	
	private void translate(ChatInputEvent event)
	{
		String incomingMsg = event.getComponent().getString();
		
		String translatorPrefix =
			"\u00a7a[\u00a7b" + langTo.getSelected().name + "\u00a7a]:\u00a7r ";
		
		if(incomingMsg.startsWith(ChatUtils.WURST_PREFIX)
			|| incomingMsg.startsWith(translatorPrefix))
			return;
		
		String translated = GoogleTranslate.translate(incomingMsg,
			langFrom.getSelected().value, langTo.getSelected().value);
		
		if(translated == null)
			return;
		
		Text translationMsg =
			Text.literal(translatorPrefix).append(Text.literal(translated));
		
		MC.inGameHud.getChatHud().addMessage(translationMsg);
	}
	
	public static enum FromLanguage
	{
		AUTO_DETECT("检测语言", "auto"),
		AFRIKAANS("南非语", "af"),
		ARABIC("阿拉伯语", "ar"),
		CZECH("捷克语", "cs"),
		CHINESE_SIMPLIFIED("中文（简体）", "zh-CN"),
		CHINESE_TRADITIONAL("中文（繁体）", "zh-TW"),
		DANISH("丹麦语", "da"),
		DUTCH("荷兰语", "nl"),
		ENGLISH("英语", "en"),
		FINNISH("芬兰语", "fi"),
		FRENCH("法语", "fr"),
		GERMAN("德语！", "de"),
		GREEK("希腊语", "el"),
		HINDI("印地语", "hi"),
		ITALIAN("意大利语", "it"),
		JAPANESE("日语", "ja"),
		KOREAN("韩语", "ko"),
		NORWEGIAN("挪威语", "no"),
		POLISH("波兰语", "pl"),
		PORTUGUESE("葡萄牙语", "pt"),
		RUSSIAN("俄语", "ru"),
		SPANISH("西班牙语", "es"),
		SWAHILI("斯瓦希里语", "sw"),
		SWEDISH("瑞典语", "sv"),
		TURKISH("土耳其语", "tr");
		
		private final String name;
		private final String value;
		
		private FromLanguage(String name, String value)
		{
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public static enum ToLanguage
	{
		AFRIKAANS("南非语", "af"),
		ARABIC("阿拉伯语", "ar"),
		CZECH("捷克语", "cs"),
		CHINESE_SIMPLIFIED("中文（简体）", "zh-CN"),
		CHINESE_TRADITIONAL("中文（繁体）", "zh-TW"),
		DANISH("丹麦语", "da"),
		DUTCH("荷兰语", "nl"),
		ENGLISH("英语", "en"),
		FINNISH("芬兰语", "fi"),
		FRENCH("法语", "fr"),
		GERMAN("德语！", "de"),
		GREEK("希腊语", "el"),
		HINDI("印地语", "hi"),
		ITALIAN("意大利语", "it"),
		JAPANESE("日语", "ja"),
		KOREAN("韩语", "ko"),
		NORWEGIAN("挪威语", "no"),
		POLISH("波兰语", "pl"),
		PORTUGUESE("葡萄牙语", "pt"),
		RUSSIAN("俄语", "ru"),
		SPANISH("西班牙语", "es"),
		SWAHILI("斯瓦希里语", "sw"),
		SWEDISH("瑞典语", "sv"),
		TURKISH("土耳其语", "tr");
		
		private final String name;
		private final String value;
		
		private ToLanguage(String name, String value)
		{
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
