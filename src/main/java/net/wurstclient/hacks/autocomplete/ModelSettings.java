/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autocomplete;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.TextFieldSetting;

public final class ModelSettings
{
	public final EnumSetting<OpenAiModel> openAiModel = new EnumSetting<>(
		"OpenAI 模型", "The model to use for OpenAI API calls.",
		OpenAiModel.values(), OpenAiModel.GPT_4O_2024_08_06);
	
	public enum OpenAiModel
	{
		GPT_4O_2024_08_06("gpt-4o-2024-08-06", true),
		GPT_4O_2024_05_13("gpt-4o-2024-05-13", true),
		GPT_4O_MINI_2024_07_18("gpt-4o-mini-2024-07-18", true),
		GPT_4_TURBO_2024_04_09("gpt-4-turbo-2024-04-09", true),
		GPT_4_0125_PREVIEW("gpt-4-0125-preview", true),
		GPT_4_1106_PREVIEW("gpt-4-1106-preview", true),
		GPT_4_0613("gpt-4-0613", true),
		GPT_3_5_TURBO_0125("gpt-3.5-turbo-0125", true),
		GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106", true),
		GPT_3_5_TURBO_INSTRUCT("gpt-3.5-turbo-instruct", false),
		DAVINCI_002("davinci-002", false),
		BABBAGE_002("babbage-002", false);
		
		private final String name;
		private final boolean chat;
		
		private OpenAiModel(String name, boolean chat)
		{
			this.name = name;
			this.chat = chat;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
		public boolean isChatModel()
		{
			return chat;
		}
	}
	
	public final SliderSetting maxTokens = new SliderSetting("最大代币数",
		"模型可以生成的最大代币数。\n\n"
			+ "更高的值允许模型生成更长的聊天消息，"
			+ " 但也会增加生成预测所需的时间。\n\n"
			+ "默认值 16 对大多数用例来说已经足够。",
		16, 1, 100, 1, ValueDisplay.INTEGER);
	
	public final SliderSetting temperature = new SliderSetting("温度",
		"Controls the model's creativity and randomness. A higher value will"
			+ " 生成更创意但有时不合理的补全，"
			+ " 而较低的值将生成更无聊的补全。",
		1, 0, 2, 0.01, ValueDisplay.DECIMAL);
	
	public final SliderSetting topP = new SliderSetting("Top P",
		"温度的替代方案。通过只让模型"
			+ " 从最可能的代币中选择来减少其随机性。\n\n"
			+ "值为 100% 将禁用此功能，让模型"
			+ " 从所有代币中选择。",
		1, 0, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	public final SliderSetting presencePenalty =
		new SliderSetting("存在惩罚",
			"选择已经在聊天中出现的代币的惩罚"
				+ " 历史。\n\n"
				+ "正值鼓励模型使用同义词并"
				+ " 讨论不同的主题。负值鼓励模型"
				+ " 重复使用同一个单词。",
			0, -2, 2, 0.01, ValueDisplay.DECIMAL);
	
	public final SliderSetting frequencyPenalty =
		new SliderSetting("频率惩罚",
			"类似于存在惩罚，但基于代币"
				+ " 在聊天历史中出现的频率。\n\n"
				+ "正值鼓励模型使用同义词并"
				+ " 讨论不同的主题。负值鼓励模型"
				+ " 重复现有的聊天消息。",
			0, -2, 2, 0.01, ValueDisplay.DECIMAL);
	
	public final EnumSetting<StopSequence> stopSequence = new EnumSetting<>(
		"停止序列",
		"控制 AutoComplete 如何检测聊天消息的结束。\n\n"
			+ "\u00a7l行中断\u00a7r 是默认值，并且推荐"
			+ " 用于大多数语言模型。\n\n"
			+ "\u00a7l下一条消息\u00a7r 与某些"
			+ "代码优化的语言模型一起工作更好，这些模型有"
			+ " 在聊天消息中间插入行中断的倾向。",
		StopSequence.values(), StopSequence.LINE_BREAK);
	
	public enum StopSequence
	{
		LINE_BREAK("行中断", "\n"),
		NEXT_MESSAGE("下一条消息", "\n<");
		
		private final String name;
		private final String sequence;
		
		private StopSequence(String name, String sequence)
		{
			this.name = name;
			this.sequence = sequence;
		}
		
		public String getSequence()
		{
			return sequence;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public final SliderSetting contextLength = new SliderSetting(
		"上下文长度",
		"控制用于生成的聊天历史消息数量"
			+ " 预测。\n\n"
			+ "更高的值可以提高预测质量，但也会"
			+ " 增加生成预测所需的时间，以及成本"
			+ " (对于API如OpenAI)或RAM使用量(对于自托管模型)",
		10, 0, 100, 1, ValueDisplay.INTEGER);
	
	public final CheckboxSetting filterServerMessages =
		new CheckboxSetting("过滤服务器消息",
			"仅向模型显示玩家制作的聊天消息。\n\n"
				+ "这可以帮助您节省令牌并充分利用较低的"
				+ "上下文长度，但也意味着模型将无法了解"
				+ "诸如玩家加入、离开、死亡等事件"
				+ "等",
			false);
	
	public final TextFieldSetting customModel = new TextFieldSetting(
		"自定义模型",
		"如果设置，此模型将代替指定的"
			+ " \"OpenAI model\" 设置。\n\n"
			+ "如果您有一个微调过的OpenAI模型或"
			+ "正在使用一个与OpenAI兼容但提供"
			+ "不同模型的自定义端点",
		"");
	
	public final EnumSetting<CustomModelType> customModelType =
		new EnumSetting<>("自定义模型类型", "是否让自定义"
			+ " 模型使用聊天端点或旧版端点。\n\n"
			+ "如果 \"Custom model\" 留为空，此设置将被忽略",
			CustomModelType.values(), CustomModelType.CHAT);
	
	public enum CustomModelType
	{
		CHAT("聊天", true),
		LEGACY("旧版", false);
		
		private final String name;
		private final boolean chat;
		
		private CustomModelType(String name, boolean chat)
		{
			this.name = name;
			this.chat = chat;
		}
		
		public boolean isChat()
		{
			return chat;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public final TextFieldSetting openaiChatEndpoint = new TextFieldSetting(
		"OpenAI聊天端点", "Endpoint for OpenAI's chat completion API.",
		"https://api.openai.com/v1/chat/completions");
	
	public final TextFieldSetting openaiLegacyEndpoint =
		new TextFieldSetting("OpenAI旧版端点",
			"Endpoint for OpenAI's legacy completion API.",
			"https://api.openai.com/v1/completions");
	
	private final List<Setting> settings =
		Collections.unmodifiableList(Arrays.asList(openAiModel, maxTokens,
			temperature, topP, presencePenalty, frequencyPenalty, stopSequence,
			contextLength, filterServerMessages, customModel, customModelType,
			openaiChatEndpoint, openaiLegacyEndpoint));
	
	public void forEach(Consumer<Setting> action)
	{
		settings.forEach(action);
	}
}
