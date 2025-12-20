/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.events.MouseScrollListener;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.TextFieldSetting;
import net.wurstclient.util.MathUtils;

@SearchTags({"telescope", "optifine"})
@DontBlock
public final class ZoomOtf extends OtherFeature implements MouseScrollListener
{
	private final SliderSetting level = new SliderSetting("缩放级别", 3, 1,
		50, 0.1, ValueDisplay.DECIMAL.withSuffix("x"));
	
	private final CheckboxSetting scroll = new CheckboxSetting(
		"使用鼠标滚轮", "如果启用，您可以在"
			+ "缩放时使用鼠标滚轮进一步放大。",
		true);
	
	private final CheckboxSetting zoomInScreens = new CheckboxSetting(
		"缩放屏幕", "如果启用，您也可以在打开聊天窗口、"
			+ "物品栏等屏幕时进行缩放。",
		false);
	
	private final TextFieldSetting keybind = new TextFieldSetting("快捷键",
		"确定缩放的快捷键。\n\n"
			+ "与其手动编辑此值，您应该进入Wurst"
			+ "选项 -> 缩放并在那里设置。",
		"key.keyboard.v", this::isValidKeybind);
	
	private Double currentLevel;
	private Double defaultMouseSensitivity;
	
	public ZoomOtf()
	{
		super("缩放", "允许您放大。\n"
			+ "默认情况下，通过按下\u00a7lV\u00a7r键激活缩放。\n"
			+ "前往Wurst选项 -> 缩放以更改此快捷键。");
		addSetting(level);
		addSetting(scroll);
		addSetting(zoomInScreens);
		addSetting(keybind);
		EVENTS.add(MouseScrollListener.class, this);
	}
	
	public float changeFovBasedOnZoom(float fov)
	{
		OptionInstance<Double> mouseSensitivitySetting =
			MC.options.sensitivity();
		
		if(currentLevel == null)
			currentLevel = level.getValue();
		
		if(!isZoomKeyPressed())
		{
			currentLevel = level.getValue();
			
			if(defaultMouseSensitivity != null)
			{
				mouseSensitivitySetting.set(defaultMouseSensitivity);
				defaultMouseSensitivity = null;
			}
			
			return fov;
		}
		
		if(defaultMouseSensitivity == null)
			defaultMouseSensitivity = mouseSensitivitySetting.get();
			
		// Adjust mouse sensitivity in relation to zoom level.
		// 1.0 / currentLevel is a value between 0.02 (50x zoom)
		// and 1 (no zoom).
		mouseSensitivitySetting
			.set(defaultMouseSensitivity * (1.0 / currentLevel));
		
		return (float)(fov / currentLevel);
	}
	
	@Override
	public void onMouseScroll(double amount)
	{
		if(!isZoomKeyPressed() || !scroll.isChecked())
			return;
		
		if(currentLevel == null)
			currentLevel = level.getValue();
		
		if(amount > 0)
			currentLevel *= 1.1;
		else if(amount < 0)
			currentLevel *= 0.9;
		
		currentLevel = MathUtils.clamp(currentLevel, level.getMinimum(),
			level.getMaximum());
	}
	
	public boolean shouldPreventHotbarScrolling()
	{
		return isZoomKeyPressed() && scroll.isChecked();
	}
	
	public Component getTranslatedKeybindName()
	{
		return InputConstants.getKey(keybind.getValue()).getDisplayName();
	}
	
	public void setBoundKey(String translationKey)
	{
		keybind.setValue(translationKey);
	}
	
	private boolean isZoomKeyPressed()
	{
		if(MC.screen != null && !zoomInScreens.isChecked())
			return false;
		
		return InputConstants.isKeyDown(MC.getWindow(),
			InputConstants.getKey(keybind.getValue()).getValue());
	}
	
	private boolean isValidKeybind(String keybind)
	{
		try
		{
			return InputConstants.getKey(keybind) != null;
			
		}catch(IllegalArgumentException e)
		{
			return false;
		}
	}
	
	public SliderSetting getLevelSetting()
	{
		return level;
	}
	
	public CheckboxSetting getScrollSetting()
	{
		return scroll;
	}
}
