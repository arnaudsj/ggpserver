package tud.gamecontroller.term;

public interface GameObjectInterface {
	/**
	 * @return the prefix form of this term
	 * e.g., "f(a,g(1,2))" 
	 */
	String getPrefixForm();
	
	/**
	 * @return the infix KIF form of this term
	 * e.g., "(f a (g 1 2))" 
	 */
	String getKIFForm();
}
