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

@DontBlock
@SearchTags({"privacy", "data", "tracking", "snooper", "spyware"})
public final class NoTelemetryOtf extends OtherFeature
{
	private final CheckboxSetting disableTelemetry =
		new CheckboxSetting("禁用遥测", true);
	
	public NoTelemetryOtf()
	{
		super("禁用遥测",
			"禁用required telemetry that Mojang introduced in 22w46a. Turns out it's not so required after all.");
		addSetting(disableTelemetry);
	}
	
	@Override
	public boolean isEnabled()
	{
		return disableTelemetry.isChecked();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return isEnabled() ? "重新启用遥测" : "禁用遥测";
	}
	
	@Override
	public void doPrimaryAction()
	{
		disableTelemetry.setChecked(!disableTelemetry.isChecked());
	}
	
	// See TelemetrySenderMixin
}
