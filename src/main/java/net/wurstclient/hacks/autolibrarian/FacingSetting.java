/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autolibrarian;

import java.util.function.Consumer;

import net.minecraft.util.math.Vec3d;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.EnumSetting;

public final class FacingSetting extends EnumSetting<FacingSetting.Facing>
{
	protected static final WurstClient WURST = WurstClient.INSTANCE;
	
	public FacingSetting()
	{
super("Facing", "如何面向村民和工作站。\n\n"
			+ "\u00a7lOff\u00a7r - 不要面向村民。会被反作弊插件检测到。\n\n"
			+ "\u00a7lServer-side\u00a7r - 在服务器端面向村民，同时在客户端自由移动摄像机。\n\n"
			+ "\u00a7lClient-side\u00a7r - 通过在客户端移动摄像机来面向村民。这是最合法的选项，但是看起来可能会让人头晕。",
			Facing.values(),
			Facing.SERVER);
	}
	
	public enum Facing
	{
		OFF("Off", v -> {}),
		
		SERVER("Server-side",
			v -> WURST.getRotationFaker().faceVectorPacket(v)),
		
		CLIENT("Client-side",
			v -> WURST.getRotationFaker().faceVectorClient(v));
		
		private String name;
		private Consumer<Vec3d> face;
		
		private Facing(String name, Consumer<Vec3d> face)
		{
			this.name = name;
			this.face = face;
		}
		
		public void face(Vec3d v)
		{
			face.accept(v);
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
