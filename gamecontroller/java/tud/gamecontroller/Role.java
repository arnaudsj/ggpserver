package tud.gamecontroller;

import cs227b.teamIago.resolver.Expression;

public class Role {
	Expression expr;

	public Role(Expression expr) {
		this.expr=expr;
	}

	public String toString(){
		return expr.toString();
	}
}
