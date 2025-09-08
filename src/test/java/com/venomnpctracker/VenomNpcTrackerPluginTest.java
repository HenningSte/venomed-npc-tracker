package com.venomnpctracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VenomNpcTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VenomNpcTrackerPlugin.class);
		RuneLite.main(args);
	}
}