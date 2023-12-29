/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.FacingSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.SwingHandSetting;
import net.wurstclient.util.BlockPlacer;
import net.wurstclient.util.BlockPlacer.BlockPlacingParams;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.util.InteractionSimulator;
import net.wurstclient.util.RegionPos;
import net.wurstclient.util.RenderUtils;
import net.wurstclient.util.RotationUtils;

@SearchTags({"build random", "RandomBuild", "random build", "PlaceRandom",
	"place random", "RandomPlace", "random place"})
public final class BuildRandomHack extends Hack
	implements UpdateListener, RenderListener
{
	private final SliderSetting range =
		new SliderSetting("范围", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private SliderSetting maxAttempts = new SliderSetting(
		"最大尝试次数", "BuildRandom在一次tick中尝试放置一个方块的最大随机位置数。\n\n"
			+ "更高的值可以加快建造过程，但是会增加" + "延迟。" + "检查手持物品",
		128, 1, 1024, 1, ValueDisplay.INTEGER);
	
	private final CheckboxSetting checkItem =
		new CheckboxSetting("只有当你真正拿着一个方块时才会建造。\n",
			"关闭这个选项可以用火，水，岩浆，刷怪蛋，" + "或者如果你只想用空手在随机的地方右键点击" + "就可以建造。" + "检查视线",
			true);
	
	private final CheckboxSetting checkLOS = new CheckboxSetting(
		"确保BuildRandom不会尝试在墙后放置方块。", "BuildRandom应该如何面向随机放置的方块。\n\n", false);
	
	private final FacingSetting facing = FacingSetting
		.withoutPacketSpam("\u00a7lOff\u00a7r - 不要面向方块。会被反作弊插件检测到。\n\n"
			+ "\u00a7lServer-side\u00a7r - 在服务器端面向方块，同时在" + "客户端自由地移动摄像头。\n\n"
			+ "\u00a7lClient-side\u00a7r - 通过在客户端移动摄像头来面向方块。这是最合法的选项，但是"
			+ "看起来非常令人眼花缭乱。" + "BuildRandom应该如何挥动手臂来放置方块。\n\n"
			+ "\u00a7lOff\u00a7r - 不要挥动手臂。会被反作弊插件检测到" + "。\n\n"
			+ "\u00a7lServer-side\u00a7r - 在服务器端挥动手臂，");
	
	private final SwingHandSetting swingHand = new SwingHandSetting(
		"而不在客户端播放动画。\n\n" + "\u00a7lClient-side\u00a7r - 在客户端挥动手臂。这是最合法的选项。"
			+ "始终启用FastPlace" + "即使没有启用FastPlace，也会像启用了一样建造。" + "边破坏边放置"
			+ "即使你正在破坏一个方块，也会继续建造。\n" + "使用外挂可以做到，但是在原版中不行。可能看起来很可疑。");
	
	private final CheckboxSetting fastPlace =
		new CheckboxSetting("边骑乘边放置", "即使你正在骑乘一个载具，也会继续建造。\n", false);
	
	private final CheckboxSetting placeWhileBreaking =
		new CheckboxSetting("使用外挂可以做到，但是在原版中不行。可能看起来很可疑。",
			"指示器" + "显示BuildRandom正在放置方块的位置。", false);
	
	private final CheckboxSetting placeWhileRiding = new CheckboxSetting("随机建造",
		"Builds even while you are riding a vehicle.\n"
			+ "显示BuildRandom正在放置方块的位置。",
		false);
	
	private final CheckboxSetting indicator = new CheckboxSetting("Indicator",
		"Shows where BuildRandom is placing blocks.", true);
	
	private final Random random = new Random();
	private BlockPos lastPos;
	
	public BuildRandomHack()
	{
		super("随机建造");
		setCategory(Category.BLOCKS);
		addSetting(range);
		addSetting(maxAttempts);
		addSetting(checkItem);
		addSetting(checkLOS);
		addSetting(facing);
		addSetting(swingHand);
		addSetting(fastPlace);
		addSetting(placeWhileBreaking);
		addSetting(placeWhileRiding);
		addSetting(indicator);
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		lastPos = null;
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		lastPos = null;
		
		if(WURST.getHax().freecamHack.isEnabled())
			return;
		
		if(!fastPlace.isChecked() && MC.itemUseCooldown > 0)
			return;
		
		if(checkItem.isChecked() && !MC.player.isHolding(
			stack -> !stack.isEmpty() && stack.getItem() instanceof BlockItem))
			return;
		
		if(!placeWhileBreaking.isChecked()
			&& MC.interactionManager.isBreakingBlock())
			return;
		
		if(!placeWhileRiding.isChecked() && MC.player.isRiding())
			return;
		
		int maxAttempts = this.maxAttempts.getValueI();
		int blockRange = range.getValueCeil();
		int bound = blockRange * 2 + 1;
		BlockPos pos;
		int attempts = 0;
		
		do
		{
			// generate random position
			pos = BlockPos.ofFloored(RotationUtils.getEyesPos()).add(
				random.nextInt(bound) - blockRange,
				random.nextInt(bound) - blockRange,
				random.nextInt(bound) - blockRange);
			attempts++;
			
		}while(attempts < maxAttempts && !tryToPlaceBlock(pos));
	}
	
	private boolean tryToPlaceBlock(BlockPos pos)
	{
		if(!BlockUtils.getState(pos).isReplaceable())
			return false;
		
		BlockPlacingParams params = BlockPlacer.getBlockPlacingParams(pos);
		if(params == null || params.distanceSq() > range.getValueSq())
			return false;
		if(checkLOS.isChecked() && !params.lineOfSight())
			return false;
		
		MC.itemUseCooldown = 4;
		facing.getSelected().face(params.hitVec());
		lastPos = pos;
		
		InteractionSimulator.rightClickBlock(params.toHitResult(),
			swingHand.getSelected());
		return true;
	}
	
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		if(lastPos == null || !indicator.isChecked())
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		matrixStack.push();
		
		RegionPos region = RenderUtils.getCameraRegion();
		RenderUtils.applyRegionalRenderOffset(matrixStack, region);
		
		// set position
		matrixStack.translate(lastPos.getX() - region.x(), lastPos.getY(),
			lastPos.getZ() - region.z());
		
		// get color
		float red = partialTicks * 2F;
		float green = 2 - red;
		
		// draw box
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(red, green, 0, 0.25F);
		RenderUtils.drawSolidBox(matrixStack);
		RenderSystem.setShaderColor(red, green, 0, 0.5F);
		RenderUtils.drawOutlinedBox(matrixStack);
		
		matrixStack.pop();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
