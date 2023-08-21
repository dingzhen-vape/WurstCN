/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.DontSaveState;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.BlockListSetting;
import net.wurstclient.settings.BlockSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.BlockBreaker;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.util.RotationUtils;

@SearchTags({"speed nuker", "FastNuker", "fast nuker"})
@DontSaveState
public final class SpeedNukerHack extends Hack
	implements LeftClickListener, UpdateListener
{
	private final SliderSetting range =
		new SliderSetting("范围", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final EnumSetting<Mode> mode = new EnumSetting<>("模式",
		"\u00a7l普通\u00a7r 模式简单地破坏你周围的一切。\n"
			+ "\u00a7lID\u00a7r 模式只破坏选定的方块类型。左键点击一个方块来选择它。\n"
			+ "\u00a7l多ID\u00a7r 模式只破坏你的多ID列表中的方块类型。\n"
			+ "\u00a7l平整\u00a7r 模式平整你周围的区域,但不会挖下去。\n"
			+ "\u00a7l粉碎\u00a7r 模式只破坏可以立即被摧毁的方块（例如高草）。",
		Mode.values(), Mode.NORMAL);
	
	private final BlockSetting id =
		new BlockSetting("ID", "在ID模式中要破坏的方块类型。\n"
			+ "air = won't break anything", "minecraft:air", true);
	
	private final CheckboxSetting lockId = new CheckboxSetting("锁定ID",
		"防止通过点击方块或重启快速核弹器来改变ID。",
		false);
	
	private final BlockListSetting multiIdList = new BlockListSetting(
		"多ID列表", "在多ID模式中要破坏的方块类型。",
		"minecraft:ancient_debris", "minecraft:bone_block",
		"minecraft:coal_ore", "minecraft:copper_ore",
		"minecraft:deepslate_coal_ore", "minecraft:deepslate_copper_ore",
		"minecraft:deepslate_diamond_ore", "minecraft:deepslate_emerald_ore",
		"minecraft:deepslate_gold_ore", "minecraft:deepslate_iron_ore",
		"minecraft:deepslate_lapis_ore", "minecraft:deepslate_redstone_ore",
		"minecraft:diamond_ore", "minecraft:emerald_ore", "minecraft:glowstone",
		"minecraft:gold_ore", "minecraft:iron_ore", "minecraft:lapis_ore",
		"minecraft:nether_gold_ore", "minecraft:nether_quartz_ore",
		"minecraft:raw_copper_block", "minecraft:raw_gold_block",
		"minecraft:raw_iron_block", "minecraft:redstone_ore");
	
	public SpeedNukerHack()
	{
		super("快速Nuker");
		
		setCategory(Category.BLOCKS);
		addSetting(range);
		addSetting(mode);
		addSetting(id);
		addSetting(lockId);
		addSetting(multiIdList);
	}
	
	@Override
	public String getRenderName()
	{
		return mode.getSelected().renderName.apply(this);
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		WURST.getHax().autoMineHack.setEnabled(false);
		WURST.getHax().excavatorHack.setEnabled(false);
		WURST.getHax().nukerHack.setEnabled(false);
		WURST.getHax().nukerLegitHack.setEnabled(false);
		WURST.getHax().tunnellerHack.setEnabled(false);
		
		// add listeners
		EVENTS.add(LeftClickListener.class, this);
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		EVENTS.remove(LeftClickListener.class, this);
		EVENTS.remove(UpdateListener.class, this);
		
		// resets
		if(!lockId.isChecked())
			id.setBlock(Blocks.AIR);
	}
	
	@Override
	public void onUpdate()
	{
		// abort if using IDNuker without an ID being set
		if(mode.getSelected() == Mode.ID && id.getBlock() == Blocks.AIR)
			return;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks = getValidBlocks(range.getValue(),
			pos -> mode.getSelected().validator.test(this, pos));
		
		Iterator<BlockPos> autoToolIterator = validBlocks.iterator();
		if(autoToolIterator.hasNext())
			WURST.getHax().autoToolHack.equipIfEnabled(autoToolIterator.next());
		
		// break all blocks
		BlockBreaker.breakBlocksWithPacketSpam(validBlocks);
	}
	
	private ArrayList<BlockPos> getValidBlocks(double range,
		Predicate<BlockPos> validator)
	{
		Vec3d eyesVec = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		double rangeSq = Math.pow(range + 0.5, 2);
		int rangeI = (int)Math.ceil(range);
		
		BlockPos center = BlockPos.ofFloored(RotationUtils.getEyesPos());
		BlockPos min = center.add(-rangeI, -rangeI, -rangeI);
		BlockPos max = center.add(rangeI, rangeI, rangeI);
		
		return BlockUtils.getAllInBox(min, max).stream()
			.filter(pos -> eyesVec.squaredDistanceTo(Vec3d.of(pos)) <= rangeSq)
			.filter(BlockUtils::canBeClicked).filter(validator)
			.sorted(Comparator.comparingDouble(
				pos -> eyesVec.squaredDistanceTo(Vec3d.of(pos))))
			.collect(Collectors.toCollection(ArrayList::new));
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		// check mode
		if(mode.getSelected() != Mode.ID)
			return;
		
		if(lockId.isChecked())
			return;
		
		// check hitResult
		if(MC.crosshairTarget == null
			|| !(MC.crosshairTarget instanceof BlockHitResult))
			return;
		
		// check pos
		BlockPos pos = ((BlockHitResult)MC.crosshairTarget).getBlockPos();
		if(pos == null || BlockUtils.getBlock(pos) == Blocks.AIR)
			return;
		
		// set id
		id.setBlockName(BlockUtils.getName(pos));
	}
	
	private enum Mode
	{
		NORMAL("普通", n -> "快速Nuker", (n, pos) -> true),
		
		ID("ID",
			n -> "ID快速核弹器 ["
				+ n.id.getBlockName().replace("minecraft:", "") + "]",
			(n, pos) -> BlockUtils.getName(pos).equals(n.id.getBlockName())),
		
		MULTI_ID("多ID",
			n -> "多ID快速核弹器 [" + n.multiIdList.getBlockNames().size()
				+ (n.multiIdList.getBlockNames().size() == 1 ? " ID]"
					: " IDs]"),
			(n, p) -> n.multiIdList.getBlockNames()
				.contains(BlockUtils.getName(p))),
		
		FLAT("平整", n -> "平整快速核弹器",
			(n, pos) -> pos.getY() >= MC.player.getY()),
		
		SMASH("粉碎", n -> "粉碎快速核弹器",
			(n, pos) -> BlockUtils.getHardness(pos) >= 1);
		
		private final String name;
		private final Function<SpeedNukerHack, String> renderName;
		private final BiPredicate<SpeedNukerHack, BlockPos> validator;
		
		private Mode(String name, Function<SpeedNukerHack, String> renderName,
			BiPredicate<SpeedNukerHack, BlockPos> validator)
		{
			this.name = name;
			this.renderName = renderName;
			this.validator = validator;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
