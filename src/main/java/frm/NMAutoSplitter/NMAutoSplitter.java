package frm.NMAutoSplitter;

import static net.runelite.api.ChatMessageType.GAMEMESSAGE;


import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.io.PrintWriter;

@Slf4j
@PluginDescriptor(
	name = "NM Auto Splitter"
)
public class NMAutoSplitter extends Plugin {
	@Inject
	private Client client;

	@Inject
	private NMAutoSplitterConfig config;

	@Inject
	private ClientToolbar clientToolbar;
	// side panel
	private NavigationButton navButton;
	private NMAutoSplitterPanel panel;
	// LiveSplit server
	PrintWriter writer;
	private static final int NM_ROOM_MASK = 0b11_1111111000_11111111000_000;
	private static final int NM_ROOM = 3 << 24 | 120 << 16 | 310 << 5;

	private static final int NIGHTMARE_P1 = NpcID.THE_NIGHTMARE_9425;
	private static final int NIGHTMARE_P2 = NpcID.THE_NIGHTMARE_9426;
	private static final int NIGHTMARE_P3 = NpcID.THE_NIGHTMARE_9427;

	private static final int NIGHTMARE_P1_PILLARS = NpcID.THE_NIGHTMARE_9428;
	private static final int NIGHTMARE_P2_PILLARS = NpcID.THE_NIGHTMARE_9429;
	private static final int NIGHTMARE_P3_PILLARS = NpcID.THE_NIGHTMARE_9430;
	private static final int NIGHTMARE_SLEEPWALKERS = NpcID.THE_NIGHTMARE_9431;
	private static final int NIGHTMARE_DOWN = NpcID.THE_NIGHTMARE_9432;
	private static final int NIGHTMARE_DEATH = NpcID.THE_NIGHTMARE_9433;
	private static final int PHOSANI_P1 = NpcID.PHOSANIS_NIGHTMARE_9416;
	private static final int PHOSANI_P2 = NpcID.PHOSANIS_NIGHTMARE_9417;
	private static final int PHOSANI_P3 = NpcID.PHOSANIS_NIGHTMARE_9418;
	private static final int PHOSANI_P4 = NpcID.PHOSANIS_NIGHTMARE_11153;
	private static final int PHOSANI_P5 = NpcID.PHOSANIS_NIGHTMARE_11154;

	private static final int PHOSANI_P1_PILLARS = NpcID.PHOSANIS_NIGHTMARE_9419;
	private static final int PHOSANI_P2_PILLARS = NpcID.PHOSANIS_NIGHTMARE_9420;
	private static final int PHOSANI_P3_PILLARS = NpcID.PHOSANIS_NIGHTMARE_9421;
	private static final int PHOSANI_P4_PILLARS = NpcID.PHOSANIS_NIGHTMARE_11155;
	private static final int PHOSANI_SLEEPWALKERS = NpcID.PHOSANIS_NIGHTMARE_9422;
	private static final int PHOSANI_DOWN = NpcID.PHOSANIS_NIGHTMARE_9423;
	private static final int PHOSANI_DEATH = NpcID.PHOSANIS_NIGHTMARE_9424;

	private NPC nm;
	private int phase;
	boolean dirty;
	int fight_timer = -1, phase_timer = -1, subph_timer = -1;
	int phase_splits[] = new int[6];

	@Provides
	NMAutoSplitterConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(NMAutoSplitterConfig.class);
	}
//TODO sidebar controller dissapears if sky's cox autosplitter or nmas is turned off
// want to keep controller in sidebar unless both are turned off
	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(navButton);
		panel.disconnect();  // terminates active socket
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned e) {
		if (!is_in_noa()) return;
		NPC npc = e.getNpc();
		if (get_noa_npc_type(npc) == -1) return;
		nm = npc;
		fight_timer = phase_timer = subph_timer = client.getTickCount();
		Arrays.fill(phase_splits, -1);
		dirty = true;
		phase = -1;
	}
	@Subscribe
	public void onNpcChanged(NpcChanged e) {
		if (e.getNpc() != nm) return;

		onNightmareChanged(e.getOld().getId());
		send_split();
	}

	private void onNightmareChanged(int oldid) {
		int tick_count = client.getTickCount();
		// full boss: 9432->9425->9428->9431->9426->9429->9431->9427->9430->9433
		switch (nm.getId()) {
			case NIGHTMARE_P1:
			case NIGHTMARE_P2:
			case NIGHTMARE_P3:
			case PHOSANI_P1:
			case PHOSANI_P2:
			case PHOSANI_P3:
			case PHOSANI_P4:
			case PHOSANI_P5:
				if (oldid == NIGHTMARE_DOWN || oldid == PHOSANI_DOWN) {
					Arrays.fill(phase_splits, -1);
					phase = 1;
					fight_timer = tick_count;
					dirty = false;
				} else {
					phase++;
				}
				// reset phase and subphase timers
				phase_timer = tick_count;
				subph_timer = tick_count;
				break;
			case PHOSANI_DEATH:
				phase_splits[5] = tick_count - phase_timer;
				phase_splits[0] = tick_count - fight_timer;
			case NIGHTMARE_P1_PILLARS:
			case NIGHTMARE_P2_PILLARS:
			case NIGHTMARE_P3_PILLARS:
			case PHOSANI_P1_PILLARS:
			case PHOSANI_P2_PILLARS:
			case PHOSANI_P3_PILLARS:
			case PHOSANI_P4_PILLARS:
				subph_timer = tick_count;
				break;
		}
	}

	private int get_noa_npc_type(NPC npc) {
		if (npc == null) return -1;

		switch (npc.getId()) {
			case NIGHTMARE_P1:
			case NIGHTMARE_P2:
			case NIGHTMARE_P3:
			case NIGHTMARE_P1_PILLARS:
			case NIGHTMARE_P2_PILLARS:
			case NIGHTMARE_P3_PILLARS:
			case NIGHTMARE_SLEEPWALKERS:
			case NIGHTMARE_DOWN:
			case NIGHTMARE_DEATH:
				return 0;
			case PHOSANI_P1:
			case PHOSANI_P2:
			case PHOSANI_P3:
			case PHOSANI_P4:
			case PHOSANI_P5:
			case PHOSANI_P1_PILLARS:
			case PHOSANI_P2_PILLARS:
			case PHOSANI_P3_PILLARS:
			case PHOSANI_P4_PILLARS:
			case PHOSANI_DOWN:
			case PHOSANI_SLEEPWALKERS:
			case PHOSANI_DEATH:
				return 1;
		}

		return -1;
	}



	private boolean is_in_noa() {
		WorldPoint wp = client.getLocalPlayer().getWorldLocation();
		int x = wp.getX() - client.getBaseX();
		int y = wp.getY() - client.getBaseY();
		int template = client.getInstanceTemplateChunks()[client.getPlane()][x / 8][y / 8];
		return (template & NM_ROOM_MASK) == NM_ROOM;
	}
//TODO autoreset function, when you click the pool or wake her up again it resets your splits
	private void send_split() {
		try {
			writer.write("startorsplit\r\n");
			writer.flush();
		} catch (Exception ignored) { }
	}
	@Override
	protected void startUp() {
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");
		panel = new NMAutoSplitterPanel(client, writer, config, this);
		navButton = NavigationButton.builder().tooltip("LiveSplit controller")
				.icon(icon).priority(6).panel(panel).build();
		clientToolbar.addNavigation(navButton);

		panel.startPanel();
	}

}