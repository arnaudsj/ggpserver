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

import tud.gamecontroller.game.InvalidKIFException;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermFactoryInterface;
import tud.gamecontroller.game.TermInterface;

public class RemotePlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayer<T,S> {

	private String host;
	private int port;
	private TermFactoryInterface<T> termfactory;
	
	public RemotePlayer(String name, String host, int port, TermFactoryInterface<T> termfactory) {
		super(name);
		this.host=host;
		this.port=port;
		this.termfactory=termfactory;
	}

	public void gameStart(Match<T,S> match, Role<T> role) {
		super.gameStart(match, role);
		String msg="(START "+match.getMatchID()+" "+role+" (\n"+match.getGame().getGameDescription().toUpperCase()+"\n) "+match.getStartclock()+" "+match.getPlayclock()+")";
		sendMsg(msg, match.getStartclock());
	}

	public Move<T> gamePlay(List<Move<T>> priormoves) {
		Move<T> move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(Move<T> m:priormoves){
				msg+=m.getKIFForm()+" ";
			}
			msg+="))";
		}
		String reply=sendMsg(msg, match.getPlayclock());
		if(reply!=null){
			T moveterm=null;
			try {
				moveterm = termfactory.getTermFromKIF(reply);
			} catch (InvalidKIFException ex) {
				Logger.getLogger("tud.gamecontroller").severe("Error parsing reply from "+this+":"+ex.getMessage());
			}
			if(moveterm!=null && !moveterm.isGround()){
				Logger.getLogger("tud.gamecontroller").severe("Reply from "+this+" is not a ground term:"+reply);
			}else{
				move=new Move<T>(moveterm);
			}
		}
		return move;
	}

	public void gameStop(List<Move<T>> priormoves) {
		super.gameStop(priormoves);
		String msg="(STOP "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(Move<T> m:priormoves){
				msg+=m.getKIFForm()+" ";
			}
			msg+="))";
		}
		sendMsg(msg, match.getPlayclock());
	}

	private String sendMsg(String msg, int timeout) {
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
			Logger.getLogger("tud.gamecontroller").severe("error: unknown host \""+ host+ "\"");
		} catch (IOException e) {
			Logger.getLogger("tud.gamecontroller").severe("error: io error for "+ this+" : "+e.getMessage());
		}
		return reply;
	}

	public String toString(){
		return "remote("+getName()+", "+host+":"+port+")";
	}
}