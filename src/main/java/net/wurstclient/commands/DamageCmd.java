/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.command.CmdError;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.MathUtils;

public final class DamageCmd extends Command
{
	public DamageCmd()
	{
		super("damage", "对你造成指定的伤害。",
			".damage <amount>", "注意: 伤害值是以半颗心为单位的。",
			"示例: .damage 7 (造成3.5颗心的伤害)",
			"要造成更多的伤害，可以多次运行这个命令。");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		
		if(MC.player.getAbilities().creativeMode)
			throw new CmdError("不能在创造模式下受到伤害。");
		
		int amount = parseAmount(args[0]);
		applyDamage(amount);
	}
	
	private int parseAmount(String dmgString) throws CmdSyntaxError
	{
		if(!MathUtils.isInteger(dmgString))
			throw new CmdSyntaxError("Not a number: " + dmgString);
		
		int dmg = Integer.parseInt(dmgString);
		
		if(dmg < 1)
			throw new CmdSyntaxError("Minimum amount is 1.");
		
		if(dmg > 7)
			throw new CmdSyntaxError("Maximum amount is 7.");
		
		return dmg;
	}
	
	private void applyDamage(int amount)
	{
		Vec3d pos = MC.player.getPos();
		
		for(int i = 0; i < 80; i++)
		{
			sendPosition(pos.x, pos.y + amount + 2.1, pos.z, false);
			sendPosition(pos.x, pos.y + 0.05, pos.z, false);
		}
		
		sendPosition(pos.x, pos.y, pos.z, true);
	}
	
	private void sendPosition(double x, double y, double z, boolean onGround)
	{
		MC.player.networkHandler.sendPacket(
			new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
	}
}
