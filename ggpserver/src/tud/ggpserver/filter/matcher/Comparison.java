/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de> 

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

package tud.ggpserver.filter.matcher;

public enum Comparison {
	Equal("="), NotEqual("!="), Greater(">"), GreaterEqual(">="), Smaller("<"), SmallerEqual("<=");
	private String name;
	private Comparison(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
	/**
	 * must return a valid SQL comparison operator
	 * @return
	 */
	public String getSQLOperator() {
		return name; // at the moment all names happen to be valid comparison operators
	}
}