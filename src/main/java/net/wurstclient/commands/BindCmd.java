/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import net.wurstclient.command.CmdException;
import net.wurstclient.command.Command;

public final class BindCmd extends Command
{
	public BindCmd()
	{
		super("bind", "'.binds add'的快捷方式。", ".bind <key> <hacks>",
			".bind <key> <commands>",
			"多个黑客功能/命令必须用';'分隔。",
			"使用.binds来获得更多选项。");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		WURST.getCmdProcessor().process("binds add " + String.join(" ", args));
	}
}
