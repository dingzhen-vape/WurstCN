/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.test;

import static net.wurstclient.test.WurstClientTestHelper.*;

import java.time.Duration;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;

public final class WurstE2ETestClient implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		if(System.getProperty("wurst.e2eTest") == null)
			return;
		
		Thread.ofVirtual().name("Wurst端到端测试")
			.uncaughtExceptionHandler((t, e) -> {
				e.printStackTrace();
				System.exit(1);
			}).start(this::runTests);
	}
	
	private void runTests()
	{
		System.out.println("开始Wurst端到端测试");
		waitForResourceLoading();
		
		if(submitAndGet(mc -> mc.options.onboardAccessibility))
		{
			System.out.println("新手引导已启用。等待其");
			waitForScreen(AccessibilityOnboardingScreen.class);
			System.out.println("到达新手引导屏幕");
			clickButton("gui.continue");
		}
		
		waitForScreen(TitleScreen.class);
		waitForTitleScreenFade();
		System.out.println("到达标题屏幕");
		takeScreenshot("title_screen", Duration.ZERO);
		
		submitAndWait(AltManagerTest::testAltManagerButton);
		// TODO: Test more of AltManager
		
		System.out.println("点击单人游戏按钮");
		clickButton("menu.singleplayer");
		
		if(submitAndGet(mc -> !mc.getLevelStorage().getLevelList().isEmpty()))
		{
			System.out.println("世界列表不为空。等待其");
			waitForScreen(SelectWorldScreen.class);
			System.out.println("到达选择世界屏幕");
			takeScreenshot("select_world_screen");
			clickButton("selectWorld.create");
		}
		
		waitForScreen(CreateWorldScreen.class);
		System.out.println("到达创建世界屏幕");
		
		// Set MC version as world name
		setTextFieldText(0,
			"E2E 测试 " + SharedConstants.getGameVersion().getName());
		// Select creative mode
		clickButton("selectWorld.gameMode");
		clickButton("selectWorld.gameMode");
		takeScreenshot("create_world_screen");
		
		System.out.println("创建测试世界");
		clickButton("selectWorld.create");
		
		waitForWorldLoad();
		dismissTutorialToasts();
		waitForWorldTicks(200);
		runChatCommand("seed");
		System.out.println("到达单人游戏世界");
		takeScreenshot("in_game", Duration.ZERO);
		clearChat();
		
		System.out.println("打开调试菜单");
		toggleDebugHud();
		takeScreenshot("debug_menu");
		
		System.out.println("关闭调试菜单");
		toggleDebugHud();
		
		System.out.println("检查损坏的混入");
		MixinEnvironment.getCurrentEnvironment().audit();
		
		System.out.println("打开物品栏");
		openInventory();
		takeScreenshot("inventory");
		
		System.out.println("关闭物品栏");
		closeScreen();
		
		// TODO: Open ClickGUI and Navigator
		
		// Build a test platform and clear out the space above it
		runChatCommand("fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 stone");
		runChatCommand("fill ~-5 ~ ~-5 ~5 ~30 ~5 air");
		runChatCommand("clear");
		
		// Clear inventory and chat before running tests
		runChatCommand("clear");
		clearChat();
		
		// Test Wurst hacks
		AutoMineHackTest.testAutoMineHack();
		FreecamHackTest.testFreecamHack();
		NoFallHackTest.testNoFallHack();
		XRayHackTest.testXRayHack();
		
		// Test Wurst commands
		CopyItemCmdTest.testCopyItemCmd();
		GiveCmdTest.testGiveCmd();
		ModifyCmdTest.testModifyCmd();
		
		// TODO: Test more Wurst features
		
		// Test special cases
		PistonTest.testPistonDoesntCrash();
		
		System.out.println("返回标题屏幕");
		openGameMenu();
		takeScreenshot("返回标题屏幕");
		
		// TODO: Check Wurst Options
		
		System.out.println("停止游戏");
		clickButton("停止游戏");
		waitForScreen(TitleScreen.class);
		
		System.out.println("menu.quit");
		clickButton("menu.quit");
	}
}
