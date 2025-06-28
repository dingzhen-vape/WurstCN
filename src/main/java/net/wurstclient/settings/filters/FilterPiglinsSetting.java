/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PiglinEntity;

public final class FilterPiglinsSetting extends AttackDetectingEntityFilter
{
	private static final String EXCEPTIONS_TEXT = "\n\n不会过滤小猪，注意了嗷.";
	
	private FilterPiglinsSetting(String description, Mode selected,
		boolean checked)
	{
		super("筛选猪灵", description + EXCEPTIONS_TEXT, selected, checked);
	}
	
	public FilterPiglinsSetting(String description, Mode selected)
	{
		this(description, selected, false);
	}
	
	@Override
	public boolean onTest(Entity e)
	{
		return !(e instanceof PiglinEntity);
	}
	
	@Override
	public boolean ifCalmTest(Entity e)
	{
		return !(e instanceof PiglinEntity pe) || pe.isAttacking();
	}
	
	public static FilterPiglinsSetting genericCombat(Mode selected)
	{
		return new FilterPiglinsSetting(
			"当设置为 \u00a7l开启\u00a7r," + " piglins won't be attacked at all.\n\n"
				+ "When set to \u00a7lIf calm\u00a7r, piglins won't be attacked"
				+ " 它们首先攻击时才被攻击。请注意，此过滤器无法" + " 检测猪灵是攻击你还是其他人。\n\n"
				+ "当设置为 \u00a7l关闭\u00a7r, 此过滤器不执行任何操作并且" + " 猪灵可以被攻击。",
			selected);
	}
	
	public static FilterPiglinsSetting genericVision(Mode selected)
	{
		return new FilterPiglinsSetting("当设置为 \u00a7l开启\u00a7r,"
			+ " piglins won't be shown at all.\n\n"
			+ "When set to \u00a7lIf calm\u00a7r, piglins won't be shown until"
			+ " 它们攻击某物。\n\n" + "当设置为 \u00a7l关闭\u00a7r, 此过滤器不执行任何操作并且"
			+ " 猪灵可以显示。", selected);
	}
	
	public static FilterPiglinsSetting onOffOnly(String description,
		boolean onByDefault)
	{
		return new FilterPiglinsSetting(description, null, onByDefault);
	}
}
