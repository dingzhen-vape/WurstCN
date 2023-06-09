/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtString;
import net.wurstclient.command.CmdError;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;

public final class AuthorCmd extends Command
{
	public AuthorCmd()
	{
		super("author", "修改一本已写好的书的作者。\n"
			+ "需要创造模式。", ".author <author>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		
		if(!MC.player.getAbilities().creativeMode)
			throw new CmdError("创造模式才能使用。");
		
		ItemStack heldItem = MC.player.getInventory().getMainHandStack();
		int heldItemID = Item.getRawId(heldItem.getItem());
		int writtenBookID = Item.getRawId(Items.WRITTEN_BOOK);
		
		if(heldItemID != writtenBookID)
			throw new CmdError(
				"你必须在主手拿着一本已写好的书。");
		
		String author = String.join(" ", args);
		heldItem.setSubNbt("author", NbtString.of(author));
	}
}
