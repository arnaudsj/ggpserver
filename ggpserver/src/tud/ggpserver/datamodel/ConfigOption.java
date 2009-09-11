/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.datamodel;

public enum ConfigOption {
	
	NEXT_PLAYED_GAME ("next_played_game", null),
	PLAY_CLOCK_MIN ("playclock_min", "5"),
	PLAY_CLOCK_MAX ("playclock_max", "60"),
	START_CLOCK_MIN ("startclock_min", "30"),
	START_CLOCK_MAX ("startclock_max", "600");

    private final String dBKey;
    private final String defaultValue;

    private ConfigOption(String dBKey, String defaultValue) {
        this.dBKey = dBKey;
        this.defaultValue = defaultValue;
    }
    public String getDBKey() { return dBKey; }
    public String getDefaultValue() { return defaultValue; }
}
