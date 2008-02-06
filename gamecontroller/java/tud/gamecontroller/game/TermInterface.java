package tud.gamecontroller.game;

import java.util.List;

public interface TermInterface {
	public String getName();
	public List<TermInterface> getArgs();
	public boolean isConstant();
	public boolean isVariable();
	public boolean isCompound();
	public boolean isGround();
	String getPrefixForm();
	String getKIFForm();
}
