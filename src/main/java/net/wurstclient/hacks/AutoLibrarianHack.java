/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.hacks.autolibrarian.BookOffer;
import net.wurstclient.hacks.autolibrarian.UpdateBooksSetting;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.BookOffersSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.FacingSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.SwingHandSetting;
import net.wurstclient.util.*;
import net.wurstclient.util.BlockBreaker.BlockBreakingParams;
import net.wurstclient.util.BlockPlacer.BlockPlacingParams;

@SearchTags({"auto librarian", "AutoVillager", "auto villager",
	"VillagerTrainer", "villager trainer", "LibrarianTrainer",
	"librarian trainer", "AutoHmmm", "auto hmmm"})
public final class AutoLibrarianHack extends Hack
	implements UpdateListener, RenderListener
{
	private final BookOffersSetting wantedBooks = new BookOffersSetting("想要的书",
		"你想让你的村民出售的附魔书的列表。\n\n" + "AutoLibrarian会停止训练当前的村民"
			+ "一旦它学会了出售这些书中的一本。\n\n" + "你也可以为每本书设置一个最高价格，以防你"
			+ "已经有一个村民出售它，但你想要一个" + "更便宜的价格。",
		"minecraft:depth_strider", "minecraft:efficiency",
		"minecraft:feather_falling", "minecraft:fortune", "minecraft:looting",
		"minecraft:mending", "minecraft:protection", "minecraft:respiration",
		"minecraft:sharpness", "minecraft:silk_touch", "minecraft:unbreaking");
	
	private final CheckboxSetting lockInTrade = new CheckboxSetting("锁定交易",
		"一旦村民学会了出售你想要的书，就自动从村民那里买点东西。这样可以防止村民" + "以后改变它的交易报价。\n\n"
			+ "使用这个功能时，请确保你的" + "库存中至少有24张纸和9个绿宝石。或者，1本书和" + "64个绿宝石也可以。"
			+ "范围",
		false);
	
	private final UpdateBooksSetting updateBooks = new UpdateBooksSetting();
	
	private final SliderSetting range =
		new SliderSetting("距离\n\n", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final FacingSetting facing = FacingSetting
		.withoutPacketSpam("\u00a7lOff\u00a7r - 根本不要面对村民。会被" + "反作弊插件检测到。\n\n"
			+ "\u00a7lServer-side\u00a7r - 在" + "服务器端面对村民，同时让你在" + "客户端自由地移动相机。"
			+ "客户端。\n\n" + "\u00a7lClient-side\u00a7r - 通过移动你的"
			+ "客户端上的相机来面对村民。这是最合法的选项，但" + "看起来可能会让人头晕。");
	
	private final SwingHandSetting swingHand =
		new SwingHandSetting("如何在与" + "村民和工作场所互动时挥动你的手。\n\n"
			+ "\u00a7lOff\u00a7r - 根本不要挥动你的手。会被检测到" + "反作弊插件。\n\n"
			+ "\u00a7lServer-side\u00a7r - 在服务器端挥动你的手，" + "而不在客户端播放动画。\n\n"
			+ "\u00a7lClient-side\u00a7r - 在客户端挥动你的手。" + "这是最合法的选项。");
	
	private final SliderSetting repairMode = new SliderSetting("修复模式",
		"防止AutoLibrarian在你的斧头的耐久度达到" + "给定的阈值时使用它，这样你可以在它坏掉之前修复它。\n"
			+ "可以从0（关闭）到100进行调整。",
		1, 0, 100, 1, ValueDisplay.INTEGER.withLabel(0, "关闭"));
	
	private final OverlayRenderer overlay = new OverlayRenderer();
	private final HashSet<VillagerEntity> experiencedVillagers =
		new HashSet<>();
	
	private VillagerEntity villager;
	private BlockPos jobSite;
	
	private boolean placingJobSite;
	private boolean breakingJobSite;
	
	public AutoLibrarianHack()
	{
		super("自动图书馆管理员");
		setCategory(Category.OTHER);
		addSetting(wantedBooks);
		addSetting(lockInTrade);
		addSetting(updateBooks);
		addSetting(range);
		addSetting(facing);
		addSetting(swingHand);
		addSetting(repairMode);
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
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		
		if(breakingJobSite)
		{
			MC.interactionManager.breakingBlock = true;
			MC.interactionManager.cancelBlockBreaking();
			breakingJobSite = false;
		}
		
		overlay.resetProgress();
		villager = null;
		jobSite = null;
		placingJobSite = false;
		breakingJobSite = false;
		experiencedVillagers.clear();
	}
	
	@Override
	public void onUpdate()
	{
		if(villager == null)
		{
			setTargetVillager();
			return;
		}
		
		if(jobSite == null)
		{
			setTargetJobSite();
			return;
		}
		
		if(placingJobSite && breakingJobSite)
			throw new IllegalStateException("试图同时放置和打破工作场所。出了点问题。");
		
		if(placingJobSite)
		{
			placeJobSite();
			return;
		}
		
		if(breakingJobSite)
		{
			breakJobSite();
			return;
		}
		
		if(!(MC.currentScreen instanceof MerchantScreen tradeScreen))
		{
			openTradeScreen();
			return;
		}
		
		// Can't see experience until the trade screen is open, so we have to
		// check it here and start over if the villager is already experienced.
		int experience = tradeScreen.getScreenHandler().getExperience();
		if(experience > 0)
		{
			ChatUtils.warning("村民在 " + villager.getBlockPos().toShortString()
				+ "已经有经验了，意味着它不能再被训练了。");
			ChatUtils.message("寻找另一个村民...");
			experiencedVillagers.add(villager);
			villager = null;
			jobSite = null;
			closeTradeScreen();
			return;
		}
		
		// check which book the villager is selling
		BookOffer bookOffer =
			findEnchantedBookOffer(tradeScreen.getScreenHandler().getRecipes());
		
		if(bookOffer == null)
		{
			ChatUtils.message("村民没有出售附魔书。");
			closeTradeScreen();
			breakingJobSite = true;
			System.out.println("打破工作场所...");
			return;
		}
		
		ChatUtils.message("村民正在出售 " + bookOffer.getEnchantmentNameWithLevel()
			+ "为 " + bookOffer.getFormattedPrice() + "。");
		
		// if wrong enchantment, break job site and start over
		if(!wantedBooks.isWanted(bookOffer))
		{
			breakingJobSite = true;
			System.out.println("打破工作场所...");
			closeTradeScreen();
			return;
		}
		
		// lock in the trade, if enabled
		if(lockInTrade.isChecked())
		{
			// select the first valid trade
			tradeScreen.getScreenHandler().setRecipeIndex(0);
			tradeScreen.getScreenHandler().switchTo(0);
			MC.getNetworkHandler()
				.sendPacket(new SelectMerchantTradeC2SPacket(0));
			
			// buy whatever the villager is selling
			MC.interactionManager.clickSlot(
				tradeScreen.getScreenHandler().syncId, 2, 0,
				SlotActionType.PICKUP, MC.player);
			
			// close the trade screen
			closeTradeScreen();
		}
		
		// update wanted books based on the user's settings
		updateBooks.getSelected().update(wantedBooks, bookOffer);
		
		ChatUtils.message("完成！");
		setEnabled(false);
	}
	
	private void breakJobSite()
	{
		if(jobSite == null)
			throw new IllegalStateException("工作场所是空的。");
		
		BlockBreakingParams params =
			BlockBreaker.getBlockBreakingParams(jobSite);
		
		if(params == null || BlockUtils.getState(jobSite).isReplaceable())
		{
			System.out.println("工作场所已经被打破。替换...");
			breakingJobSite = false;
			placingJobSite = true;
			return;
		}
		
		// equip tool
		WURST.getHax().autoToolHack.equipBestTool(jobSite, false, true,
			repairMode.getValueI());
		
		// face block
		facing.getSelected().face(params.hitVec());
		
		// damage block and swing hand
		if(MC.interactionManager.updateBlockBreakingProgress(jobSite,
			params.side()))
			swingHand.getSelected().swing(Hand.MAIN_HAND);
		
		// update progress
		overlay.updateProgress();
	}
	
	private void placeJobSite()
	{
		if(jobSite == null)
			throw new IllegalStateException("工作场所是空的。");
		
		if(!BlockUtils.getState(jobSite).isReplaceable())
		{
			if(BlockUtils.getBlock(jobSite) == Blocks.LECTERN)
			{
				System.out.println("工作场所已经被放置。");
				placingJobSite = false;
				
			}else
			{
				System.out.println("在工作场所发现错误的方块。打破...");
				breakingJobSite = true;
				placingJobSite = false;
			}
			
			return;
		}
		
		// check if holding a lectern
		if(!MC.player.isHolding(Items.LECTERN))
		{
			InventoryUtils.selectItem(Items.LECTERN, 36);
			return;
		}
		
		// get the hand that is holding the lectern
		Hand hand = MC.player.getMainHandStack().isOf(Items.LECTERN)
			? Hand.MAIN_HAND : Hand.OFF_HAND;
		
		// sneak-place to avoid activating trapdoors/chests/etc.
		MC.options.sneakKey.setPressed(true);
		if(!MC.player.isSneaking())
			return;
		
		// get block placing params
		BlockPlacingParams params = BlockPlacer.getBlockPlacingParams(jobSite);
		if(params == null)
		{
			((IKeyBinding)MC.options.sneakKey).resetPressedState();
			return;
		}
		
		// face block
		facing.getSelected().face(params.hitVec());
		
		// place block
		ActionResult result = MC.interactionManager.interactBlock(MC.player,
			hand, params.toHitResult());
		
		// swing hand
		if(result.isAccepted() && result.shouldSwingHand())
			swingHand.getSelected().swing(hand);
		
		// reset sneak
		((IKeyBinding)MC.options.sneakKey).resetPressedState();
	}
	
	private void openTradeScreen()
	{
		if(MC.itemUseCooldown > 0)
			return;
		
		ClientPlayerInteractionManager im = MC.interactionManager;
		ClientPlayerEntity player = MC.player;
		
		if(player.squaredDistanceTo(villager) > range.getValueSq())
		{
			ChatUtils.error("村民超出范围。考虑困住" + "村民，这样它就不会走开了。");
			setEnabled(false);
			return;
		}
		
		// create realistic hit result
		Box box = villager.getBoundingBox();
		Vec3d start = RotationUtils.getEyesPos();
		Vec3d end = box.getCenter();
		Vec3d hitVec = box.raycast(start, end).orElse(start);
		EntityHitResult hitResult = new EntityHitResult(villager, hitVec);
		
		// face end vector
		facing.getSelected().face(end);
		
		// click on villager
		Hand hand = Hand.MAIN_HAND;
		ActionResult actionResult =
			im.interactEntityAtLocation(player, villager, hitResult, hand);
		
		if(!actionResult.isAccepted())
			im.interactEntity(player, villager, hand);
		
		// swing hand
		if(actionResult.isAccepted() && actionResult.shouldSwingHand())
			swingHand.getSelected().swing(hand);
		
		// set cooldown
		MC.itemUseCooldown = 4;
	}
	
	private void closeTradeScreen()
	{
		MC.player.closeHandledScreen();
		MC.itemUseCooldown = 4;
	}
	
	private BookOffer findEnchantedBookOffer(TradeOfferList tradeOffers)
	{
		for(TradeOffer tradeOffer : tradeOffers)
		{
			ItemStack stack = tradeOffer.getSellItem();
			if(!(stack.getItem() instanceof EnchantedBookItem book))
				continue;
			
			NbtList enchantmentNbt = EnchantedBookItem.getEnchantmentNbt(stack);
			if(enchantmentNbt.isEmpty())
				continue;
			
			NbtList bookNbt = EnchantedBookItem.getEnchantmentNbt(stack);
			String enchantment = bookNbt.getCompound(0).getString("id");
			int level = bookNbt.getCompound(0).getInt("lvl");
			int price = tradeOffer.getAdjustedFirstBuyItem().getCount();
			BookOffer bookOffer = new BookOffer(enchantment, level, price);
			
			if(!bookOffer.isValid())
			{
				System.out
					.println("发现无效的附魔书报价。\n" + "NBT数据: " + stack.getNbt());
				continue;
			}
			
			return bookOffer;
		}
		
		return null;
	}
	
	private void setTargetVillager()
	{
		ClientPlayerEntity player = MC.player;
		double rangeSq = range.getValueSq();
		
		Stream<VillagerEntity> stream =
			StreamSupport.stream(MC.world.getEntities().spliterator(), true)
				.filter(e -> !e.isRemoved())
				.filter(VillagerEntity.class::isInstance)
				.map(e -> (VillagerEntity)e).filter(e -> e.getHealth() > 0)
				.filter(e -> player.squaredDistanceTo(e) <= rangeSq)
				.filter(e -> e.getVillagerData()
					.getProfession() == VillagerProfession.LIBRARIAN)
				.filter(e -> e.getVillagerData().getLevel() == 1)
				.filter(e -> !experiencedVillagers.contains(e));
		
		villager = stream
			.min(Comparator.comparingDouble(e -> player.squaredDistanceTo(e)))
			.orElse(null);
		
		if(villager == null)
		{
			String errorMsg = "找不到附近的图书管理员。";
			int numExperienced = experiencedVillagers.size();
			if(numExperienced > 0)
				errorMsg += "（除了 " + numExperienced + "那个 "
					+ (numExperienced == 1 ? "是" : "是") + "已经有经验了。）";
			
			ChatUtils.error(errorMsg);
			ChatUtils.message("请确保图书管理员和讲台" + "都在你站立的地方可以到达。");
			setEnabled(false);
			return;
		}
		
		System.out.println("在这里找到村民 " + villager.getBlockPos());
	}
	
	private void setTargetJobSite()
	{
		Vec3d eyesVec = RotationUtils.getEyesPos();
		double rangeSq = range.getValueSq();
		
		Stream<BlockPos> stream = BlockUtils
			.getAllInBoxStream(BlockPos.ofFloored(eyesVec),
				range.getValueCeil())
			.filter(pos -> eyesVec
				.squaredDistanceTo(Vec3d.ofCenter(pos)) <= rangeSq)
			.filter(pos -> BlockUtils.getBlock(pos) == Blocks.LECTERN);
		
		jobSite = stream
			.min(Comparator.comparingDouble(
				pos -> villager.squaredDistanceTo(Vec3d.ofCenter(pos))))
			.orElse(null);
		
		if(jobSite == null)
		{
			ChatUtils.error("找不到图书管理员的讲台。");
			ChatUtils.message("请确保图书管理员和讲台" + "都在你站立的地方可以到达。");
			setEnabled(false);
			return;
		}
		
		System.out.println("在这里找到讲台 " + jobSite);
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
		
		RegionPos region = RenderUtils.getCameraRegion();
		RenderUtils.applyRegionalRenderOffset(matrixStack, region);
		Vec3d regionOffset = region.negate().toVec3d();
		
		RenderSystem.setShaderColor(0, 1, 0, 0.75F);
		
		if(villager != null)
			RenderUtils.drawOutlinedBox(
				villager.getBoundingBox().offset(regionOffset), matrixStack);
		
		if(jobSite != null)
			RenderUtils.drawOutlinedBox(new Box(jobSite).offset(regionOffset),
				matrixStack);
		
		RenderSystem.setShaderColor(1, 0, 0, 0.75F);
		
		for(VillagerEntity villager : experiencedVillagers)
		{
			Box box = villager.getBoundingBox().offset(regionOffset);
			RenderUtils.drawOutlinedBox(box, matrixStack);
			RenderUtils.drawCrossBox(box, matrixStack);
		}
		
		matrixStack.pop();
		
		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		if(breakingJobSite)
			overlay.render(matrixStack, partialTicks, jobSite);
	}
}
