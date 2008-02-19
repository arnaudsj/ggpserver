package tud.gamecontroller.game;

import java.util.List;

public interface TermInterface {
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
	public List<TermInterface> getArgs();

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
