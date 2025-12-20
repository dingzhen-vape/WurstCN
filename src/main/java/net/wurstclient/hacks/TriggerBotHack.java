/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.HandleInputListener;
import net.wurstclient.events.PreMotionListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.AttackSpeedSliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.SwingHandSetting;
import net.wurstclient.settings.SwingHandSetting.SwingHand;
import net.wurstclient.settings.filterlists.EntityFilterList;
import net.wurstclient.util.EntityUtils;

@SearchTags({"trigger bot", "AutoAttack", "auto attack", "AutoClicker",
	"auto clicker"})
public final class TriggerBotHack extends Hack
	implements PreMotionListener, HandleInputListener
{
	private final SliderSetting range =
		new SliderSetting("范围", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final AttackSpeedSliderSetting speed =
		new AttackSpeedSliderSetting();
	
	private final SliderSetting speedRandMS =
		new SliderSetting("速度随机化",
			"通过在攻击之间变化延迟来帮助你绕过反作弊插件"
				+ "攻击。\n\n" + "\u00b1100毫秒是 Vulcan 的推荐值。\n\n"
				+ "0 (关闭) 对于 NoCheat+、AAC、Grim、Verus、Spartan 和"
				+ " 经典服务器是合适的。",
			100, 0, 1000, 50, ValueDisplay.INTEGER.withPrefix("\u00b1")
				.withSuffix("毫秒").withLabel(0, "关闭"));
	
	private final SwingHandSetting swingHand =
		new SwingHandSetting(this, SwingHand.CLIENT);
	
	private final CheckboxSetting attackWhileBlocking =
		new CheckboxSetting("在阻挡时攻击",
			"Attacks even while you're blocking with a shield or using"
				+ " 物品。\n\n"
				+ "This would not be possible in vanilla and won't work if"
				+ " \"Simulate mouse click\"开启时生效。",
			false);
	
	private final CheckboxSetting simulateMouseClick = new CheckboxSetting(
		"模拟鼠标点击",
		"在攻击时模拟实际的鼠标点击（或按键）。可以被"
			+ " used to trick CPS measuring tools into thinking that you're"
			+ " 是手动攻击。\n\n"
			+ "\u00a7c\u00a7l警告:\u00a7r 模拟鼠标点击可能导致"
			+ " 出现意外行为，例如游戏菜单自己点击。"
			+ " 此外，Swing hand 和 Attack while blocking 设置"
			+ " 在此选项启用时将无法工作。",
		false);
	
	private final EntityFilterList entityFilters =
		EntityFilterList.genericCombat();
	
	private boolean simulatingMouseClick;
	
	public TriggerBotHack()
	{
		super("扳机机器人");
		setCategory(Category.COMBAT);
		
		addSetting(range);
		addSetting(speed);
		addSetting(speedRandMS);
		addSetting(swingHand);
		addSetting(attackWhileBlocking);
		addSetting(simulateMouseClick);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		// disable other killauras
		WURST.getHax().clickAuraHack.setEnabled(false);
		WURST.getHax().crystalAuraHack.setEnabled(false);
		WURST.getHax().fightBotHack.setEnabled(false);
		WURST.getHax().killauraLegitHack.setEnabled(false);
		WURST.getHax().killauraHack.setEnabled(false);
		WURST.getHax().multiAuraHack.setEnabled(false);
		WURST.getHax().protectHack.setEnabled(false);
		WURST.getHax().tpAuraHack.setEnabled(false);
		
		speed.resetTimer(speedRandMS.getValue());
		EVENTS.add(PreMotionListener.class, this);
		EVENTS.add(HandleInputListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		if(simulatingMouseClick)
		{
			IKeyBinding.get(MC.options.keyAttack).simulatePress(false);
			simulatingMouseClick = false;
		}
		
		EVENTS.remove(PreMotionListener.class, this);
		EVENTS.remove(HandleInputListener.class, this);
	}
	
	@Override
	public void onPreMotion()
	{
		if(!simulatingMouseClick)
			return;
		
		IKeyBinding.get(MC.options.keyAttack).simulatePress(false);
		simulatingMouseClick = false;
	}
	
	@Override
	public void onHandleInput()
	{
		speed.updateTimer();
		if(!speed.isTimeToAttack())
			return;
		
		// don't attack when a container/inventory screen is open
		if(MC.screen instanceof AbstractContainerScreen)
			return;
		
		LocalPlayer player = MC.player;
		if(!attackWhileBlocking.isChecked() && player.isUsingItem())
			return;
		
		if(MC.hitResult == null
			|| !(MC.hitResult instanceof EntityHitResult eResult))
			return;
		
		Entity target = eResult.getEntity();
		if(!isCorrectEntity(target))
			return;
		
		WURST.getHax().autoSwordHack.setSlot(target);
		
		if(simulateMouseClick.isChecked())
		{
			IKeyBinding.get(MC.options.keyAttack).simulatePress(true);
			simulatingMouseClick = true;
			
		}else
		{
			MC.gameMode.attack(player, target);
			swingHand.swing(InteractionHand.MAIN_HAND);
		}
		
		speed.resetTimer(speedRandMS.getValue());
	}
	
	private boolean isCorrectEntity(Entity entity)
	{
		if(!EntityUtils.IS_ATTACKABLE.test(entity))
			return false;
		
		if(MC.player.distanceToSqr(entity) > range.getValueSq())
			return false;
		
		return entityFilters.testOne(entity);
	}
}
