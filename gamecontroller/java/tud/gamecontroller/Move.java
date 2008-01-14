package tud.gamecontroller;

import cs227b.teamIago.resolver.Expression;

public class Move {
	Expression expr;
	
	public Move(Expression expr) {
		this.expr=expr;
	}

	public String toString(){
		return expr.toString();
	}
}
