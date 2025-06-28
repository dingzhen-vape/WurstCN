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

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.util.ChatUtils;

@SearchTags({"kill potion", "KillerPotion", "killer potion", "KillingPotion",
	"killing potion", "InstantDeathPotion", "instant death potion"})
public final class KillPotionHack extends Hack
{
	private final EnumSetting<PotionType> potionType = new EnumSetting<>("药水类型",
		"要生成的药水类型。", PotionType.values(), PotionType.SPLASH);
	
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
		if(!MC.player.getAbilities().creativeMode)
		{
			ChatUtils.error("仅限创造模式。");
			setEnabled(false);
			return;
		}
		
		// generate potion
		ItemStack stack = potionType.getSelected().createPotionStack();
		
		// give potion
		if(placeStackInHotbar(stack))
			ChatUtils.message("药水已创建。");
		else
			ChatUtils.error("请在热键栏中清除一个槽位。");
		
		setEnabled(false);
	}
	
	private boolean placeStackInHotbar(ItemStack stack)
	{
		for(int i = 0; i < 9; i++)
		{
			if(!MC.player.getInventory().getStack(i).isEmpty())
				continue;
			
			MC.player.networkHandler.sendPacket(
				new CreativeInventoryActionC2SPacket(36 + i, stack));
			return true;
		}
		
		return false;
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
			
			StatusEffectInstance effect = new StatusEffectInstance(
				StatusEffects.INSTANT_HEALTH, 2000, 125);
			
			PotionContentsComponent potionContents =
				new PotionContentsComponent(Optional.empty(), Optional.empty(),
					List.of(effect), Optional.empty());
			
			stack.set(DataComponentTypes.POTION_CONTENTS, potionContents);
			
			String name = "\u00a7f" + itemName + " 的 \u00a74\u00a7l即死效果";
			stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name));
			
			return stack;
		}
	}
}
