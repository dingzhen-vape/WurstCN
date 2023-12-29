/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IClientPlayerInteractionManager;
import net.wurstclient.settings.ItemListSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"AutoRestock", "auto-restock", "auto restock"})
public final class RestockHack extends Hack implements UpdateListener
{
	public static final int OFFHAND_ID = PlayerInventory.OFF_HAND_SLOT;
	public static final int OFFHAND_PKT_ID = 45;
	
	private static final List<Integer> SEARCH_SLOTS =
		Stream.concat(IntStream.range(0, 36).boxed(), Stream.of(OFFHAND_ID))
			.collect(Collectors.toCollection(ArrayList::new));
	
	private ItemListSetting items = new ItemListSetting("物品",
		"Item(s) to be restocked.", "minecraft:minecart");
	
	private final SliderSetting restockSlot =
		new SliderSetting("槽位", "补充到哪个槽位。", 0, -1, 9, 1,
			ValueDisplay.INTEGER.withLabel(9, "副手").withLabel(-1, "当前"));
	
	private final SliderSetting restockAmount = new SliderSetting("最小数量",
		"手中物品的最小数量，低于这个数量时会触发补充。", 1, 1, 64, 1, ValueDisplay.INTEGER);
	
	private final SliderSetting repairMode = new SliderSetting("工具修复模式",
		"当工具的耐久度达到给定的阈值时，会自动换掉，以便在它们损坏之前修复它们。\n" + "可以从0（关闭）到100调整。", 0, 0, 100,
		1, ValueDisplay.INTEGER.withLabel(0, "关闭"));
	
	public RestockHack()
	{
		super("仓库补充");
		setCategory(Category.ITEMS);
		addSetting(items);
		addSetting(restockSlot);
		addSetting(restockAmount);
		addSetting(repairMode);
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// Don't mess with the inventory while it's open.
		if(MC.currentScreen instanceof HandledScreen)
			return;
		
		PlayerInventory inv = MC.player.getInventory();
		IClientPlayerInteractionManager im = IMC.getInteractionManager();
		
		int hotbarSlot = restockSlot.getValueI();
		if(hotbarSlot == -1)
			hotbarSlot = inv.selectedSlot;
		else if(hotbarSlot == 9)
			hotbarSlot = OFFHAND_ID;
		
		for(String itemName : items.getItemNames())
		{
			ItemStack hotbarStack = inv.getStack(hotbarSlot);
			
			boolean wrongItem =
				hotbarStack.isEmpty() || !itemEqual(itemName, hotbarStack);
			if(!wrongItem && hotbarStack.getCount() >= Math
				.min(restockAmount.getValueI(), hotbarStack.getMaxCount()))
				return;
			
			List<Integer> searchResult =
				searchSlotsWithItem(itemName, hotbarSlot);
			for(int itemIndex : searchResult)
			{
				int pickupIndex = dataSlotToNetworkSlot(itemIndex);
				
				im.windowClick_PICKUP(pickupIndex);
				im.windowClick_PICKUP(dataSlotToNetworkSlot(hotbarSlot));
				if(!MC.player.playerScreenHandler.getCursorStack().isEmpty())
					im.windowClick_PICKUP(pickupIndex);
				
				if(hotbarStack.getCount() >= hotbarStack.getMaxCount())
					break;
			}
			
			if(wrongItem && searchResult.isEmpty())
				continue;
			
			break;
		}
		
		ItemStack restockStack = inv.getStack(hotbarSlot);
		if(repairMode.getValueI() > 0 && restockStack.isDamageable()
			&& isTooDamaged(restockStack))
			for(int i : SEARCH_SLOTS)
			{
				if(i == hotbarSlot || i == OFFHAND_ID)
					continue;
				
				ItemStack stack = inv.getStack(i);
				if(stack.isEmpty() || !stack.isDamageable())
				{
					IMC.getInteractionManager().windowClick_SWAP(i,
						dataSlotToNetworkSlot(hotbarSlot));
					break;
				}
			}
	}
	
	private boolean isTooDamaged(ItemStack stack)
	{
		return stack.getMaxDamage() - stack.getDamage() <= repairMode
			.getValueI();
	}
	
	private int dataSlotToNetworkSlot(int index)
	{
		// hotbar
		if(index >= 0 && index <= 8)
			return index + 36;
		
		// main inventory
		if(index >= 9 && index <= 35)
			return index;
		
		if(index == OFFHAND_ID)
			return OFFHAND_PKT_ID;
		
		throw new IllegalArgumentException("未实现的数据槽");
	}
	
	private List<Integer> searchSlotsWithItem(String itemName, int slotToSkip)
	{
		List<Integer> slots = new ArrayList<>();
		
		for(int i : SEARCH_SLOTS)
		{
			if(i == slotToSkip)
				continue;
			
			ItemStack stack = MC.player.getInventory().getStack(i);
			if(stack.isEmpty())
				continue;
			
			if(itemEqual(itemName, stack))
				slots.add(i);
		}
		
		return slots;
	}
	
	private boolean itemEqual(String itemName, ItemStack stack)
	{
		if(repairMode.getValueI() > 0 && stack.isDamageable()
			&& isTooDamaged(stack))
			return false;
		
		return Registries.ITEM.getId(stack.getItem()).toString()
			.equals(itemName);
	}
}
