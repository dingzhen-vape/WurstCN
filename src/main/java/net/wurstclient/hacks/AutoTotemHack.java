/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IClientPlayerInteractionManager;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.InventoryUtils;

@SearchTags({"auto totem", "副手", "off-hand"})
public final class AutoTotemHack extends Hack implements UpdateListener
{
	private final CheckboxSetting showCounter =
		new CheckboxSetting("显示图腾计数器", "显示您拥有的图腾数量。", true);
	
	private final SliderSetting delay = new SliderSetting("延迟",
		"在装备下一个图腾之前等待的滴答数。", 0, 0, 20, 1, ValueDisplay.INTEGER);
	
	private final SliderSetting health = new SliderSetting("生命值",
		"Won't equip a totem until your health reaches this value or falls"
			+ " 以下。\n" + "0 = 始终激活",
		0, 0, 10, 0.5, ValueDisplay.DECIMAL.withSuffix(" 心").withLabel(1, "1 心")
			.withLabel(0, " 忽略"));
	
	private int nextTickSlot;
	private int totems;
	private int timer;
	private boolean wasTotemInOffhand;
	
	public AutoTotemHack()
	{
		super("自动图腾");
		setCategory(Category.COMBAT);
		addSetting(showCounter);
		addSetting(delay);
		addSetting(health);
	}
	
	@Override
	public String getRenderName()
	{
		if(!showCounter.isChecked())
			return getName();
		
		if(totems == 1)
			return getName() + " [1 图腾]";
		
		return getName() + " [" + totems + " [图腾]";
	}
	
	@Override
	protected void onEnable()
	{
		nextTickSlot = -1;
		totems = 0;
		timer = 0;
		wasTotemInOffhand = false;
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		finishMovingTotem();
		
		int nextTotemSlot = searchForTotems();
		
		if(isTotem(MC.player.getOffHandStack()))
		{
			wasTotemInOffhand = true;
			return;
		}
		
		if(wasTotemInOffhand)
		{
			timer = delay.getValueI();
			wasTotemInOffhand = false;
		}
		
		if(nextTotemSlot == -1)
			return;
		
		float healthF = health.getValueF();
		if(healthF > 0 && MC.player.getHealth() > healthF * 2F)
			return;
		
		// don't move items while a container is open
		if(MC.currentScreen instanceof HandledScreen
			&& !(MC.currentScreen instanceof InventoryScreen
				|| MC.currentScreen instanceof CreativeInventoryScreen))
			return;
		
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		moveToOffhand(nextTotemSlot);
	}
	
	private void moveToOffhand(int itemSlot)
	{
		boolean offhandEmpty = MC.player.getOffHandStack().isEmpty();
		
		IClientPlayerInteractionManager im = IMC.getInteractionManager();
		im.windowClick_PICKUP(itemSlot);
		im.windowClick_PICKUP(45);
		
		if(!offhandEmpty)
			nextTickSlot = itemSlot;
	}
	
	private void finishMovingTotem()
	{
		if(nextTickSlot == -1)
			return;
		
		IClientPlayerInteractionManager im = IMC.getInteractionManager();
		im.windowClick_PICKUP(nextTickSlot);
		nextTickSlot = -1;
	}
	
	private int searchForTotems()
	{
		totems = InventoryUtils.count(this::isTotem, 40, true);
		if(totems <= 0)
			return -1;
		
		int totemSlot = InventoryUtils.indexOf(this::isTotem, 40);
		return InventoryUtils.toNetworkSlot(totemSlot);
	}
	
	private boolean isTotem(ItemStack stack)
	{
		return stack.isOf(Items.TOTEM_OF_UNDYING);
	}
}
