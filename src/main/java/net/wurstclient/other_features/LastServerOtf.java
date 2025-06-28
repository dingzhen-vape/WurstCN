/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;

@SearchTags({"last server"})
@DontBlock
public final class LastServerOtf extends OtherFeature
{
	public LastServerOtf()
	{
		super("上次服务器", "Wurst添加了一个Last Server按钮到服务器选择屏幕，它可以自动将你带回上次游玩的服务器。\n\n"
			+ "当你被踢出或者有很多服务器时很有用。");
	}
}
