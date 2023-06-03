/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.math.MathHelper;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.ISimpleOption;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"Fullbrightness", "full brightness", "Fulbrightness",
	"ful brightness", "NightVision", "night vision", "FullLightness",
	"FulLightness", "full lightness", "FullGamma", "full gamma"})
public final class FullbrightHack extends Hack implements UpdateListener
{
	private final EnumSetting<Method> method = new EnumSetting<>("方法",
		"\u00a7l伽马\u00a7r 通过将你的亮度滑块设置超过100%来工作。与着色器包不兼容。\n\n"
			+ "\u00a7l夜视\u00a7r 通过应用夜视效果来工作。这\u00a7o通常\u00a7r与着色器包兼容。",
		Method.values(), Method.GAMMA);
	
	private final CheckboxSetting fade = new CheckboxSetting("渐变",
		"在亮度和黑暗之间缓慢渐变。", true);
	
	private final SliderSetting defaultGamma = new SliderSetting(
		"默认亮度",
		"当你关闭全亮时，全亮会将你的亮度滑块设置回这个值。",

		0.5, 0, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	private boolean wasGammaChanged;
	private float nightVisionStrength;
	
	public FullbrightHack()
	{
		super("黑暗视觉");
		setCategory(Category.RENDER);
		addSetting(method);
		addSetting(fade);
		addSetting(defaultGamma);
		
		checkGammaOnStartup();
		EVENTS.add(UpdateListener.class, this);
	}
	
	private void checkGammaOnStartup()
	{
		EVENTS.add(UpdateListener.class, new UpdateListener()
		{
			@Override
			public void onUpdate()
			{
				double gamma = MC.options.getGamma().getValue();
				System.out.println("亮度开始于 " + gamma);
				
				if(gamma > 1)
					wasGammaChanged = true;
				else
					defaultGamma.setValue(gamma);
				
				EVENTS.remove(UpdateListener.class, this);
			}
		});
	}
	
	@Override
	public void onUpdate()
	{
		updateGamma();
		updateNightVision();
	}
	
	private void updateGamma()
	{
		boolean shouldChangeGamma =
			isEnabled() && method.getSelected() == Method.GAMMA;
		
		if(shouldChangeGamma)
		{
			setGamma(16);
			return;
		}
		
		if(wasGammaChanged)
			resetGamma(defaultGamma.getValue());
	}
	
	private void setGamma(double target)
	{
		wasGammaChanged = true;
		
		SimpleOption<Double> gammaOption = MC.options.getGamma();
		@SuppressWarnings("unchecked")
		ISimpleOption<Double> gammaOption2 =
			(ISimpleOption<Double>)(Object)gammaOption;
		double oldGammaValue = gammaOption.getValue();
		
		if(!fade.isChecked() || Math.abs(oldGammaValue - target) <= 0.5)
		{
			gammaOption2.forceSetValue(target);
			return;
		}
		
		if(oldGammaValue < target)
			gammaOption2.forceSetValue(oldGammaValue + 0.5);
		else
			gammaOption2.forceSetValue(oldGammaValue - 0.5);
	}
	
	private void resetGamma(double target)
	{
		SimpleOption<Double> gammaOption = MC.options.getGamma();
		@SuppressWarnings("unchecked")
		ISimpleOption<Double> gammaOption2 =
			(ISimpleOption<Double>)(Object)gammaOption;
		double oldGammaValue = gammaOption.getValue();
		
		if(!fade.isChecked() || Math.abs(oldGammaValue - target) <= 0.5)
		{
			gammaOption2.forceSetValue(target);
			wasGammaChanged = false;
			return;
		}
		
		if(oldGammaValue < target)
			gammaOption2.forceSetValue(oldGammaValue + 0.5);
		else
			gammaOption2.forceSetValue(oldGammaValue - 0.5);
	}
	
	private void updateNightVision()
	{
		boolean shouldGiveNightVision =
			isEnabled() && method.getSelected() == Method.NIGHT_VISION;
		
		if(fade.isChecked())
		{
			if(shouldGiveNightVision)
				nightVisionStrength += 0.03125;
			else
				nightVisionStrength -= 0.03125;
			
			nightVisionStrength = MathHelper.clamp(nightVisionStrength, 0, 1);
			
		}else if(shouldGiveNightVision)
			nightVisionStrength = 1;
		else
			nightVisionStrength = 0;
	}
	
	public boolean isNightVisionActive()
	{
		return nightVisionStrength > 0;
	}
	
	public float getNightVisionStrength()
	{
		return nightVisionStrength;
	}
	
	private static enum Method
	{
		GAMMA("伽马值"),
		NIGHT_VISION("夜视效果");
		
		private final String name;
		
		private Method(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	// See ClientPlayerEntityMixin.hasStatusEffect()
}
