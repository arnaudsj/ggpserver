package tud.ggpserver.util;

import java.util.List;
import java.util.LinkedList;

import tud.gamecontroller.GDLVersion;
import tud.ggpserver.scheduler.SchedulerVersion;

import tud.gamecontroller.players.PlayerInfo;

public class Utilities {
	
	
	public static GDLVersion[] gdlVersions () {
		return new GDLVersion[] {GDLVersion.v1, GDLVersion.v2};
	}
	
	public static int gdlVersion (GDLVersion gdlVersion) {
		if (gdlVersion == GDLVersion.v1) {
			return 1;
		} else {
			return 2;
		}
	}
	
	public static GDLVersion gdlVersion (int gdlVersion) {
		if (gdlVersion == 1) {
			return GDLVersion.v1;
		} else {
			return GDLVersion.v2;
		}
	}
	
	public static SchedulerVersion[] schedulerVersions () {
		return new SchedulerVersion[] {SchedulerVersion.Original, SchedulerVersion.GDLII_Compatible};
	}
	
	public static List<GDLVersion> allowedGdlVersions (SchedulerVersion schedulerVersion) {
		List<GDLVersion> list = new LinkedList<GDLVersion>();
		if (schedulerVersion == SchedulerVersion.Original) { // original scheduler, players understand only GDLv1, os only GDLv1 plays should be available
			list.add(GDLVersion.v1);
		} else { // GDLII-compatible scheduler, players must understand GDL-II, so that both GDLv1 and GDLv2 games can be played  
			list.add(GDLVersion.v1);
			list.add(GDLVersion.v2);
		}
		return list;
	}
	
	public static boolean areCompatible (SchedulerVersion schedulerVersion, GDLVersion gdlVersion) {
		return allowedGdlVersions(schedulerVersion).contains(gdlVersion);
	}
	
	public static boolean areCompatible (SchedulerVersion schedulerVersion, PlayerInfo playerInfo) {
		if (schedulerVersion == SchedulerVersion.Original) { // original scheduler, runs GDLv1 games, for any enabled player
			return true;
		} else { // GDLII-compatible scheduler, only for GDLII-compatible players
			return playerInfo.getGdlVersion() == GDLVersion.v2;
		}
	}
	
	public static boolean areCompatible (PlayerInfo playerInfo, GDLVersion gdlVersion) {
		if (playerInfo.getGdlVersion() == GDLVersion.v1) {
			return gdlVersion == GDLVersion.v1;
		} else { // a GDLII-compatible player can play any game
			return true;
		}
	}
	
}
