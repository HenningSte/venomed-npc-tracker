package com.venomnpctracker;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("venomnpctracker")
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
		return new Color(0, 99, 67);
	}

	@ConfigItem(
		keyName = "trackPoisonedNpcs",
		name = "Track Poisoned NPCs",
		description = "Configures whether to track poisoned NPCs as well as venomed NPCs",
		position = 2
	)
	default boolean trackPoisonedNpcs()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showVenomSuffix",
		name = "Show Suffix",
		description = "Configures whether to show a -V suffix in the menu of venomed NPCs",
		position = 3
	)
	default boolean showVenomSuffix()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayMode",
		name = "Display Mode",
		description = "Configures how to display venomed NPCs in the menu",
		position = 4
	)
	default DisplayMode displayMode()
	{
		return DisplayMode.BOTH;
	}

	@ConfigItem(
		keyName = "compatibilityMode",
		name = "Compatibility Behaviour",
		description = "Configures how to behave when Monster Menu HP is also installed",
		position = 5
	)
	default CompatibilityMode compatibilityMode()
	{
		return CompatibilityMode.INTEGRATE;
	}
}
