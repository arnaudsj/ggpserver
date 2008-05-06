///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

/**
 * 
 */
package stanfordlogic.test.prover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.knowledge.GameInformation;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.knowledge.BasicKB;
import stanfordlogic.knowledge.MetaGdl;
import stanfordlogic.knowledge.RelationNameProcessor;
import stanfordlogic.prover.AbstractReasoner;
import stanfordlogic.prover.BasicReasoner;
import stanfordlogic.prover.Expression;
import stanfordlogic.prover.Fact;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.ProofContext;
import stanfordlogic.prover.Implication;
import stanfordlogic.prover.VariableFact;
import stanfordlogic.game.GameManager;
import junit.framework.TestCase;

/**
 *
 */
public class TestProver extends TestCase
{
    KnowledgeBase kb_;
    SymbolTable symbolTable_;
    Parser parser_;
    
    static TestProver currentProver;
    
    public static SymbolTable getTable()
    {
        return currentProver.symbolTable_;
    }
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        parser_ = GameManager.getParser();
        symbolTable_ = parser_.getSymbolTable();
        currentProver = this;
        
        kb_ = new BasicKB();
    }
    
    private void addFacts(GdlList facts)
    {
        for ( GdlExpression exp : facts )
            kb_.setTrue( GroundFact.fromExpression(exp) );
    }
    
    private GroundFact getAnAnswer( AbstractReasoner r, Fact f )
    {
        return r.getAnAnswer( f, ProofContext.makeDummy(parser_));
    }
    
    private List<GroundFact> getAllAnswers( AbstractReasoner r, Fact f )
    {
        return r.getAllAnswers(f, ProofContext.makeDummy(parser_));
    }

    public void testGroundProof()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 3) (succ 3 4)");
        addFacts(succs);
        
        AbstractReasoner r = new BasicReasoner(kb_, new ArrayList<Implication>(), parser_ );
        
        // Make sure we can prove all of our ground facts!
        GroundFact answer1 = getAnAnswer( r, new GroundFact(symbolTable_, "succ", "1", "2") );
        GroundFact answer2 = getAnAnswer( r, new GroundFact(symbolTable_, "succ", "2", "3") );
        GroundFact answer3 = getAnAnswer( r, new GroundFact(symbolTable_, "succ", "3", "4") );
        
        assertEquals( new GroundFact(symbolTable_, "succ", "1", "2"), answer1 );
        assertEquals( new GroundFact(symbolTable_, "succ", "2", "3"), answer2 );
        assertEquals( new GroundFact(symbolTable_, "succ", "3", "4"), answer3 );
    }
    
    public void testSingleGroundProofWithVars()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 3) (succ 3 4)");
        addFacts(succs);
        
        AbstractReasoner r = new BasicReasoner(kb_, new ArrayList<Implication>(), parser_ );
        
        // See if we can get just one answer
        GdlList questionList = parser_.parse("succ ?x 2");
        Fact question = VariableFact.fromList(questionList);
        
        GroundFact answer = getAnAnswer( r, question );
        assertEquals( new GroundFact(symbolTable_, "succ", "1", "2"), answer);
        
        questionList = parser_.parse("succ ?x 2");
        question = VariableFact.fromList(questionList);
        
        answer = getAnAnswer( r, question );
        assertNotNull(answer);
        assertEquals( new GroundFact(symbolTable_, "succ", "1", "2"), answer);
    }
    
    public void testMultiGroundProofWithVars()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 3) (succ 3 4)");
        addFacts(succs);
        
        AbstractReasoner r = new BasicReasoner(kb_, new ArrayList<Implication>(), parser_ );
        
        //  Now see if we can get *all* answers
        GdlList questionList = parser_.parse("succ ?x ?y");
        Fact question = VariableFact.fromList(questionList);
        
        List<GroundFact> answers = getAllAnswers( r,question);
        assertEquals( 3, answers.size() );
        
        assertEquals( new GroundFact(symbolTable_, "succ", "1", "2"), answers.get(0) );
        assertEquals( new GroundFact(symbolTable_, "succ", "2", "3"), answers.get(1) );
        assertEquals( new GroundFact(symbolTable_, "succ", "3", "4"), answers.get(2) );
    }
    
    public void testGroundProofWithRule()
    {
        GdlList succs = parser_.parse("(succ 1 2)");
        addFacts(succs);
        
        Expression conjunct = GroundFact.fromList( parser_.parse("succ 1 2") );
        Implication rule = new Implication( GroundFact.fromList(parser_.parse("gt 2 1")), conjunct);
        
        ArrayList<Implication> rules = new ArrayList<Implication>();
        rules.add(rule);
        
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_ );
        
        // Make sure we can prove all of our ground facts!
        GroundFact answer1 = getAnAnswer( r, new GroundFact(symbolTable_, "gt", "2", "1") );
        
        assertEquals( new GroundFact(symbolTable_, "gt", "2", "1"), answer1 );
    }
    
    public void testGroundProofWithRuleAndVars()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 2) (succ 1 3)");
        addFacts(succs);
        
        Expression conjunct = VariableFact.fromList( parser_.parse("succ 1 ?x") );
        Implication rule = new Implication( VariableFact.fromList(parser_.parse("gt ?x 1")), conjunct);
        
        ArrayList<Implication> rules = new ArrayList<Implication>();
        rules.add(rule);
        
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_ );
        
        // Make sure we can prove all of our ground facts!
        GroundFact answer1 = getAnAnswer( r, new GroundFact(symbolTable_, "gt", "2", "1") );
        
        assertEquals( new GroundFact(symbolTable_, "gt", "2", "1"), answer1 );
        
        // Do it again just for good measure
        answer1 = getAnAnswer( r, new GroundFact(symbolTable_, "gt", "2", "1") );
        
        assertEquals( new GroundFact(symbolTable_, "gt", "2", "1"), answer1 );
    }

    public void testGroundProofWithRulesAndVars()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 2) (succ 1 3)");
        addFacts(succs);
        
        Expression conjunct = VariableFact.fromList( parser_.parse("succ 2 ?x") );
        Implication rule = new Implication( VariableFact.fromList(parser_.parse("gt ?x 1")), conjunct);
        
        Expression conjunct2 = VariableFact.fromList( parser_.parse("succ ?y ?x") );
        Implication rule2 = new Implication( VariableFact.fromList(parser_.parse("gt ?x ?y")), conjunct2);
        
        ArrayList<Implication> rules = new ArrayList<Implication>();
        rules.add(rule);
        rules.add(rule2);
        
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_ );
        
        // Make sure we can prove all of our ground facts!
        List<GroundFact> answers = getAllAnswers( r, VariableFact.fromList(parser_.parse("gt ?x ?y")) );
        
        // note: we'll see if we get (gt 2 1) twice
        assertEquals( 3, answers.size() );
        
        // Do it again, for good measure
        answers = getAllAnswers( r, VariableFact.fromList(parser_.parse("gt ?x ?y")) );
        
        // note: we'll see if we get (gt 2 1) twice
        assertEquals( 3, answers.size() );
    }
    
    public void testGroundProofWithMultiConjunction()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 2) (succ 1 3) (succ 2 3) (succ 3 2)");
        addFacts(succs);
        
        Expression conjunct = VariableFact.fromList( parser_.parse("succ 2 ?x") );
        Expression conjunct2 = VariableFact.fromList( parser_.parse("succ ?x 2") );
        Implication rule = new Implication( VariableFact.fromList(parser_.parse("gt ?x")), conjunct, conjunct2);
        
        ArrayList<Implication> rules = new ArrayList<Implication>();
        rules.add(rule);
        
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_ );
        
        
        // Make sure we can prove all of our ground facts!
        List<GroundFact> answers = getAllAnswers( r, VariableFact.fromList(parser_.parse("gt ?y")) );
        
        assertEquals( 2, answers.size() );
        
        // Do it again
        answers = getAllAnswers( r, VariableFact.fromList(parser_.parse("gt ?y")) );
        
        assertEquals( 2, answers.size() );
    }
    
    public void testGroundProofWithLevels()
    {
        GdlList succs = parser_.parse("(succ 1 2) (succ 2 2) (succ 1 3) (succ 2 3) (succ 3 2) (succ 2 1)");
        addFacts(succs);
        
        Expression conjunct = VariableFact.fromList( parser_.parse("succ 2 ?x") );
        Expression conjunct2 = VariableFact.fromList( parser_.parse("pop ?x") );
        Implication rule = new Implication( VariableFact.fromList(parser_.parse("gt ?x")), conjunct, conjunct2);
        
        Expression conjunct3 = VariableFact.fromList( parser_.parse("succ ?y 2") );
        Implication rule2 = new Implication( VariableFact.fromList(parser_.parse("pop ?y")), conjunct3);
        
        ArrayList<Implication> rules = new ArrayList<Implication>();
        rules.add(rule);
        rules.add(rule2);
        
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_ );
         
        // Make sure we can prove all of our ground facts!
        List<GroundFact> answers = getAllAnswers( r, VariableFact.fromList(parser_.parse("gt ?y")) );
        
        assertEquals( 3, answers.size() );
        
        // Do it again
        answers = getAllAnswers( r, VariableFact.fromList(parser_.parse("gt ?y")) );
        
        assertEquals( 3, answers.size() );
    }
    
    private List<Implication> getTicTacToeRules() throws IOException
    {
        return MetaGdl.examineGame("game-defs/tictactoe.kif", parser_).getRules();
    }
    
    public void testTicTacToeTerminal() throws IOException
    {
        List<Implication> rules = getTicTacToeRules();
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_);
        
        GdlList facts = parser_.parse("(true (cell 1 1 b))(true (cell 1 2 b))(true (cell 1 3 b))(true (cell 2 1 b))(true (cell 2 2 b))(true (cell 2 3 b))(true (cell 3 1 b))(true (cell 3 2 b))(true (cell 3 3 b))(true (control xplayer))");
        addFacts(facts);
        
        Fact f = getAnAnswer( r, Fact.fromExpression( parser_.parse("terminal") ) );
        assertNull(f);
        
//        kb_.clear();
//        facts = parser_
//                .parse( "(true (cell 1 1 x)) (true (cell 1 2 o)) (true (cell 1 3 x)) (true (cell 2 1 o)) (true (cell 2 2 x)) (true (cell 2 2 x)) (true (cell 2 3 o)) (true (cell 3 1 b)) (true (cell 3 2 b)) (true (cell 3 3 b))" );
//        addFacts(facts);
//        
//        f = getAnAnswer( r, Fact.fromExpression( parser_.parse("terminal") ) );
//        assertNull(f);
    }
    
    public void testSpecificMove()
    {
        GameInformation info = MetaGdl.examineGame("game-defs/tictactoe.kif", parser_);
        List<Implication> rules = info.getRules();
        
        assertEquals(26, rules.size());
        
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_);
        
        GdlList facts = parser_.parse( "(true (cell 1 1 x)) (true (cell 1 2 x)) (true (cell 1 3 o)) (true (cell 2 1 x)) (true (cell 2 2 o)) (true (cell 2 3 b)) (true (cell 3 1 o)) (true (cell 3 2 b)) (true (cell 3 3 b)) (true (control xplayer))" );
        addFacts(facts);
        
        List<GroundFact> results = getAllAnswers( r, Fact.fromExpression( parser_.parse("legal xplayer ?x") ) );
        assertEquals(3, results.size());
    }
    
    public void testMoreTicTacToe() throws IOException
    {
        GameInformation info = MetaGdl.examineGame("game-defs/tictactoe.kif", parser_);
        List<Implication> rules = info.getRules();
        AbstractReasoner r = new BasicReasoner(kb_, rules, parser_);
        
        GdlList facts = parser_.parse("(true (cell 1 1 x))(true (cell 1 2 o))(true (cell 1 3 x))(true (cell 2 1 o))(true (cell 2 2 x))(true (cell 2 3 b))(true (cell 3 1 b))(true (cell 3 2 b))(true (cell 3 3 b))(true (control oplayer))");
        addFacts(facts);
        
        // Get all next facts
        GroundFact does = GroundFact.fromExpression( parser_.parse("does xplayer noop") );
        GroundFact does2 = GroundFact.fromExpression( parser_.parse("does oplayer (mark 2 3)") );
        Fact question = VariableFact.fromExpression( parser_.parse("next ?x") );
        
        KnowledgeBase kb = new BasicKB();
        
        kb.setTrue(does);
        kb.setTrue(does2);
        
        ProofContext context = new ProofContext(kb, parser_);
        
        List<GroundFact> results = r.getAllAnswers( question, context );
        
        assertEquals(10, results.size());
    }
    
    public void testTicTacToe() throws IOException
    {
        GameInformation info = MetaGdl.examineGame("game-defs/tictactoe.kif", parser_);
        
        KnowledgeBase staticKb = new BasicKB();
        staticKb.loadWithFacts(info.getAllGrounds());
        
        AbstractReasoner r = new BasicReasoner(staticKb, info.getIndexedRules(), parser_);
        
        
        Fact initQuestion = Fact.fromExpression(parser_.parse("init ?x"));
        
        // Compute the initial state
        List<GroundFact> inits = r.getAllAnswers(initQuestion);
        
        KnowledgeBase currentState = new BasicKB();
        
        RelationNameProcessor trueProcessor = new RelationNameProcessor(parser_.TOK_TRUE);
        for (GroundFact init: inits) {
            currentState.setTrue(trueProcessor.processFact(init));
        }
        
        assertEquals(10, currentState.getNumFacts());
        
        GroundFact [] moves;
        
        // Do some updates
        moves = new GroundFact [] {
                                   (GroundFact) makeFact("does xplayer (mark 1 1)"),
                                   (GroundFact) makeFact("does oplayer noop"),
        };
        currentState = updateKbWithMoves(r, currentState, moves);
        System.out.println(currentState.stateToGdl());
        assertEquals(10, currentState.getNumFacts());
        
        moves = new GroundFact [] {
                                   (GroundFact) makeFact("does xplayer noop"),
                                   (GroundFact) makeFact("does oplayer (mark 1 3)"),
        };
        currentState = updateKbWithMoves(r, currentState, moves);
        System.out.println(currentState.stateToGdl());
        assertEquals(10, currentState.getNumFacts());
    }
    
    private KnowledgeBase updateKbWithMoves(AbstractReasoner r, KnowledgeBase kb, GroundFact ... moves)
    {
        for (GroundFact move : moves) {
            kb.setTrue(move);
        }
        
        Fact nextQuestion = Fact.fromExpression(parser_.parse("next ?x"));
        
        ProofContext context = new ProofContext(kb, parser_);
        
        List<GroundFact> nexts = r.getAllAnswers(nextQuestion, context);
        
        KnowledgeBase newKb = new BasicKB();
        
        RelationNameProcessor trueProcessor = new RelationNameProcessor(parser_.TOK_TRUE);
        
        for (GroundFact next : nexts) {
            newKb.setTrue(trueProcessor.processFact(next));
        }
        
        return newKb;
        
    }
    
    public void testBigMinichess() throws IOException
    {
        // Load the rules for mini-chess
        GameInformation info = MetaGdl.examineGame("game-defs/minichess.kif", parser_);
        
        KnowledgeBase staticKb = new BasicKB();
        staticKb.loadWithFacts( info.getAllGrounds() );
        
        List<Implication> rules = info.getRules();
        
        KnowledgeBase volatileKb = new BasicKB();
        
        AbstractReasoner reasoner = new BasicReasoner(staticKb, rules, parser_);
        
        Fact question = VariableFact.fromList( parser_.parse("init ?x") );
        
        ProofContext context = new ProofContext(volatileKb, parser_);
        
        List<GroundFact> init = reasoner.getAllAnswers( question, context );
        
        // Make sure that we have a cache for all the initial truths
        assertEquals( 18, init.size() );
        
        RelationNameProcessor processor = new RelationNameProcessor(parser_.TOK_TRUE);
        
        for ( GroundFact f : init )
            volatileKb.setTrue ( processor.processFact(f) );
            
                // Find out how many legal moves there are
        List<GroundFact> legal = reasoner.getAllAnswers( makeFact( "legal white ?x" ),
                context );

        // Make sure there's the right amount of legal moves
        assertEquals( 7, legal.size() );

        // Is the game terminal?
        assertNull( reasoner.getAnAnswer( makeFact( "terminal" ), context ) );

        // Make some move
        GroundFact does1 = (GroundFact) makeFact( "does white (move wk c 1 c 2)" );
        GroundFact does2 = (GroundFact) makeFact( "does black noop" );

        volatileKb.setTrue( does1 );
        volatileKb.setTrue( does2 );

        // Find the next state.
        List<GroundFact> nextTruths = reasoner.getAllAnswers( makeFact( "next ?x" ),
                context );

        assertEquals( 18, nextTruths.size() );
    }
    
    
    public void testChessLegalMoves()
    {
        // Load the rules for chess
        GameInformation info = MetaGdl.examineGame("game-defs/chess.kif", parser_);
        
        KnowledgeBase staticKb = new BasicKB();
        staticKb.loadWithFacts( info.getAllGrounds() );
        
        List<Implication> rules = info.getRules();
        
        KnowledgeBase volatileKb = new BasicKB();
        
        AbstractReasoner reasoner = new BasicReasoner(staticKb, rules, parser_);
        
        Fact question = VariableFact.fromList( parser_.parse("init ?x") );
        
        ProofContext context = new ProofContext(volatileKb, parser_);
        
        List<GroundFact> init = reasoner.getAllAnswers( question, context );
        
        assertEquals(66, init.size());
        
        RelationNameProcessor processor = new RelationNameProcessor(parser_.TOK_TRUE);
        
        for ( GroundFact f : init )
            volatileKb.setTrue ( processor.processFact(f) );
            
        // Find out how many legal moves there are
        List<GroundFact> legal = reasoner.getAllAnswers( makeFact( "legal white ?x" ),
                context );

        // Make sure there's the right amount of legal moves
        assertEquals( 20, legal.size() );
    }
    
    private Fact makeFact(String str)
    {
        return Fact.fromExpression( parser_.parse(str) );
    }
}
