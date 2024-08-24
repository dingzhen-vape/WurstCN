/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
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
		
		builder.add(new FilterPlayersSetting(
			"description.wurst.setting.follow.filter_players", false));
		
		builder.add(new FilterSleepingSetting(
			"description.wurst.setting.follow.filter_sleeping", false));
		
		builder.add(new FilterFlyingSetting(
			"description.wurst.setting.follow.filter_flying", 0));
		
		builder.add(new FilterHostileSetting("自动跟随时不会跟随像僵尸和苦力怕这样的敌对生物。", true));
		
		builder.add(FilterNeutralSetting.onOffOnly(
			"description.wurst.setting.follow.filter_neutral", true));
		
		builder.add(new FilterPassiveSetting(
			"自动跟随时不会跟随像猪和牛这样的动物、像蝙蝠这样的环境生物，以及像鱼、鱿鱼和海豚这样的水生生物。", true));
		
		builder.add(new FilterPassiveWaterSetting(
			"自动跟随时不会跟随像鱼、鱿鱼、海豚和美洲狮这样的被动水生生物。", true));
		
		builder.add(new FilterBabiesSetting("自动跟随时不会跟随小猪、小村民等。", true));
		
		builder.add(new FilterBatsSetting(
			"description.wurst.setting.follow.filter_bats", true));
		
		builder.add(new FilterSlimesSetting("自动跟随时不会跟随史莱姆。", true));
		
		builder.add(new FilterPetsSetting(
			"description.wurst.setting.follow.filter_pets", true));
		
		builder.add(new FilterVillagersSetting(
			"description.wurst.setting.follow.filter_villagers", true));
		
		builder.add(new FilterZombieVillagersSetting(
			"description.wurst.setting.follow.filter_zombie_villagers", true));
		
		builder.add(new FilterGolemsSetting(
			"description.wurst.setting.follow.filter_golems", true));
		
		builder.add(FilterPiglinsSetting.onOffOnly("自动跟随时不会跟随猪灵。", true));
		
		builder.add(FilterZombiePiglinsSetting.onOffOnly(
			"description.wurst.setting.follow.filter_zombie_piglins", true));
		
		builder.add(FilterEndermenSetting.onOffOnly(
			"description.wurst.setting.follow.filter_endermen", true));
		
		builder.add(new FilterShulkersSetting(
			"description.wurst.setting.follow.filter_shulkers", true));
		
		builder.add(new FilterAllaysSetting(
			"description.wurst.setting.follow.filter_allays", true));
		
		builder.add(new FilterInvisibleSetting(
			"description.wurst.setting.follow.filter_invisible", false));
		
		builder.add(new FilterArmorStandsSetting(
			"description.wurst.setting.follow.filter_armor_stands", true));
		
		builder.add(new FilterMinecartsSetting(
			"description.wurst.setting.follow.filter_minecarts", true));
		
		return new FollowFilterList(builder);
	}
}
