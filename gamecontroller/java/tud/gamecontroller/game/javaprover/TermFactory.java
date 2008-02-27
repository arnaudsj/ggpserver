package tud.gamecontroller.game.javaprover;

import tud.gamecontroller.aux.InvalidKIFException;

import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;

public class TermFactory implements tud.gamecontroller.term.TermFactoryInterface<Term> {

	public Term getTermFromKIF(String kif) throws InvalidKIFException {
		Term term=null;
		try{
			ExpList list=Parser.parseDesc("(bla "+kif+")");
			if(list.size()>0){
				ExpList list2=((Connective)list.get(0)).getOperands();
				term=new Term(list2.get(0));
			}
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex.getMessage());
		}
		if(term==null){
			throw new InvalidKIFException("not a valid kif term:"+kif);
		}
		return term;
	}

}
