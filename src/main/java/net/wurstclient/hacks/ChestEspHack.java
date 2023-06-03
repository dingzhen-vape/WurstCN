/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.entity.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.wurstclient.Category;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.hacks.chestesp.ChestEspBlockGroup;
import net.wurstclient.hacks.chestesp.ChestEspEntityGroup;
import net.wurstclient.hacks.chestesp.ChestEspGroup;
import net.wurstclient.hacks.chestesp.ChestEspRenderer;
import net.wurstclient.hacks.chestesp.ChestEspStyle;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.util.ChunkUtils;
import net.wurstclient.util.RenderUtils;

public class ChestEspHack extends Hack implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final EnumSetting<ChestEspStyle> style =
		new EnumSetting<>("样式", ChestEspStyle.values(), ChestEspStyle.BOXES);
	
	private final ChestEspBlockGroup basicChests = new ChestEspBlockGroup(
		new ColorSetting("箱子颜色",
			"普通箱子将以这种颜色突出显示。", Color.GREEN),
		null);
	
	private final ChestEspBlockGroup trapChests = new ChestEspBlockGroup(
		new ColorSetting("陷阱箱颜色",
			"陷阱箱将以这种颜色突出显示。",
			new Color(0xFF8000)),
		new CheckboxSetting("包括陷阱箱", true));
	
	private final ChestEspBlockGroup enderChests = new ChestEspBlockGroup(
		new ColorSetting("末影箱颜色",
			"末影箱将以这种颜色突出显示。", Color.CYAN),
		new CheckboxSetting("包括末影箱", true));
	
	private final ChestEspEntityGroup chestCarts =
		new ChestEspEntityGroup(
			new ColorSetting("箱子车颜色",
				"带有箱子的矿车将以这种颜色突出显示。",
				Color.YELLOW),
			new CheckboxSetting("包括箱子车", true));
	
	private final ChestEspEntityGroup chestBoats =
		new ChestEspEntityGroup(
			new ColorSetting("箱子船颜色",
				"带有箱子的船只将以这种颜色突出显示。",
				Color.YELLOW),
			new CheckboxSetting("包括箱子船", true));
	
	private final ChestEspBlockGroup barrels = new ChestEspBlockGroup(
		new ColorSetting("桶颜色",
			"桶将以这种颜色突出显示。", Color.GREEN),
		new CheckboxSetting("包括桶", true));
	
	private final ChestEspBlockGroup shulkerBoxes = new ChestEspBlockGroup(
		new ColorSetting("潜影盒颜色",
			"潜影盒将以这种颜色突出显示。", Color.MAGENTA),
		new CheckboxSetting("包括潜影盒", true));
	
	private final ChestEspBlockGroup hoppers = new ChestEspBlockGroup(
		new ColorSetting("漏斗颜色",
			"漏斗将以这种颜色突出显示。", Color.WHITE),
		new CheckboxSetting("包括漏斗", false));
	
	private final ChestEspEntityGroup hopperCarts =
		new ChestEspEntityGroup(
			new ColorSetting("漏斗车颜色",
				"带有漏斗的矿车将以这种颜色突出显示。",
				Color.YELLOW),
			new CheckboxSetting("包括漏斗车", false));
	
	private final ChestEspBlockGroup droppers = new ChestEspBlockGroup(
		new ColorSetting("投掷器颜色",
			"投掷器将以这种颜色突出显示。", Color.WHITE),
		new CheckboxSetting("包括投掷器", false));
	
	private final ChestEspBlockGroup dispensers = new ChestEspBlockGroup(
		new ColorSetting("发射器颜色",
			"发射器将以这种颜色突出显示。",
			new Color(0xFF8000)),
		new CheckboxSetting("包括发射器", false));
	
	private final ChestEspBlockGroup furnaces =
		new ChestEspBlockGroup(new ColorSetting("熔炉颜色",
			"熔炉、烟熏炉和高炉将以这种颜色突出显示。",
			Color.RED), new CheckboxSetting("包括熔炉", false));
	
	private final List<ChestEspGroup> groups = Arrays.asList(basicChests,
		trapChests, enderChests, chestCarts, chestBoats, barrels, shulkerBoxes,
		hoppers, hopperCarts, droppers, dispensers, furnaces);
	
	private final List<ChestEspEntityGroup> entityGroups =
		Arrays.asList(chestCarts, chestBoats, hopperCarts);
	
	public ChestEspHack()
	{
		super("箱子透视");
		setCategory(Category.RENDER);
		
		addSetting(style);
		groups.stream().flatMap(ChestEspGroup::getSettings)
			.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(CameraTransformViewBobbingListener.class, this);
		EVENTS.add(RenderListener.class, this);
		
		ChestEspRenderer.prepareBuffers();
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(CameraTransformViewBobbingListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		
		groups.forEach(ChestEspGroup::clear);
		ChestEspRenderer.closeBuffers();
	}
	
	@Override
	public void onUpdate()
	{
		groups.forEach(ChestEspGroup::clear);
		
		ArrayList<BlockEntity> blockEntities =
			ChunkUtils.getLoadedBlockEntities()
				.collect(Collectors.toCollection(ArrayList::new));
		
		for(BlockEntity blockEntity : blockEntities)
			if(blockEntity instanceof TrappedChestBlockEntity)
				trapChests.add(blockEntity);
			else if(blockEntity instanceof ChestBlockEntity)
				basicChests.add(blockEntity);
			else if(blockEntity instanceof EnderChestBlockEntity)
				enderChests.add(blockEntity);
			else if(blockEntity instanceof ShulkerBoxBlockEntity)
				shulkerBoxes.add(blockEntity);
			else if(blockEntity instanceof BarrelBlockEntity)
				barrels.add(blockEntity);
			else if(blockEntity instanceof HopperBlockEntity)
				hoppers.add(blockEntity);
			else if(blockEntity instanceof DropperBlockEntity)
				droppers.add(blockEntity);
			else if(blockEntity instanceof DispenserBlockEntity)
				dispensers.add(blockEntity);
			else if(blockEntity instanceof AbstractFurnaceBlockEntity)
				furnaces.add(blockEntity);
			
		for(Entity entity : MC.world.getEntities())
			if(entity instanceof ChestMinecartEntity)
				chestCarts.add(entity);
			else if(entity instanceof HopperMinecartEntity)
				hopperCarts.add(entity);
			else if(entity instanceof ChestBoatEntity)
				chestBoats.add(entity);
	}
	
	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(style.getSelected().hasLines())
			event.cancel();
	}
	
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);
		
		entityGroups.stream().filter(ChestEspGroup::isEnabled)
			.forEach(g -> g.updateBoxes(partialTicks));
		
		ChestEspRenderer espRenderer = new ChestEspRenderer(matrixStack);
		
		if(style.getSelected().hasBoxes())
		{
			RenderSystem.setShader(GameRenderer::getPositionProgram);
			groups.stream().filter(ChestEspGroup::isEnabled)
				.forEach(espRenderer::renderBoxes);
		}
		
		if(style.getSelected().hasLines())
		{
			RenderSystem.setShader(GameRenderer::getPositionProgram);
			groups.stream().filter(ChestEspGroup::isEnabled)
				.forEach(espRenderer::renderLines);
		}
		
		matrixStack.pop();
		
		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
