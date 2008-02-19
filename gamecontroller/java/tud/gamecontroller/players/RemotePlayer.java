package tud.gamecontroller.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.InvalidKIFException;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermFactoryInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;

public class RemotePlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayer<T,S> {

	private String host;
	private int port;
	private TermFactoryInterface<T> termfactory;
	private GameScramblerInterface gameScrambler;
	private Logger logger;
	
	public RemotePlayer(String name, String host, int port, TermFactoryInterface<T> termfactory, GameScramblerInterface gamescrambler) {
		super(name);
		this.host=host;
		this.port=port;
		this.termfactory=termfactory;
		this.gameScrambler=gamescrambler;
		this.logger=Logger.getLogger("tud.gamecontroller");
	}

	public void gameStart(Match<T, S, Player<T,S>> match, Role<T> role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		String msg="(START "+
				match.getMatchID()+" "+
				gameScrambler.scramble(role.getKIFForm()).toUpperCase()+
				" ("+gameScrambler.scramble(match.getGame().getKIFGameDescription()).toUpperCase()+") "+
				match.getStartclock()+" "+match.getPlayclock()+")";
		sendMsg(msg, match.getStartclock(), notifier);
	}

	public Move<T> gamePlay(List<Move<T>> priormoves, MessageSentNotifier notifier) {
		Move<T> move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(Move<T> m:priormoves){
				msg+=gameScrambler.scramble(m.getKIFForm()).toUpperCase()+" ";
			}
			msg+="))";
		}
		String reply=sendMsg(msg, match.getPlayclock(), notifier), descrambledReply;
		logger.info("reply from "+this.getName()+": "+reply);
		if(reply!=null){
			descrambledReply=gameScrambler.descramble(reply);
			T moveterm=null;
			try {
				moveterm = termfactory.getTermFromKIF(descrambledReply);
			} catch (InvalidKIFException ex) {
				logger.severe("Error parsing reply \""+reply+"\" from "+this+":"+ex.getMessage());
			}
			if(moveterm!=null && !moveterm.isGround()){
				logger.severe("Reply \""+reply+"\" from "+this+" is not a ground term. (descrambled:\""+descrambledReply+"\")");
			}else{
				move=new Move<T>(moveterm);
			}
		}
		return move;
	}

	public void gameStop(List<Move<T>> priormoves, MessageSentNotifier notifier) {
		super.gameStop(priormoves, notifier);
		String msg="(STOP "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(Move<T> m:priormoves){
				msg+=gameScrambler.scramble(m.getKIFForm()).toUpperCase()+" ";
			}
			msg+="))";
		}
		sendMsg(msg, match.getPlayclock(), notifier);
	}

	private String sendMsg(String msg, int timeout, MessageSentNotifier notifier) {
		String reply=null;
		Socket s;
		try {
			s = new Socket(host, port);
			
			OutputStream out=s.getOutputStream();
			PrintWriter pw=new PrintWriter(out);
			pw.print("POST / HTTP/1.0\r\n");
			pw.print("Accept: text/delim\r\n");
			pw.print("Sender: Gamecontroller\r\n");
			pw.print("Receiver: "+host+"\r\n");
			pw.print("Content-type: text/acl\r\n");	
			pw.print("Content-length: "+msg.length()+"\r\n");	
			pw.print("\r\n");	
	
			pw.print(msg);
			pw.flush();
			logger.info("message to "+this.getName()+" sent: \"" + msg+ "\"");
			notifier.messageWasSent();
			
			InputStream is = s.getInputStream();
			if ( is == null) return null;
			BufferedReader in = new BufferedReader( new InputStreamReader( is ));
			String line;
			line = in.readLine();
			while( line!=null && line.trim().length() > 0 ){
				line = in.readLine();
			}
	
			char[] cbuf=new char[1024];
			int len;
			while((len=in.read(cbuf,0,1023))!=-1){
				line=new String(cbuf,0,len);
				if(reply==null) reply=line;
				else reply += line;
			}
			out.close();
			in.close();
		} catch (UnknownHostException e) {
			logger.severe("error: unknown host \""+ host+ "\"");
			// call the notifier in case of an exception, otherwise 
			// the GameController will wait forever if the exception occurred
			// before the sending of the message
			notifier.messageWasSent();
		} catch (IOException e) {
			logger.severe("error: io error for "+ this+" : "+e.getMessage());
			// call the notifier in case of an exception, otherwise 
			// the GameController will wait forever if the exception occurred
			// before the sending of the message
			notifier.messageWasSent();
		}
		return reply;
	}

	public String toString(){
		return "remote("+getName()+", "+host+":"+port+")";
//		return "remote("+host+":"+port+")";
	}
}
