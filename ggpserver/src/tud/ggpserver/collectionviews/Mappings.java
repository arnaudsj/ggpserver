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

package tud.ggpserver.collectionviews;

public class Mappings {
	@SuppressWarnings("unchecked")
	private static Mapping identityMapping = new Mapping(){
		public Object map(Object o) {
			return o;
		}
		public Object reverseMap(Object o) {
			return o;
		}
	};

	@SuppressWarnings("unchecked")
	public static <T> Mapping<T, T> identity() {
		return identityMapping;
	}
}
