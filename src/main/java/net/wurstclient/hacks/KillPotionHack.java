/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.InventoryUtils;

@SearchTags({"kill potion", "KillerPotion", "killer potion", "KillingPotion",
	"killing potion", "InstantDeathPotion", "instant death potion"})
public final class KillPotionHack extends Hack
{
	private final EnumSetting<PotionType> potionType =
		new EnumSetting<>("药水类型", "要生成的药水类型。",
			PotionType.values(), PotionType.SPLASH);
	
	public KillPotionHack()
	{
		super("杀戮神药");
		
		setCategory(Category.ITEMS);
		addSetting(potionType);
	}
	
	@Override
	protected void onEnable()
	{
		// check gamemode
		if(!MC.player.getAbilities().instabuild)
		{
			ChatUtils.error("仅限创造模式。");
			setEnabled(false);
			return;
		}
		
		// generate potion
		ItemStack stack = potionType.getSelected().createPotionStack();
		
		// give potion
		Inventory inventory = MC.player.getInventory();
		int slot = inventory.getFreeSlot();
		if(slot < 0)
			ChatUtils.error("Cannot give potion. Your inventory is full.");
		else
		{
			InventoryUtils.setCreativeStack(slot, stack);
			ChatUtils.message("药水已创建。");
		}
		
		setEnabled(false);
	}
	
	private enum PotionType
	{
		NORMAL("普通", "药水", Items.POTION),
		
		SPLASH("溅射", "溅射药水", Items.SPLASH_POTION),
		
		LINGERING("滞留", "滞留药水", Items.LINGERING_POTION);
		
		// does not work
		// ARROW("箭", "箭", Items.TIPPED_ARROW);
		
		private final String name;
		private final String itemName;
		private final Item item;
		
		private PotionType(String name, String itemName, Item item)
		{
			this.name = name;
			this.itemName = itemName;
			this.item = item;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
		public ItemStack createPotionStack()
		{
			ItemStack stack = new ItemStack(item);
			
			MobEffectInstance effect =
				new MobEffectInstance(MobEffects.INSTANT_HEALTH, 2000, 125);
			
			PotionContents potionContents = new PotionContents(Optional.empty(),
				Optional.empty(), List.of(effect), Optional.empty());
			
			stack.set(DataComponents.POTION_CONTENTS, potionContents);
			
			String name =
				"\u00a7f" + itemName + " 的 \u00a74\u00a7l即死效果";
			stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
			
			return stack;
		}
	}
}
