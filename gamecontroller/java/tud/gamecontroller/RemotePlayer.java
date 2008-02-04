package tud.gamecontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;

public class RemotePlayer implements Player {
	private String host;
	private int port;
	private Match match;
	private String name;
	
	public RemotePlayer(String name, String host, int port) {
		this.host=host;
		this.port=port;
		this.name=name;
		this.match=null;
	}

	public void gameStart(Match match, Role role) {
		this.match=match;
		String msg="(START "+match.getMatchID()+" "+role+" (\n"+match.getGame().getGameDescription().toUpperCase()+"\n) "+match.getStartclock()+" "+match.getPlayclock()+")";
		sendMsg(msg, match.getStartclock());
	}

	public Move gamePlay(Move[] priormoves) {
		Move move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(int i=0;i<priormoves.length;i++){
				msg+=priormoves[i]+" ";
			}
			msg+="))";
		}
		String reply=sendMsg(msg, match.getPlayclock());
		try{
			ExpList list=Parser.parseDesc("(bla "+reply+")");
			if(list.size()>0){
				Expression expr=((Connective)list.get(0)).getOperands().get(0);
				if(expr.getVars().size()>0){
					Logger.getLogger("tud.gamecontroller").severe("Reply from "+this+" is not a ground term:"+reply);
				}else{
					move=new Move(expr);
				}
			}else{
				Logger.getLogger("tud.gamecontroller").severe("Reply from "+this+" is not a valid term:"+reply);
			}
		}catch(Exception ex){
			Logger.getLogger("tud.gamecontroller").severe("Exception while parsing reply \""+reply+"\" from "+this+":"+ex.getMessage());
		}
		if(move==null){
			move=new Move(new Atom("NIL"));
		}
		return move;
	}

	public void gameStop(Move[] priormoves) {
		String msg="(STOP "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(int i=0;i<priormoves.length;i++){
				msg+=priormoves[i]+" ";
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
		return "remote("+host+":"+port+")";
	}

	public String getName() {
		return name;
	}

}
