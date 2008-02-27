package tud.gamecontroller.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.aux.InvalidKIFException;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;

public class RemotePlayer<
		RoleType extends RoleInterface,
		MoveType
		> extends AbstractPlayer<RoleType, MoveType, MatchInterface<?,? extends GameInterface<?, ?>, ?>>  {

	private String host;
	private int port;
	private MoveFactoryInterface<MoveType> termfactory;
	private GameScramblerInterface gameScrambler;
	private Logger logger;
	
	public RemotePlayer(String name, String host, int port, MoveFactoryInterface<MoveType> termfactory, GameScramblerInterface gamescrambler) {
		super(name);
		this.host=host;
		this.port=port;
		this.termfactory=termfactory;
		this.gameScrambler=gamescrambler;
		this.logger=Logger.getLogger("tud.gamecontroller");
	}

	@Override
	public void gameStart(MatchInterface<?,? extends GameInterface<?, ?>, ?> match, RoleType role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		String msg="(START "+
				match.getMatchID()+" "+
				gameScrambler.scramble(role.getKIFForm()).toUpperCase()+
				" ("+gameScrambler.scramble(match.getGame().getKIFGameDescription()).toUpperCase()+") "+
				match.getStartclock()+" "+match.getPlayclock()+")";
		sendMsg(msg, match.getStartclock(), notifier);
	}

	public MoveType gamePlay(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, MessageSentNotifier notifier) {
		MoveType move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		if(jointMove==null){
			msg+="NIL";
		}else{
			msg+=gameScrambler.scramble(jointMove.getKIFForm()).toUpperCase();
		}
		msg+=")";
		String reply=sendMsg(msg, match.getPlayclock(), notifier), descrambledReply;
		logger.info("reply from "+this.getName()+": "+reply);
		if(reply!=null){
			descrambledReply=gameScrambler.descramble(reply);
			try {
				move=termfactory.getMoveFromKIF(descrambledReply);
			} catch (InvalidKIFException ex) {
				logger.severe("Error parsing reply \""+reply+"\" from "+this+":"+ex.getMessage());
			}
//			if(moveterm!=null && !moveterm.isGround()){
//				logger.severe("Reply \""+reply+"\" from "+this+" is not a ground term. (descrambled:\""+descrambledReply+"\")");
//			}else if(moveterm!=null){
//				move=new MoveType(moveterm);
//			}
		}
		return move;
	}

	@Override
	public void gameStop(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, MessageSentNotifier notifier) {
		String msg="(STOP "+match.getMatchID()+" ";
		if(jointMove==null){
			msg+="NIL";
		}else{
			msg+=gameScrambler.scramble(jointMove.getKIFForm()).toUpperCase();
		}
		msg+=")";
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
