/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.aux.InvalidKIFException;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;

public class RemotePlayer<TermType extends TermInterface> extends AbstractPlayer<TermType>  {

	private String host;
	private int port;
	private MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory;
	private GameScramblerInterface gameScrambler;
	private Logger logger;
	
	public RemotePlayer(String name, String host, int port, MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory, GameScramblerInterface gamescrambler) {
		super(name);
		this.host=host;
		this.port=port;
		this.movefactory=movefactory;
		this.gameScrambler=gamescrambler;
		this.logger=Logger.getLogger("tud.gamecontroller");
	}

	@Override
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		String msg="(START "+
				match.getMatchID()+" "+
				gameScrambler.scramble(role.getKIFForm()).toUpperCase()+
				" ("+gameScrambler.scramble(match.getGame().getKIFGameDescription()).toUpperCase()+") "+
				match.getStartclock()+" "+match.getPlayclock()+")";
		notifyStartRunning();
		String reply=sendMsg(msg, match.getStartclock(), notifier);
		notifyStopRunning();
		logger.info("reply from "+this.getName()+": "+reply+ " after "+getLastMessageRuntime()+"ms");
			
	}

	public MoveInterface<TermType> gamePlay(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier) {
		MoveInterface<TermType> move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		if(jointMove==null){
			msg+="NIL";
		}else{
			msg+=gameScrambler.scramble(jointMove.getKIFForm()).toUpperCase();
		}
		msg+=")";
		String reply, descrambledReply;
		notifyStartRunning();
		reply=sendMsg(msg, match.getPlayclock(), notifier);
		notifyStopRunning();
		logger.info("reply from "+this.getName()+": "+reply+ " after "+getLastMessageRuntime()+"ms");
		if(reply!=null){
			descrambledReply=gameScrambler.descramble(reply);
			try {
				move=movefactory.getMoveFromKIF(descrambledReply);
			} catch (InvalidKIFException ex) {
				String message = "Error parsing reply \""+reply+"\" from "+this+": "+ex.getMessage();
				logger.log(Level.SEVERE, message, new GameControllerErrorMessage(GameControllerErrorMessage.PARSING_ERROR, message, match, this.getName()));
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
	public void gameStop(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier) {
		String msg="(STOP "+match.getMatchID()+" ";
		if(jointMove==null){
			msg+="NIL";
		}else{
			msg+=gameScrambler.scramble(jointMove.getKIFForm()).toUpperCase();
		}
		msg+=")";
		//notifyStartRunning(); // don't count time for the stop message
		/*String reply=*/ sendMsg(msg, match.getPlayclock(), notifier);
		//notifyStopRunning();
		//logger.info("reply from "+this.getName()+": "+reply+ " after "+getLastMessageRuntime()+"ms");
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
		} catch (InterruptedIOException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} catch (UnknownHostException e) {
			String message = "error: unknown host \""+ host+ "\"";
			logger.log(Level.SEVERE, message, new GameControllerErrorMessage(GameControllerErrorMessage.UNKNOWN_HOST, message, match, this.getName()));
			// call the notifier in case of an exception, otherwise 
			// the GameController will wait forever if the exception occurred
			// before the sending of the message
			notifier.messageWasSent();
		} catch (IOException e) {
			String message = "error: io error for "+ this+" : "+e.getMessage();
			logger.log(Level.SEVERE, message, new GameControllerErrorMessage(GameControllerErrorMessage.IO_ERROR, message, match, this.getName()));
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
