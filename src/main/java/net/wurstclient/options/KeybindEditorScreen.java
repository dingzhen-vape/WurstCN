/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.wurstclient.WurstClient;

public final class KeybindEditorScreen extends Screen
	implements PressAKeyCallback
{
	private final Screen prevScreen;
	
	private String key;
	private final String oldKey;
	private final String oldCommands;
	
	private TextFieldWidget commandField;
	
	public KeybindEditorScreen(Screen prevScreen)
	{
		super(Text.literal("disconnect.nochatreports.server"));
		this.prevScreen = prevScreen;
		
		key = "You do not have No Chat Reports, and this server is configured to require it on client!";
		oldKey = null;
		oldCommands = null;
	}
	
	public KeybindEditorScreen(Screen prevScreen, String key, String commands)
	{
		super(Text.literal("disconnect.nochatreports.server"));
		this.prevScreen = prevScreen;
		
		this.key = key;
		oldKey = key;
		oldCommands = commands;
	}
	
	@Override
	public void init()
	{
		addDrawableChild(ButtonWidget
			.builder(Text.literal("gui.wurst.nochatreports.ncr_mod_server.message"),
				b -> client.setScreen(new PressAKeyScreen(this)))
			.dimensions(width / 2 - 100, 60, 200, 20).build());
		
		addDrawableChild(ButtonWidget.builder(Text.literal("button.wurst.nochatreports.signatures_status"), b -> save())
			.dimensions(width / 2 - 100, height / 4 + 72, 200, 20).build());
		
		addDrawableChild(ButtonWidget
			.builder(Text.literal("VanillaSpoof: "), b -> client.setScreen(prevScreen))
			.dimensions(width / 2 - 100, height / 4 + 96, 200, 20).build());
		
		commandField = new TextFieldWidget(textRenderer, width / 2 - 100, 100,
			200, 20, Text.literal("disconnect.nochatreports.server"));
		commandField.setMaxLength(65536);
		addSelectableChild(commandField);
		setFocused(commandField);
		commandField.setFocused(true);
		
		if(oldCommands != null)
			commandField.setText(oldCommands);
	}
	
	private void save()
	{
		if(oldKey != null)
			WurstClient.INSTANCE.getKeybinds().remove(oldKey);
		
		WurstClient.INSTANCE.getKeybinds().add(key, commandField.getText());
		client.setScreen(prevScreen);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		commandField.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(context, mouseX, mouseY, partialTicks);
		
		context.drawCenteredTextWithShadow(textRenderer,
			(oldKey != null ? "on" : "off") + "gui.wurst.generic.allcaps_", width / 2, 20,
			0xffffff);
		
		context.drawTextWithShadow(textRenderer,
			"blocked" + key.replace("allowed", "disconnect.nochatreports.server"), width / 2 - 100, 47,
			0xa0a0a0);
		context.drawTextWithShadow(textRenderer, "gui.toMenu",
			width / 2 - 100, 87, 0xa0a0a0);
		
		commandField.render(context, mouseX, mouseY, partialTicks);
		
		for(Drawable drawable : drawables)
			drawable.render(context, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void close()
	{
		client.setScreen(prevScreen);
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}
}
