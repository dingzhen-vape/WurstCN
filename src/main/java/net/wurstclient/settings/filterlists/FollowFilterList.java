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

public final class FollowFilterList extends EntityFilterList
{
	private FollowFilterList(List<EntityFilter> filters)
	{
		super(filters);
	}
	
	public static FollowFilterList create()
	{
		ArrayList<EntityFilter> builder = new ArrayList<>();
		
		builder.add(new FilterPlayersSetting("不会跟随其他玩家。", false));
		
		builder.add(new FilterSleepingSetting("不会跟随睡觉的玩家。", false));
		
		builder.add(new FilterFlyingSetting("不会跟随高于地面一定距离的玩家。", 0));
		
		builder.add(new FilterHostileSetting("不会跟随像僵尸和苦力怕这样的敌对生物。", true));
		
		builder.add(FilterNeutralSetting.onOffOnly("不会跟随像末影人和狼这样的中立生物。", true));
		
		builder.add(new FilterPassiveSetting(
			"不会跟随像猪和" + "牛这样的动物，像蝙蝠这样的环境生物，和像" + "鱼，鱿鱼和海豚这样的水生生物。", true));
		
		builder.add(new FilterPassiveWaterSetting(
			"不会跟随像鱼，鱿鱼，海豚和水怪这样的" + "被动水生生物。", true));
		
		builder.add(new FilterBabiesSetting("不会跟随小猪，小村民等。", true));
		
		builder.add(new FilterBatsSetting(
			"不会跟随蝙蝠和任何其他" + " \"ambient\" 可能由模组添加的生物。", true));
		
		builder.add(new FilterSlimesSetting("不会跟随史莱姆。", true));
		
		builder.add(new FilterPetsSetting("不会跟随驯服的狼，驯服的马等。", true));
		
		builder.add(new FilterVillagersSetting("不会跟随村民和流浪商人。", true));
		
		builder.add(new FilterZombieVillagersSetting("不会跟随僵尸村民。", true));
		
		builder.add(new FilterGolemsSetting("不会跟随铁傀儡和雪傀儡。", true));
		
		builder.add(FilterPiglinsSetting.onOffOnly("不会跟随猪灵。", true));
		
		builder.add(FilterZombiePiglinsSetting.onOffOnly("不会跟随僵尸猪灵。", true));
		
		builder.add(FilterEndermenSetting.onOffOnly("不会跟随末影人。", true));
		
		builder.add(new FilterShulkersSetting("不会跟随潜影贝。", true));
		
		builder.add(new FilterAllaysSetting("不会跟随盟友。", true));
		
		builder.add(new FilterInvisibleSetting("不会跟随隐形的实体。", false));
		
		builder.add(new FilterArmorStandsSetting("不会跟随盔甲架。", true));
		
		builder.add(new FilterMinecartsSetting("不会跟随矿车。", true));
		
		return new FollowFilterList(builder);
	}
}
