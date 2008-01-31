package tud.gamecontroller;

import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Term;

public class ExpressionFormatter {

	protected ExpressionFormatter(){ };
	
	public static String prefixForm(Expression expr) {
		String s=null;
		if(expr instanceof Term){
			s=expr.toString().toLowerCase();
		}else if(expr instanceof Connective){
			s=((Connective)expr).getOperator().toString().toLowerCase();
			ExpList el=((Connective)expr).getOperands();
			if(el.size()>0){
				s+="(";
				for(int i=0;i<el.size();i++){
					if(i>0){
						s+=",";
					}
					s+=prefixForm(el.get(i));
				}
				s+=")";
			}
		}else{
			System.err.println("ExpressionFormatter: unsupported expression:"+expr);
		}
		return s;
	}

}
