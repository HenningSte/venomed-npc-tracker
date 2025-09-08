package com.venomnpctracker;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface VenomNpcTrackerConfig extends Config
{
	@ConfigItem(
		keyName = "hpColor",
		name = "HP color",
		description = "The HP color for the monster's menu HP bar",
		position = 1
	)
	default Color hpColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "showVenomSuffix",
		name = "Show Venom Suffix",
		description = "Configures whether to show a venom suffix next to venomed NPCs",
		position = 2
	)
	default boolean showVenomSuffix()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayMode",
		name = "Display Mode",
		description = "Configures how to display venomed NPCs in the menu",
		position = 3
	)
	default DisplayMode displayMode()
	{
		return DisplayMode.LEVEL;
	}

	@ConfigItem(
		keyName = "compatibilityMode",
		name = "Compatibility Behaviour",
		description = "Configures how to behave when Monster Menu HP is also installed",
		position = 4
	)
	default CompatibilityMode compatibilityMode()
	{
		return CompatibilityMode.INTEGRATE;
	}
}
