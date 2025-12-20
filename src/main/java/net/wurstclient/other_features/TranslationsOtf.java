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
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"languages", "localizations", "localisations",
	"internationalization", "internationalisation", "i18n", "sprachen",
	"übersetzungen", "force english"})
@DontBlock
public final class TranslationsOtf extends OtherFeature
{
	private final CheckboxSetting forceEnglish = new CheckboxSetting(
		"Force English",
		"即使Minecraft设置为其他语言，也会以英文显示Wurst客户端。",
		true);
	
	public TranslationsOtf()
	{
		super("翻译", "允许Wurst中的文本显示"
			+ "其他语言，除了英语。它将使用与Minecraft相同的语言"
			+ "Minecraft设置的语言。\n\n"
			+ "这是一个实验性功能！");
		addSetting(forceEnglish);
	}
	
	public CheckboxSetting getForceEnglish()
	{
		return forceEnglish;
	}
}
