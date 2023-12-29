/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.Category;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.hacks.portalesp.PortalEspBlockGroup;
import net.wurstclient.hacks.portalesp.PortalEspRenderer;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ChunkAreaSetting;
import net.wurstclient.settings.ColorSetting;
import net.wurstclient.settings.EspStyleSetting;
import net.wurstclient.util.ChunkSearcher.Result;
import net.wurstclient.util.ChunkSearcherCoordinator;
import net.wurstclient.util.RenderUtils;

public final class PortalEspHack extends Hack implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final EspStyleSetting style = new EspStyleSetting();

	private final PortalEspBlockGroup netherPortal =
			new PortalEspBlockGroup(Blocks.NETHER_PORTAL,
					new ColorSetting("下界传送门颜色",
						"下界传送门将以这种颜色突出显示。", Color.RED),
					new CheckboxSetting("包括下界传送门", true));

	private final PortalEspBlockGroup endPortal =
			new PortalEspBlockGroup(Blocks.END_PORTAL,
					new ColorSetting("末地传送门颜色",
						"末地传送门将以这种颜色突出显示。", Color.GREEN),
					new CheckboxSetting("包括末地传送门", true));

	private final PortalEspBlockGroup endPortalFrame = new PortalEspBlockGroup(
		Blocks.END_PORTAL_FRAME,
		new ColorSetting("末地传送门框架颜色",
			"末地传送门框架将以这种颜色突出显示。", Color.BLUE),
		new CheckboxSetting("包括末地传送门框架", true));

	private final PortalEspBlockGroup endGateway = new PortalEspBlockGroup(
			Blocks.END_GATEWAY,
			new ColorSetting("End gateway color",
					"End gateways will be highlighted in this color.", Color.YELLOW),
			new CheckboxSetting("Include end gateways", true));

	private final List<PortalEspBlockGroup> groups =
		Arrays.asList(netherPortal, endPortal, endPortalFrame, endGateway);

	private final ChunkAreaSetting area = new ChunkAreaSetting("区域",
		"玩家周围要搜索的区域。\n"
			+ "更高的值需要更快的电脑。");

	private final BiPredicate<BlockPos, BlockState> query =
		(pos, state) -> state.getBlock() == Blocks.NETHER_PORTAL
			|| state.getBlock() == Blocks.END_PORTAL
			|| state.getBlock() == Blocks.END_PORTAL_FRAME
			|| state.getBlock() == Blocks.END_GATEWAY;

	private final ChunkSearcherCoordinator coordinator =
		new ChunkSearcherCoordinator(query, area);

	private boolean groupsUpToDate;

	public PortalEspHack()
	{
		super("传送门ESP");
		setCategory(Category.RENDER);

		addSetting(style);
		groups.stream().flatMap(PortalEspBlockGroup::getSettings)
			.forEach(this::addSetting);
		addSetting(area);
	}

	@Override
	public void onEnable()
	{
		groupsUpToDate = false;

		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(PacketInputListener.class, coordinator);
		EVENTS.add(CameraTransformViewBobbingListener.class, this);
		EVENTS.add(RenderListener.class, this);

		PortalEspRenderer.prepareBuffers();
	}

	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(PacketInputListener.class, coordinator);
		EVENTS.remove(CameraTransformViewBobbingListener.class, this);
		EVENTS.remove(RenderListener.class, this);

		coordinator.reset();
		groups.forEach(PortalEspBlockGroup::clear);
		PortalEspRenderer.closeBuffers();
	}

	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(style.getSelected().hasLines())
			event.cancel();
	}

	@Override
	public void onUpdate()
	{
		boolean searchersChanged = coordinator.update();
		if(searchersChanged)
			groupsUpToDate = false;

		if(!groupsUpToDate && coordinator.isDone())
			updateGroupBoxes();
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

		PortalEspRenderer espRenderer =
			new PortalEspRenderer(matrixStack, partialTicks);

		if(style.getSelected().hasBoxes())
		{
			RenderSystem.setShader(GameRenderer::getPositionProgram);
			groups.stream().filter(PortalEspBlockGroup::isEnabled)
				.forEach(espRenderer::renderBoxes);
		}

		if(style.getSelected().hasLines())
		{
			RenderSystem.setShader(GameRenderer::getPositionProgram);
			groups.stream().filter(PortalEspBlockGroup::isEnabled)
				.forEach(espRenderer::renderLines);
		}

		matrixStack.pop();

		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void updateGroupBoxes()
	{
		groups.forEach(PortalEspBlockGroup::clear);
		coordinator.getMatches().forEach(this::addToGroupBoxes);
		groupsUpToDate = true;
	}

	private void addToGroupBoxes(Result result)
	{
		for(PortalEspBlockGroup group : groups)
			if(result.state().getBlock() == group.getBlock())
			{
				group.add(result.pos());
				break;
			}
	}
}
