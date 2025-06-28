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

@SearchTags({"清理"})
@DontBlock
public final class CleanUpOtf extends OtherFeature
{
	public CleanUpOtf()
	{
		super("清理", "清理你的服务器列表。\n"
			+ "To use it, press the 'Clean Up' button on the server selection screen.");
	}
}
