/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autofish;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.wurstclient.WurstClient;
import net.wurstclient.hacks.AutoFishHack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.Setting;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.InventoryUtils;

public final class AutoFishRodSelector
{
	private static final MinecraftClient MC = WurstClient.MC;
	
	private final CheckboxSetting stopWhenOutOfRods =
		new CheckboxSetting("停止当没有钓鱼竿", "如果启用，AutoFish将自动关闭当它耗尽钓鱼竿。", false);
	
	private final CheckboxSetting stopWhenInvFull =
		new CheckboxSetting("停止当库存满", "如果启用，AutoFish将自动关闭当您的库存满了。", false);
	
	private final AutoFishHack autoFish;
	private int bestRodSlot;
	
	public AutoFishRodSelector(AutoFishHack autoFish)
	{
		this.autoFish = autoFish;
	}
	
	public Stream<Setting> getSettings()
	{
		return Stream.of(stopWhenOutOfRods, stopWhenInvFull);
	}
	
	public void reset()
	{
		bestRodSlot = -1;
	}
	
	public boolean isOutOfRods()
	{
		return bestRodSlot == -1;
	}
	
	/**
	 * Reevaluates the player's fishing rods, checks for any inventory-related
	 * issues and updates the selected rod if necessary.
	 *
	 * @return true if it's OK to proceed with fishing in the same tick
	 */
	public boolean update()
	{
		PlayerInventory inventory = MC.player.getInventory();
		int selectedSlot = inventory.selectedSlot;
		ItemStack selectedStack = inventory.getStack(selectedSlot);
		
		// evaluate selected rod (or lack thereof)
		int bestRodValue = getRodValue(selectedStack);
		bestRodSlot = bestRodValue > -1 ? selectedSlot : -1;
		
		// create a stream of all slots that we want to search
		IntStream stream = IntStream.range(0, 36);
		stream = IntStream.concat(stream, IntStream.of(40));
		
		// search inventory for better rod
		for(int slot : stream.toArray())
		{
			ItemStack stack = inventory.getStack(slot);
			int rodValue = getRodValue(stack);
			
			if(rodValue > bestRodValue)
			{
				bestRodValue = rodValue;
				bestRodSlot = slot;
			}
		}
		
		// wait for AutoEat to finish eating
		if(WurstClient.INSTANCE.getHax().autoEatHack.isEating())
			return false;
		
		// stop if out of rods
		if(stopWhenOutOfRods.isChecked() && bestRodSlot == -1)
		{
			ChatUtils.message("AutoFish已耗尽钓鱼竿");
			autoFish.setEnabled(false);
			return false;
		}
		
		// stop if inventory is full
		if(stopWhenInvFull.isChecked() && inventory.getEmptySlot() == -1)
		{
			ChatUtils.message("AutoFish因您的库存满了而停止。");
			autoFish.setEnabled(false);
			return false;
		}
		
		// check if selected rod is still the best one
		if(selectedSlot == bestRodSlot)
			return true;
		
		// change selected rod and wait until the next tick
		InventoryUtils.selectItem(bestRodSlot);
		return false;
	}
	
	private int getRodValue(ItemStack stack)
	{
		if(stack.isEmpty() || !(stack.getItem() instanceof FishingRodItem))
			return -1;
		
		DynamicRegistryManager drm = MC.world.getRegistryManager();
		Registry<Enchantment> registry = drm.get(RegistryKeys.ENCHANTMENT);
		
		Optional<Reference<Enchantment>> luckOTS =
			registry.getEntry(Enchantments.LUCK_OF_THE_SEA);
		int luckOTSLvl = luckOTS
			.map(entry -> EnchantmentHelper.getLevel(entry, stack)).orElse(0);
		
		Optional<Reference<Enchantment>> lure =
			registry.getEntry(Enchantments.LURE);
		int lureLvl = lure
			.map(entry -> EnchantmentHelper.getLevel(entry, stack)).orElse(0);
		
		Optional<Reference<Enchantment>> unbreaking =
			registry.getEntry(Enchantments.UNBREAKING);
		int unbreakingLvl = unbreaking
			.map(entry -> EnchantmentHelper.getLevel(entry, stack)).orElse(0);
		
		Optional<Reference<Enchantment>> mending =
			registry.getEntry(Enchantments.MENDING);
		int mendingBonus = mending
			.map(entry -> EnchantmentHelper.getLevel(entry, stack)).orElse(0);
		
		int noVanishBonus = EnchantmentHelper.hasAnyEnchantmentsWith(stack,
			EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP) ? 0 : 1;
		
		return luckOTSLvl * 9 + lureLvl * 9 + unbreakingLvl * 2 + mendingBonus
			+ noVanishBonus;
	}
}
