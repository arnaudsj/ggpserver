package tud.gamecontroller.game.jocular;

import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.Parser;
import tud.gamecontroller.aux.InvalidKIFException;

public class TermFactory implements tud.gamecontroller.term.TermFactoryInterface<Term> {
	Parser parser;

	public TermFactory(Parser parser){
		this.parser=parser;
	}
	
	public Term getTermFromKIF(String kif) throws InvalidKIFException {
		try{
			GdlExpression gdlExpr=parser.parse(kif).getElement(0);
			return new Term(parser.getSymbolTable(), stanfordlogic.prover.Term.buildFromGdl(gdlExpr));
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex);
		}
	}
}
