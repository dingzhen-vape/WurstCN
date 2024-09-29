/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
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
import net.wurstclient.settings.SwingHandSetting.SwingHand;
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
		"最大尝试次数", "BuildRandom 尝试在一刻内将方块放置在" + " 随机位置的最大数量。\n\n"
			+ "更高的值会加快建造过程，但代价是" + " 延迟增加。",
		128, 1, 1024, 1, ValueDisplay.INTEGER);
	
	private final CheckboxSetting checkItem =
		new CheckboxSetting("检查手持物品", "仅当您实际手持方块时才会建造。\n"
			+ "关闭此功能以使用火、水、熔岩、刷怪蛋建造，" + "或者如果您只想用空手右键单击" + "在随机位置。", true);
	
	private final CheckboxSetting checkLOS =
		new CheckboxSetting("检查视线", "确保 BuildRandom 不会尝试将方块放置在墙后面。", false);
	
	private final FacingSetting facing = FacingSetting.withoutPacketSpam(
		"BuildRandom 应如何面对随机放置的方块。\n\n" + "\u00a7lOff\u00a7r - 完全不面对方块。将被"
			+ " 反作弊插件检测到。\n\n" + "\u00a7l服务器端\u00a7r - 在" + " 服务器端面对方块，同时仍允许您在"
			+ " 客户端自由移动相机。\n\n" + "\u00a7l客户端\u00a7r - 通过在客户端移动您的"
			+ " 相机来面对方块。这是最合法的选项，但" + "看起来可能会非常令人迷惑。");
	
	private final SwingHandSetting swingHand =
		new SwingHandSetting(this, SwingHand.SERVER);
	
	private final CheckboxSetting fastPlace =
		new CheckboxSetting("始终快速放置", "即使未启用快速放置，也像启用了一样构建。", false);
	
	private final CheckboxSetting placeWhileBreaking = new CheckboxSetting(
		"破坏时放置", "即使在破坏方块时也会构建。\n" + "通过黑客攻击可行，但在原版中不起作用。可能看起来很可疑。", false);
	
	private final CheckboxSetting placeWhileRiding = new CheckboxSetting(
		"骑行时放置", "即使在骑车时也会构建。\n" + "通过黑客攻击可行，但在原版中不起作用。可能看起来很可疑。", false);
	
	private final CheckboxSetting indicator =
		new CheckboxSetting("指示器", "显示 BuildRandom 放置方块的位置。", true);
	
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
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
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
