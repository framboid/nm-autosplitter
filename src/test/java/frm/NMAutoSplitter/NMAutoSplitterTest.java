package frm.NMAutoSplitter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NMAutoSplitterTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NMAutoSplitter.class);
		RuneLite.main(args);
	}
}