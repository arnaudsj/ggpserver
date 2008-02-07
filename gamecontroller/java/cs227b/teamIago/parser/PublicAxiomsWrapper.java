package cs227b.teamIago.parser;

import java.util.List;

public class PublicAxiomsWrapper extends Axioms {

	public PublicAxiomsWrapper() {
		super();
	}

	public void display() {
		super.display();
	}

	public boolean parseFromString(String gdl) {
		return super.parseFromString(gdl);
	}
	
	@SuppressWarnings("unchecked")
	public List<Statement> getStatements(){
		return this.Statements;
	}
}
