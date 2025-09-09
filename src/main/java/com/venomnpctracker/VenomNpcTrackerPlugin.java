package com.venomnpctracker;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.api.HitsplatID.VENOM;
import java.util.ArrayList;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
@PluginDescriptor(
	name = "Venomed NPC Tracker",
	description = "Tracks NPCs that have been venomed"
)
public class VenomNpcTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private VenomNpcTrackerConfig config;

	private final ArrayList<NPC> venomedNpcs = new ArrayList<>();

	private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION,
		MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_NPC,
		MenuAction.ITEM_USE_ON_NPC);

	private static final Pattern COLOR_TAG_PATTERN = Pattern.compile("<col=([a-zA-Z0-9]+)>");

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ConfigManager configManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		// clear list on shutdown
		venomedNpcs.clear();
		log.info("Example stopped!");
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
			if (hitsplatApplied.getHitsplat().getHitsplatType() == VENOM )
			{
				if (hitsplatApplied.getActor() instanceof NPC)
				{
					if (!venomedNpcs.contains((NPC) hitsplatApplied.getActor()))
					{
					    venomedNpcs.add((NPC) hitsplatApplied.getActor());
					}
				}
			}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		venomedNpcs.remove(event.getNpc());
	}

	@Subscribe(priority = -1.0f)
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int type = event.getType();

		if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET)
		{
			type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
		}

		final MenuAction menuAction = MenuAction.of(type);

		if (NPC_MENU_ACTIONS.contains(menuAction))
		{
			WorldView wv = client.getTopLevelWorldView();

			NPC npc = wv.npcs().byIndex(event.getIdentifier());

			if (venomedNpcs.contains(npc))
			{

				String target = event.getTarget();
				String finalTarget = null;

				// get MenuHpPlugin if enabled
				Plugin menuHpPlugin = pluginManager.getPlugins().stream()
					.filter(plugin -> "Monster Menu HP".equals(plugin.getName()))
					.findFirst()
					.orElse(null);

				// Integrate with Menu HP if it's installed and enabled
				// Only replace the HP colour tags with our configured colour tags
				// If compatibility mode is set to "INTEGRATE"
				if (menuHpPlugin != null && pluginManager.isPluginEnabled(menuHpPlugin) && config.compatibilityMode().name().equals("INTEGRATE"))
				{
					log.info("Integrating with Menu HP");
					Color menuHpHpColor = (Color) configManager.getConfiguration("menuhp", "hpColor", Color.class);

					String menuHpHpColorTag = ColorUtil.colorTag(menuHpHpColor);
					String hpColorTag = ColorUtil.colorTag(config.hpColor());

					// Replace only colours of segments wrapped with the menuHpHpColorTag
					finalTarget = target.replaceAll(
						Pattern.quote(menuHpHpColorTag) + "(.*?)</col>",
						hpColorTag + "$1</col>"
					);
				}
				// Otherwise, completely override the target with our own formatting
				else if (!config.displayMode().name().equals("ICONONLY"))
				{
					String cleanTarget = Text.removeTags(event.getTarget());
					Color[] tagColors = getColorsFromTags(target);
					int levelStartIndex = cleanTarget.lastIndexOf('(');
					int nameStartIndex = 0;
					// If there's an arrow, start of name is after the arrow
					int arrowIndex = cleanTarget.lastIndexOf("->");
					String preArrowText = "";
					String arrowText = "";
					if (arrowIndex != -1)
					{
						preArrowText = cleanTarget.substring(0, arrowIndex);
						preArrowText = ColorUtil.wrapWithColorTag(preArrowText, tagColors.length > 0 ? tagColors[0] : Color.WHITE);
						arrowText = "->";
						arrowText = ColorUtil.wrapWithColorTag(arrowText, tagColors.length > 0 ? tagColors[1] : Color.WHITE);
						nameStartIndex = arrowIndex + 2;
					}
					String nameText = levelStartIndex != -1 ? cleanTarget.substring(nameStartIndex, levelStartIndex) : cleanTarget;
					String levelText = levelStartIndex != -1 ? cleanTarget.substring(levelStartIndex) : "";

					log.info("Colors in tag: {}", (Object) tagColors);

					switch (config.displayMode()) {
						case LEVEL:
							levelText = ColorUtil.wrapWithColorTag(levelText, config.hpColor());
							if (arrowIndex != -1)
							{
								nameText = ColorUtil.wrapWithColorTag(nameText, tagColors.length > 2 ? tagColors[2] : Color.WHITE);
							}
							else
							{
								nameText = ColorUtil.wrapWithColorTag(nameText, tagColors.length > 0 ? tagColors[0] : Color.WHITE);
							}
							break;
						case NAME:
							nameText = ColorUtil.wrapWithColorTag(nameText, config.hpColor());
							if (arrowIndex != -1)
							{
								levelText = ColorUtil.wrapWithColorTag(levelText, tagColors.length > 3 ? tagColors[3] : Color.WHITE);
							}
							else
							{
								levelText = ColorUtil.wrapWithColorTag(levelText, tagColors.length > 1 ? tagColors[1] : Color.WHITE);
							}
							break;
						default:
							nameText = ColorUtil.wrapWithColorTag(nameText, config.hpColor());
							levelText = ColorUtil.wrapWithColorTag(levelText, config.hpColor());
							break;
					}
					// Construct final string
					finalTarget = preArrowText + arrowText + nameText + levelText;

				}

				if (config.showVenomSuffix())
				{
					// venom icon unicode
					String venomIcon = ColorUtil.wrapWithColorTag("V", config.hpColor());
					finalTarget = finalTarget + " - " + venomIcon;
				}

				MenuEntry[] menuEntries = client.getMenuEntries();
				final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
				menuEntry.setTarget(finalTarget);
				client.setMenuEntries(menuEntries);
			}
		}
	}

	private Color[] getColorsFromTags(String text)
	{
		Color[] result = new Color[]{};
		Matcher matcher = COLOR_TAG_PATTERN.matcher(text);
		while (matcher.find())
		{
			result = ArrayUtils.add(result, Color.decode('#' + matcher.group(1)));
		}
		return result;
	}

	@Provides
	VenomNpcTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VenomNpcTrackerConfig.class);
	}
}
