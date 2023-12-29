/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.block.FluidBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.events.AirStrafingSpeedListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.BlockUtils;

public final class GlideHack extends Hack
	implements UpdateListener, AirStrafingSpeedListener
{
	private final SliderSetting fallSpeed = new SliderSetting("下降速度", 0.125,
		0.005, 0.25, 0.005, ValueDisplay.DECIMAL);
	
	private final SliderSetting moveSpeed = new SliderSetting("移动速度", "水平移动因子。",
		1.2, 1, 5, 0.05, ValueDisplay.PERCENTAGE);
	
	private final SliderSetting minHeight =
		new SliderSetting("最低高度", "当你离地面太近时不会滑翔。", 0, 0, 2, 0.01,
			ValueDisplay.DECIMAL.withLabel(0, "禁用"));
	
	public GlideHack()
	{
		super("缓降");
		
		setCategory(Category.MOVEMENT);
		addSetting(fallSpeed);
		addSetting(moveSpeed);
		addSetting(minHeight);
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		Vec3d v = player.getVelocity();
		
		if(player.isOnGround() || player.isTouchingWater() || player.isInLava()
			|| player.isClimbing() || v.y >= 0)
			return;
		
		if(minHeight.getValue() > 0)
		{
			Box box = player.getBoundingBox();
			box = box.union(box.offset(0, -minHeight.getValue(), 0));
			if(!MC.world.isSpaceEmpty(box))
				return;
			
			BlockPos min = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
			BlockPos max = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);
			Stream<BlockPos> stream = StreamSupport
				.stream(BlockUtils.getAllInBox(min, max).spliterator(), true);
			
			// manual collision check, since liquids don't have bounding boxes
			if(stream.map(BlockUtils::getBlock)
				.anyMatch(FluidBlock.class::isInstance))
				return;
		}
		
		player.setVelocity(v.x, Math.max(v.y, -fallSpeed.getValue()), v.z);
	}
	
	@Override
	public void onGetAirStrafingSpeed(AirStrafingSpeedEvent event)
	{
		event.setSpeed(event.getDefaultSpeed() * moveSpeed.getValueF());
	}
}
