/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autocomplete;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.wurstclient.util.json.JsonException;
import net.wurstclient.util.json.JsonUtils;
import net.wurstclient.util.json.WsonObject;

public final class OpenAiMessageCompleter extends MessageCompleter
{
	public OpenAiMessageCompleter(ModelSettings modelSettings)
	{
		super(modelSettings);
	}
	
	@Override
	protected JsonObject buildParams(String prompt, int maxSuggestions)
	{
		// build the request parameters
		JsonObject params = new JsonObject();
		params.addProperty("停止",
			modelSettings.stopSequence.getSelected().getSequence());
		params.addProperty("最大令牌数", modelSettings.maxTokens.getValueI());
		params.addProperty("温度", modelSettings.temperature.getValue());
		params.addProperty("top_p", modelSettings.topP.getValue());
		params.addProperty("存在惩罚", modelSettings.presencePenalty.getValue());
		params.addProperty("频率惩罚", modelSettings.frequencyPenalty.getValue());
		params.addProperty("n", maxSuggestions);
		
		// determine model name and type
		boolean customModel = !modelSettings.customModel.getValue().isBlank();
		String modelName = customModel ? modelSettings.customModel.getValue()
			: "" + modelSettings.openAiModel.getSelected();
		boolean chatModel =
			customModel ? modelSettings.customModelType.getSelected().isChat()
				: modelSettings.openAiModel.getSelected().isChatModel();
		
		// add the model name
		params.addProperty("模型", modelName);
		
		// add the prompt, depending on model type
		if(chatModel)
		{
			JsonArray messages = new JsonArray();
			JsonObject systemMessage = new JsonObject();
			systemMessage.addProperty("角色", "系统");
			systemMessage.addProperty("内容", "完成以下文本。仅回复完成部分。" + " 你不是助手。");
			messages.add(systemMessage);
			JsonObject promptMessage = new JsonObject();
			promptMessage.addProperty("角色", "用户");
			promptMessage.addProperty("内容", prompt);
			messages.add(promptMessage);
			params.add("消息", messages);
			
		}else
			params.addProperty("提示", prompt);
		
		return params;
	}
	
	@Override
	protected WsonObject requestCompletions(JsonObject parameters)
		throws IOException, JsonException
	{
		// get the API URL
		URL url =
			URI.create(modelSettings.openAiModel.getSelected().isChatModel()
				? modelSettings.openaiChatEndpoint.getValue()
				: modelSettings.openaiLegacyEndpoint.getValue()).toURL();
		
		// set up the API request
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("内容类型", "application/json");
		conn.setRequestProperty("授权",
			"Bearer " + System.getenv("WURST_OPENAI_KEY"));
		
		// set the request body
		conn.setDoOutput(true);
		try(OutputStream os = conn.getOutputStream())
		{
			os.write(JsonUtils.GSON.toJson(parameters).getBytes());
			os.flush();
		}
		
		// parse the response
		return JsonUtils.parseConnectionToObject(conn);
	}
	
	@Override
	protected String[] extractCompletions(WsonObject response)
		throws JsonException
	{
		ArrayList<String> completions = new ArrayList<>();
		
		// extract choices from response
		ArrayList<WsonObject> choices = response.getArray("选择").getAllObjects();
		
		// extract completions from choices
		if(modelSettings.openAiModel.getSelected().isChatModel())
			for(WsonObject choice : choices)
			{
				WsonObject message = choice.getObject("消息");
				String content = message.getString("内容");
				completions.add(content);
			}
		else
			for(WsonObject choice : choices)
				completions.add(choice.getString("文本"));
			
		// remove newlines
		for(String completion : completions)
			completion = completion.replace("\n", " ");
		
		return completions.toArray(new String[completions.size()]);
	}
}
