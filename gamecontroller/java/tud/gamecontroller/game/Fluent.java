package tud.gamecontroller.game;

import cs227b.teamIago.resolver.Expression;

public class Fluent {
	public Expression expr;
	
	public Fluent(Expression expr){
		this.expr=expr;
	}

	public String toString(){
		return expr.toString();
	}
}
