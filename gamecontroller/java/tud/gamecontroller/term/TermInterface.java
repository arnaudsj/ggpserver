/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.term;

import java.util.List;

public interface TermInterface extends GameObjectInterface {
	/**
	 * If the term is a function this must return the function symbol,
	 * if it is a constant then just the constant and if it is a variable then
	 * it should return the variable name preceded by a question mark 
	 * @return the name of the term
	 */
	public String getName();

	/**
	 * @return the list of arguments if the term is a function, null otherwise
	 */
	public List<? extends TermInterface> getArgs();

	/**
	 * @return true if this Term is a constant (i.e., neither function nor variable) 
	 */
	public boolean isConstant();

	/**
	 * @return true if this Term is a variable 
	 */
	public boolean isVariable();

	/**
	 * @return true if this Term is a compound term (i.e., a function) 
	 */
	public boolean isCompound();

	/**
	 * @return true if this Term doesn't contain any variables 
	 */
	public boolean isGround();

}
