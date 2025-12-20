/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.AttackSpeedSliderSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.filterlists.EntityFilterList;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.util.RotationUtils;

@SearchTags({"click aura", "ClickAimbot", "click aimbot"})
public final class ClickAuraHack extends Hack
	implements UpdateListener, LeftClickListener
{
	private final SliderSetting range =
		new SliderSetting("范围", 5, 1, 10, 0.05, ValueDisplay.DECIMAL);
	
	private final AttackSpeedSliderSetting speed =
		new AttackSpeedSliderSetting();
	
	private final EnumSetting<Priority> priority = new EnumSetting<>("优先级",
		"决定哪个实体会被优先攻击。\n"
			+ "\u00a7l距离\u00a7r - 攻击最近的实体。\n"
			+ "\u00a7l角度\u00a7r - 攻击需要最少头部移动的实体。\n"
			+ "\u00a7l生命值\u00a7r - 攻击最弱的实体。",
		Priority.values(), Priority.ANGLE);
	
	private final SliderSetting fov =
		new SliderSetting("视野", 360, 30, 360, 10, ValueDisplay.DEGREES);
	
	private final EntityFilterList entityFilters =
		EntityFilterList.genericCombat();
	
	public ClickAuraHack()
	{
		super("扳机（瞄准攻击");
		
		setCategory(Category.COMBAT);
		addSetting(range);
		addSetting(speed);
		addSetting(priority);
		addSetting(fov);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		// disable other killauras
		WURST.getHax().aimAssistHack.setEnabled(false);
		WURST.getHax().crystalAuraHack.setEnabled(false);
		WURST.getHax().fightBotHack.setEnabled(false);
		WURST.getHax().killauraLegitHack.setEnabled(false);
		WURST.getHax().killauraHack.setEnabled(false);
		WURST.getHax().multiAuraHack.setEnabled(false);
		WURST.getHax().protectHack.setEnabled(false);
		WURST.getHax().triggerBotHack.setEnabled(false);
		WURST.getHax().tpAuraHack.setEnabled(false);
		
		speed.resetTimer();
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(LeftClickListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!MC.options.keyAttack.isDown())
			return;
		
		speed.updateTimer();
		if(!speed.isTimeToAttack())
			return;
		
		attack();
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		attack();
	}
	
	private void attack()
	{
		// set entity
		LocalPlayer player = MC.player;
		Stream<Entity> stream = EntityUtils.getAttackableEntities();
		
		double rangeSq = Math.pow(range.getValue(), 2);
		stream = stream.filter(e -> player.distanceToSqr(e) <= rangeSq);
		
		if(fov.getValue() < 360.0)
			stream = stream.filter(e -> RotationUtils.getAngleToLookVec(
				e.getBoundingBox().getCenter()) <= fov.getValue() / 2.0);
		
		stream = entityFilters.applyTo(stream);
		
		Entity target =
			stream.min(priority.getSelected().comparator).orElse(null);
		if(target == null)
			return;
		
		WURST.getHax().autoSwordHack.setSlot(target);
		
		// face entity
		RotationUtils.getNeededRotations(target.getBoundingBox().getCenter())
			.sendPlayerLookPacket();
		
		// attack entity
		MC.gameMode.attack(player, target);
		player.swing(InteractionHand.MAIN_HAND);
		speed.resetTimer();
	}
	
	private enum Priority
	{
		DISTANCE("距离", e -> MC.player.distanceToSqr(e)),
		
		ANGLE("角度",
			e -> RotationUtils
				.getAngleToLookVec(e.getBoundingBox().getCenter())),
		
		HEALTH("生命值", e -> e instanceof LivingEntity
			? ((LivingEntity)e).getHealth() : Integer.MAX_VALUE);
		
		private final String name;
		private final Comparator<Entity> comparator;
		
		private Priority(String name, ToDoubleFunction<Entity> keyExtractor)
		{
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
