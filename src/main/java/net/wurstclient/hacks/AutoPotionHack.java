/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.ItemUtils;
import net.wurstclient.util.Rotation;

@SearchTags({"自动嗑药", "auto potion", "AutoSplashPotion", "auto splash potion"})
public final class AutoPotionHack extends Hack implements UpdateListener
{
	private final SliderSetting health =
		new SliderSetting("生命值", "当你的生命值达到或低于这个值时，扔一个药水。", 6, 0.5, 9.5, 0.5,
			ValueDisplay.DECIMAL.withSuffix(" 颗心"));
	
	private int timer;
	
	public AutoPotionHack()
	{
		super("自动嗑药");
		
		setCategory(Category.COMBAT);
		addSetting(health);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		timer = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// search potion in hotbar
		int potionInHotbar = findPotion(0, 9);
		
		// check if any potion was found
		if(potionInHotbar != -1)
		{
			// check timer
			if(timer > 0)
			{
				timer--;
				return;
			}
			
			// check health
			if(MC.player.getHealth() > health.getValueF() * 2F)
				return;
			
			// save old slot
			int oldSlot = MC.player.getInventory().selectedSlot;
			
			// throw potion in hotbar
			MC.player.getInventory().selectedSlot = potionInHotbar;
			new Rotation(MC.player.getYaw(), 90).sendPlayerLookPacket();
			IMC.getInteractionManager().rightClickItem();
			
			// reset slot and rotation
			MC.player.getInventory().selectedSlot = oldSlot;
			new Rotation(MC.player.getYaw(), MC.player.getPitch())
				.sendPlayerLookPacket();
			
			// reset timer
			timer = 10;
			
			return;
		}
		
		// search potion in inventory
		int potionInInventory = findPotion(9, 36);
		
		// move potion in inventory to hotbar
		if(potionInInventory != -1)
			IMC.getInteractionManager()
				.windowClick_QUICK_MOVE(potionInInventory);
	}
	
	private int findPotion(int startSlot, int endSlot)
	{
		for(int i = startSlot; i < endSlot; i++)
		{
			ItemStack stack = MC.player.getInventory().getStack(i);
			
			// filter out non-splash potion items
			if(stack.getItem() != Items.SPLASH_POTION)
				continue;
			
			// search for instant health effects
			if(ItemUtils.hasEffect(stack, StatusEffects.INSTANT_HEALTH))
				return i;
		}
		
		return -1;
	}
}
