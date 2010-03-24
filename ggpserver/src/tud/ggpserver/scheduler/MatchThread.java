package tud.ggpserver.scheduler;

import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.matches.RunningMatch;

public class MatchThread<TermType extends TermInterface, ReasonerStateInfoType> extends Thread {
	
	private RunningMatch<TermType,ReasonerStateInfoType> match;
	
	public MatchThread (String s, RunningMatch<TermType,ReasonerStateInfoType> match) {
		super(s);
		this.match = match;
	}
	
	public RunningMatch<TermType,ReasonerStateInfoType> getMatch () {
		return this.match;
	}
	
}
