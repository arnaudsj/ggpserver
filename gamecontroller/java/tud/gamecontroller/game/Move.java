package tud.gamecontroller.game;

import tud.gamecontroller.ExpressionFormatter;
import cs227b.teamIago.resolver.Expression;

public class Move {
	protected Expression expr;
	
	public Move(Expression expression) {
		this.expr=expression;
	}

	public String toString(){
		return expr.toString();
	}

	public String getPrefixForm() {
		 return ExpressionFormatter.prefixForm(expr);
	}
}
