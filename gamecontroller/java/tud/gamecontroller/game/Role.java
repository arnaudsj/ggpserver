package tud.gamecontroller.game;

import cs227b.teamIago.resolver.Expression;

public class Role {
	protected Expression expr;

	public Role(Expression expr) {
		this.expr=expr;
	}

	public String toString(){
		return expr.toString();
	}
}
