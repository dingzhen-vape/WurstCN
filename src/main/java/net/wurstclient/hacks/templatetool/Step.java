/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.templatetool;

import net.minecraft.util.math.BlockPos;

public enum Step
{
	START_POS("选择起始位置。", true),
	
	END_POS("选择结束位置。", true),
	
	SCAN_AREA("扫描区域...", false),
	
	FIRST_BLOCK("选择AutoBuild将放置的第一个方块。", true),
	
	CREATE_TEMPLATE("创建模板...", false),
	
	FILE_NAME("为此模板选择一个名称。", false),
	
	SAVE_FILE("保存文件...", false);
	
	public static final Step[] SELECT_POSITION_STEPS =
		{START_POS, END_POS, FIRST_BLOCK};
	
	private final String message;
	private final boolean selectPos;
	
	private BlockPos pos;
	
	private Step(String message, boolean selectPos)
	{
		this.message = message;
		this.selectPos = selectPos;
	}
	
	public BlockPos getPos()
	{
		return pos;
	}
	
	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public boolean doesSelectPos()
	{
		return selectPos;
	}
}
