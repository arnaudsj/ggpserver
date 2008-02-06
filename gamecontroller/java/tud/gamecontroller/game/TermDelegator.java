package tud.gamecontroller.game;

import java.util.List;

public abstract class TermDelegator<T extends TermInterface> implements TermInterface {
	private T term;

	public TermDelegator(T term) {
		this.term=term;
	}
	
	public T getTerm(){
		return term;
	}

	public List<TermInterface> getArgs() {
		return term.getArgs();
	}

	public String getKIFForm() {
		return term.getKIFForm();
	}

	public String getName() {
		return term.getName();
	}

	public String getPrefixForm() {
		return term.getPrefixForm();
	}

	public boolean isCompound() {
		return term.isCompound();
	}

	public boolean isConstant() {
		return term.isConstant();
	}

	public boolean isVariable() {
		return term.isVariable();
	}
	
	public boolean isGround() {
		return term.isGround();
	}
	
}
