/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.BlockBreakingProgressListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.BlockUtils;

@SearchTags({"FastMine", "SpeedMine", "SpeedyGonzales", "fast break",
	"fast mine", "speed mine", "speedy gonzales", "NoBreakDelay",
	"no break delay"})
public final class FastBreakHack extends Hack
	implements UpdateListener, BlockBreakingProgressListener
{
	private final SliderSetting activationChance = new SliderSetting(
		"激活几率",
		"只有给定几率的一些方块被你打破时才使用快速破坏,"
			+ "这使得反作弊插件更难检测到。\n\n"
			+ "如果启用了合法模式,这个设置没有任何作用。",
		1, 0, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	private final CheckboxSetting legitMode = new CheckboxSetting("合法模式",
		"只是移除打破方块之间的延迟,而不加速"
			+ "打破过程本身。\n\n"
			+ "这要慢得多,但是很好地绕过了反作弊插件。如果常规的快速破坏不起作用,而且激活"
			+ "几率滑块也没有帮助,请使用这个功能。"
			+ " chance slider doesn't help.",
		false);
	
	private final Random random = new Random();
	private BlockPos lastBlockPos;
	private boolean fastBreakBlock;
	
	public FastBreakHack()
	{
		super("快速破坏");
		setCategory(Category.BLOCKS);
		addSetting(activationChance);
		addSetting(legitMode);
	}
	
	@Override
	public String getRenderName()
	{
		if(legitMode.isChecked())
			return getName() + "合法";
		return getName();
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(BlockBreakingProgressListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(BlockBreakingProgressListener.class, this);
		lastBlockPos = null;
	}
	
	@Override
	public void onUpdate()
	{
		MC.gameMode.destroyDelay = 0;
	}
	
	@Override
	public void onBlockBreakingProgress(BlockBreakingProgressEvent event)
	{
		if(legitMode.isChecked())
			return;
		
		if(MC.gameMode.destroyProgress >= 1)
			return;
		
		BlockPos blockPos = event.getBlockPos();
		if(!blockPos.equals(lastBlockPos))
		{
			lastBlockPos = blockPos;
			fastBreakBlock = random.nextDouble() <= activationChance.getValue();
		}
		
		// Ignore unbreakable blocks to avoid slowdown issue
		if(BlockUtils.isUnbreakable(blockPos))
			return;
		
		if(!fastBreakBlock)
			return;
		
		Action action = ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK;
		Direction direction = event.getDirection();
		IMC.getInteractionManager().sendPlayerActionC2SPacket(action, blockPos,
			direction);
	}
}
