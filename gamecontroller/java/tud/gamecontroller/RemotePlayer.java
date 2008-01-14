package tud.gamecontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;

public class RemotePlayer implements Player {
	private String host;
	private int port;
	private int playclock;
	
	public RemotePlayer(String host, int port) {
		this.host=host;
		this.port=port;
	}

	public void gameStart(GameInterface game, Role role, int startclock, int playclock) {
		this.playclock=playclock;
		String msg="(START TestMatch "+role+" (\n"+game.getGameDescription().toUpperCase()+"\n) "+startclock+" "+playclock+")";
		sendMsg(msg, startclock);
	}

	public Move gamePlay(Move[] priormoves) {
		String msg="(PLAY TestMatch ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(int i=0;i<priormoves.length;i++){
				msg+=priormoves[i]+" ";
			}
			msg+="))";
		}
		String reply=sendMsg(msg, playclock);
		if(reply==null)
			reply="NIL";
		ExpList list=Parser.parseDesc("(bla "+reply+")");
		if(list.size()>0){
			return new Move(((Connective)list.get(0)).getOperands().get(0));
		}else{
			return new Move(new Atom("NIL"));
		}
	}

	public void gameStop(Move[] priormoves) {
		String msg="(STOP TestMatch ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(int i=0;i<priormoves.length;i++){
				msg+=priormoves[i]+" ";
			}
			msg+="))";
		}
		sendMsg(msg, playclock);
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
			System.err.println("error: unknown host \""+ host+ "\"");
		} catch (IOException e) {
			System.err.println("error: io error for "+ this+" : "+e.getMessage());
		}
		return reply;
	}

	public String toString(){
		return "remote("+host+":"+port+")";
	}

}
