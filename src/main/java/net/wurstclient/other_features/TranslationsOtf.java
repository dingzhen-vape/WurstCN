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
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"languages", "localizations", "localisations",
	"internationalization", "internationalisation", "i18n", "sprachen",
	"übersetzungen", "force english"})
@DontBlock
public final class TranslationsOtf extends OtherFeature
{
	private final CheckboxSetting forceEnglish = new CheckboxSetting(
		"强制英语",
		"即使Minecraft设置为其他语言，也以英语显示Wurst客户端。",
		true);
	
	public TranslationsOtf()
	{
		super("翻译", "本地化设置。\n\n"
			+ "\u00a7c这是一个实验性的功能！\u00a7r\n"
			+ "我们还没有很多翻译。如果你会说英语和其他语言，请帮助我们添加更多的翻译。");
		addSetting(forceEnglish);
	}
	
	public CheckboxSetting getForceEnglish()
	{
		return forceEnglish;
	}
}
