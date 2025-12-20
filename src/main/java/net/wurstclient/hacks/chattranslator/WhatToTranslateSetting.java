/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.chattranslator;

import net.wurstclient.settings.EnumSetting;

public final class WhatToTranslateSetting
	extends EnumSetting<WhatToTranslateSetting.WhatToTranslate>
{
	public WhatToTranslateSetting()
	{
		super("翻译", "", WhatToTranslate.values(),
			WhatToTranslate.RECEIVED_MESSAGES);
	}
	
	public boolean includesReceived()
	{
		return getSelected().received;
	}
	
	public boolean includesSent()
	{
		return getSelected().sent;
	}
	
	public enum WhatToTranslate
	{
		RECEIVED_MESSAGES("接收到的消息", true, false),
		SENT_MESSAGES("发送的消息", false, true),
		BOTH("两者", true, true);
		
		private final String name;
		private final boolean received;
		private final boolean sent;
		
		private WhatToTranslate(String name, boolean received, boolean sent)
		{
			this.name = name;
			this.received = received;
			this.sent = sent;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
