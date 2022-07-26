package frm.NMAutoSplitter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("autosplitter")
public interface NMAutoSplitterConfig extends Config {

	@ConfigItem(position = 0, keyName = "port", name = "Port", description = "Port for the LiveSplit server. (Restart required)")
	default int port() {
		return 16834;
	}

}