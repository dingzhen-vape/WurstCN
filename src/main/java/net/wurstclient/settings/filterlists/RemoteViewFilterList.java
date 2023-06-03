/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filterlists;

import java.util.ArrayList;
import java.util.List;

import net.wurstclient.settings.filters.*;

public final class RemoteViewFilterList extends EntityFilterList
{
	private RemoteViewFilterList(List<EntityFilter> filters)
	{
		super(filters);
	}
	
public static RemoteViewFilterList create()
	{
		ArrayList<EntityFilter> builder = new ArrayList<>();
		
		builder
			.add(new FilterPlayersSetting("不会观察其他玩家。", false));
		
		builder.add(
			new FilterSleepingSetting("不会观察睡觉的玩家。", false));
		
		builder.add(new FilterFlyingSetting(
			"不会观察离地面至少给定距离的玩家。",
			0));
		
		builder.add(new FilterMonstersSetting(
			"不会观察僵尸，苦力怕等。", true));
		
		builder.add(new FilterPigmenSetting("不会观察僵尸猪人。", true));
		
		builder.add(new FilterEndermenSetting("不会观察末影人。", true));
		
		builder
			.add(new FilterAnimalsSetting("不会观察猪，牛等。", true));
		
		builder.add(new FilterBabiesSetting(
			"不会观察小猪，小村民等。", true));
		
		builder.add(new FilterPetsSetting(
			"不会观察驯服的狼，驯服的马等。", true));
		
		builder.add(new FilterTradersSetting(
			"不会观察村民，流浪商人等。", true));
		
		builder.add(new FilterGolemsSetting(
			"不会观察铁傀儡，雪傀儡和潜影贝。", true));
		
		builder.add(new FilterAllaysSetting("不会观察盟友。", true));
		
		builder.add(new FilterInvisibleSetting("不会观察隐形的实体。",
			false));
		
		builder.add(
			new FilterArmorStandsSetting("不会观察盔甲架。", true));
		
		return new RemoteViewFilterList(builder);
	}
}
