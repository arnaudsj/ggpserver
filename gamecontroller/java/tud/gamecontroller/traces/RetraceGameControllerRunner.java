/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tud.gamecontroller.traces;

import cs227b.teamIago.util.GameState;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.exceptions.NoLegalMoveException;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.game.impl.RunnableMatch;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.game.javaprover.JavaProverGameController;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.players.MovelistPlayer;
import tud.gamecontroller.players.Player;

public class RetraceGameControllerRunner {

	public static void retrace(File inputFile, File outputFile, File gameFile, GDLVersion gdlVersion) throws IOException, SAXException {
		/* create game */
		ReasonerFactory<Term, GameState> reasonerFactory = new ReasonerFactory<Term, GameState>() {
			public ReasonerInterface<Term, GameState> createReasoner(String gameDescription, String gameName) {
				return new Reasoner(gameDescription);
			}
		};
		
		GameInterface<Term, State<Term, GameState>> game = new Game<Term, GameState>(gameFile, reasonerFactory, gdlVersion);
		
		/* create players */
		Map<RoleInterface<Term>, Player<Term, State<Term, GameState>>> players = new HashMap<RoleInterface<Term>, Player<Term, State<Term, GameState>>>();
		MatchTrace trace = new MatchTraceReader().read(inputFile);
		
		List<? extends RoleInterface<Term>> roles = game.getOrderedRoles();
		for (RoleInterface<Term> role : roles) {
			Player<Term, State<Term, GameState>> player = new MovelistPlayer<Term, State<Term, GameState>>(role.getKIFForm().toLowerCase(), trace.getMovesForRole(role.getKIFForm().toLowerCase()));
			players.put(role, player);
		}
		
		/* create match */
		String matchID = "retracematch";
		int startclock = 500;
		int playclock = 500;
		RunnableMatchInterface<Term, State<Term, GameState>> match = new RunnableMatch<Term, GameState>(matchID, game, startclock, playclock, players);

		/* create game controller */
		JavaProverGameController gameController = new JavaProverGameController(match);
		
		/* add listener */
		MatchTraceBuilder matchTraceBuilder = new MatchTraceBuilder();
		gameController.addListener(matchTraceBuilder);
		
		/* run + write result */
		try {
			try {
				gameController.runGame();
			} catch (NoLegalMoveException e) {
				matchTraceBuilder.flush();
			}

			new MatchTraceWriter().write(matchTraceBuilder.getTrace(), outputFile);
		} catch (InterruptedException ex) {
			Logger.getLogger(RetraceGameControllerRunner.class.getName()).log(Level.WARNING, null, ex);
		}
	}
	
	public static void retraceDirs(File inputDir, File outputDir, File gamesDir, GDLVersion gdlVersion) throws IOException, SAXException {
		for (File inputFile : inputDir.listFiles()) {
			String gameName = inputFile.getName().substring(0, inputFile.getName().indexOf("."));
			
			File gameFile = new File(gamesDir, gameName + ".lisp");
			File outputFile = new File(outputDir, gameName + ".trace.xml");
			
			retrace(inputFile, outputFile, gameFile, gdlVersion);
		}
	}
	
	public static void main(String[] args) throws IOException, SAXException {
		try {
			if (args.length != 4) {
				throw new IllegalArgumentException("wrong number of arguments!");
			}
			if (!args[0].equals("retrace")) {
				throw new IllegalArgumentException("first argument must be 'retrace'!");
			}

			File inputFile = new File(args[1]);
			File outputFile = new File(args[2]);
			File gameFile = new File(args[3]);

			if (!inputFile.canRead()) {
				throw new IllegalArgumentException("cannot read input file!");
			}
			if (!gameFile.canRead()) {
				throw new IllegalArgumentException("cannot read game file!");
			}
			if (!inputFile.isFile()) {
				throw new IllegalArgumentException("input file is not a regular file!");
			}
			if (!gameFile.isFile()) {
				throw new IllegalArgumentException("game file is not a regular file!");
			}
			if (outputFile.exists() && !(outputFile.canWrite() && outputFile.isFile())) {
				throw new IllegalArgumentException("cannot write output file!");
			}
			
			retrace(inputFile, outputFile, gameFile, GDLVersion.v1);
		} catch (IllegalArgumentException ex) {
			System.err.println("An error occured: " + ex.getMessage());
			printUsage();
		}
	}
	
	private static void printUsage() {
		System.err.println("Usage: \n" +
				"java -jar gamecontroller-retracer.jar retrace " +
				"<input trace filename> <output trace filename> " +
				"<GDL game file name>\n");
	}
}
