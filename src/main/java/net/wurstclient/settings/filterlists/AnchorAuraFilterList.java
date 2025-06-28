/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filterlists;

import java.util.ArrayList;
import java.util.List;

import net.wurstclient.settings.filters.*;

public final class AnchorAuraFilterList extends EntityFilterList
{
	private AnchorAuraFilterList(List<EntityFilter> filters)
	{
		super(filters);
	}
	
	public static AnchorAuraFilterList create()
	{
		ArrayList<EntityFilter> builder = new ArrayList<>();
		String damageWarning = "\n\n如果它们太接近有效的目标或现有的锚点，它们仍然会受到伤害。";
		
		builder.add(new FilterPlayersSetting(
			"Won't target other players when auto-placing anchors."
				+ damageWarning,
			false));
		
		builder.add(new FilterHostileSetting("Won't target hostile mobs like"
			+ " 在自动放置锚点时攻击僵尸和苦力怕。" + damageWarning, true));
		
		builder.add(new FilterNeutralSetting("Won't target neutral mobs like"
			+ " 在自动放置锚点时攻击末影人和狼。" + damageWarning,
			AttackDetectingEntityFilter.Mode.ON));
		
		builder.add(new FilterPassiveSetting("Won't target animals like pigs"
			+ " 和牛，环境生物如蝙蝠，以及水生生物如鱼，" + " 鱼和海豚。" + damageWarning, true));
		
		builder.add(new FilterPassiveWaterSetting("Won't target passive water"
			+ " 在自动放置锚点时攻击鱼、鱿鱼、海豚和水獭。" + " 锚点。" + damageWarning, true));
		
		builder.add(new FilterBatsSetting("Won't target bats and any other"
			+ " \"ambient\" 在自动放置锚点时攻击生物。" + damageWarning, true));
		
		builder.add(new FilterSlimesSetting(
			"Won't target slimes when" + " 自动放置锚点。" + damageWarning, true));
		
		builder.add(new FilterVillagersSetting(
			"Won't target villagers and" + " 在自动放置锚点时攻击流浪商人。" + damageWarning,
			true));
		
		builder.add(new FilterZombieVillagersSetting(
			"Won't target zombified" + " 在自动放置锚点时攻击村民。" + damageWarning, true));
		
		builder.add(new FilterGolemsSetting("Won't target iron golems and snow"
			+ " 在自动放置锚点时攻击地傀儡。" + damageWarning, true));
		
		builder.add(new FilterPiglinsSetting(
			"Won't target piglins when auto-placing anchors.",
			AttackDetectingEntityFilter.Mode.ON));
		
		builder.add(new FilterZombiePiglinsSetting(
			"Won't target" + " 在自动放置锚点时攻击僵尸化的猪灵。" + damageWarning,
			AttackDetectingEntityFilter.Mode.ON));
		
		builder.add(new FilterShulkersSetting(
			"Won't target shulkers when" + " 自动放置锚点。" + damageWarning, true));
		
		builder.add(new FilterAllaysSetting(
			"Won't target allays when auto-placing anchors." + damageWarning,
			true));
		
		builder.add(new FilterInvisibleSetting(
			"Won't target invisible entities when auto-placing anchors."
				+ damageWarning,
			false));
		
		builder.add(new FilterNamedSetting(
			"Won't target name-tagged entities when auto-placing anchors."
				+ damageWarning,
			false));
		
		builder.add(new FilterArmorStandsSetting(
			"Won't target armor stands when auto-placing anchors."
				+ damageWarning,
			true));
		
		return new AnchorAuraFilterList(builder);
	}
}
