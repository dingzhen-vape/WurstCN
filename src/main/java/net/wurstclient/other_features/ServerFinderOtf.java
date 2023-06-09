/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;

@SearchTags({"Server Finder"})
@DontBlock
public final class ServerFinderOtf extends OtherFeature
{
	public ServerFinderOtf()
	{
		super("ServerFinder",
			"让你能够快速简单地找到容易被破坏的Minecraft服务器。要使用它，按下服务器选择界面上的'Server Finder'按钮。");
	}
}
