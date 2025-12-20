/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.minecraft.util.Util;
import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.WurstClient;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.update.Version;

@SearchTags({"change log", "wurst update", "release notes", "what's new",
	"what is new", "new features", "recently added features"})
@DontBlock
public final class ChangelogOtf extends OtherFeature
{
	public ChangelogOtf()
	{
		super("更新日志", "在浏览器中打开更新日志。");
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "查看更新日志";
	}
	
	@Override
	public void doPrimaryAction()
	{
		String link = new Version(WurstClient.VERSION).getChangelogLink()
			+ "?utm_source=Wurst+Client&utm_medium=ChangelogOtf&utm_content=View+Changelog";
		Util.getPlatform().openUri(link);
	}
}
