/*
 * Created on May 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cs227b.teamIago.resolver.ExpList;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameState implements Serializable{
	HashMap objState;
	HashMap objProven;
	HashSet objDisproven;
	
	public GameState(HashMap objState) {
		this.objState = objState;
		this.objProven = null;
		this.objDisproven = null;
	}
	
	public GameState(HashMap objState, 
			HashMap provenTrans, HashSet disprovenTrans) {
		this.objState = objState;
		this.objProven = provenTrans;
		this.objDisproven = disprovenTrans;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		Collection myLists = this.objState.values();
		Iterator myIt = myLists.iterator();
		long curHash = 0;

		for (; myIt.hasNext();)
		{
			ExpList myList = (ExpList) myIt.next();
			curHash += myList.hashCode();
		}
//		System.err.print("Hash: " + curHash + "   ");
		return (int)curHash;
		
	}
	public HashMap getMap() {
		return objState;
	}
	
	public HashMap getProven() {
		return objProven;
	}

	public HashSet getDisproven() {
		return objDisproven;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "< GameState: " + objState.toString() + " >";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		boolean old = false;
		if (arg0 == null) return false;
		
		if (old) {
			GameState hisObj = (GameState) arg0;
			boolean hashes = (hisObj.hashCode() == hashCode());
//			System.err.println("Hash codes equal? " + hashes);
			boolean ret = this.objState.equals(hisObj.objState);
//			System.err.println("Objects equal? " + ret);
			return ret;
		}
		else {
			GameState hisObj = (GameState) arg0;
			int hisHash = hisObj.hashCode();
			int myHash = hashCode();
			boolean hashes = (hisHash == myHash);
//			System.err.println("Hash codes equal? " + hashes);

			boolean ret = (this.objState.size() == hisObj.objState.size());
			// short-circuit to skip ContainsAll if sizes unequal
			if (ret == false) {
//				System.err.println("Objects equal? " + ret);
				return ret;
			}

			Collection hisLists = hisObj.objState.values();
			Collection myLists = this.objState.values();
			Iterator myIt = myLists.iterator();
			Iterator hisIt = hisLists.iterator();

			for (; myIt.hasNext();)
			{
				ExpList myList = (ExpList) myIt.next();
				ExpList hisList = (ExpList) hisIt.next();
				if (hisList.size() != myList.size()) {
					ret = false;
					break;
				}
				if (!hisList.containsAll(myList)) {
					ret = false;
					break;
				}
			}
//			System.err.println("Objects equal? " + ret);
			return ret;
		}
	}
}
