/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.auxiliary.InvalidKIFException;
import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.logging.ErrorMessageListener;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;

public class RemotePlayer<TermType extends TermInterface,
	StateType extends StateInterface<TermType, ? extends StateType>> extends AbstractPlayer<TermType, StateType>  {
	
	private String host;
	private InetAddress hostAddress;
	private int port;
	private MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory;
	private GameScramblerInterface gameScrambler;
	private Logger logger;
	
	
	// MODIFIED: as we don't have the rule that the first turn is the only one where no moves are sent, we have to manage this with a boolean
	private boolean firstTurn;
	
	/**
	 * the idea to handle connection timeouts by Sam Schreiber <schreib@cs.stanford.edu>:
	 * "[...] one way to deal with unresponsive 
	 * players might be to give each player a total of (for example) 10 extra
	 * seconds total over the course of the match to spend establishing the
	 * connection, and then only cut them off with a connection timeout if they
	 * spend all of that time [...]"
	 */
	private int connectionTimeoutBonus;

	/**
	 * the maximum time (milliseconds) to wait until the connection to the player is established
	 * (the start/playclock only start after that)
	 */
	private static final int CONNECTION_TIMEOUT = 2000;
	private static final int CONNECTION_TIMEOUT_BONUS = 30000;
	
	public RemotePlayer(String name, String host, int port, GDLVersion gdlVersion, MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory, GameScramblerInterface gamescrambler) {
		super(name);
		this.host=host;
		this.port=port;
		this.movefactory=movefactory;
		this.gameScrambler=gamescrambler;
		this.logger=Logger.getLogger("tud.gamecontroller");
		this.gdlVersion = gdlVersion; // this is not necessarily the same as the played game's gdlVersion, since a GDLv2 player can play GDLv1 games!
	}
	
	@Override
	public void gameStart(MatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		
		super.gameStart(match, role, notifier);
		
		this.firstTurn = true;
		
		String gameDescription = match.getGame().getKIFGameDescription().toUpperCase();
		
		// if this is a Regular GDL game and the player understands GDL-II, add necessary sees(Role,Did(Role2,Move)) ← true(does(Role2,Move))
		if (match.getGame().getGdlVersion() == GDLVersion.v1 && this.getGdlVersion() == GDLVersion.v2) {
			//gameDescription += "\n\n";
			for (RoleInterface<?> eachRole: match.getGame().getOrderedRoles())
				gameDescription += ( "(<= (sees "+role+" (did "+eachRole+" ?move) )"+
										"(does "+eachRole+" ?move)"+
										") " ).toUpperCase();
		}
		
		hostAddress = null; // don't use an old hostAddress for a new match
		connectionTimeoutBonus = CONNECTION_TIMEOUT_BONUS;
		String msg="(START "+
				match.getMatchID()+" "+
				gameScrambler.scramble(role.getKIFForm()).toUpperCase()+
				" ("+gameScrambler.scramble(gameDescription)+") "+
				match.getStartclock()+" "+match.getPlayclock()+")";
		notifyStartRunning();
		String reply=sendMsg(msg, notifier);
		notifyStopRunning();
		logger.info("reply from "+this.getName()+": "+reply+ " after "+getLastMessageRuntime()+"ms");
		
			
	}

	/** MODIFIED
	 * This is the core of GDL-II:
	 * Instead of sending the actual joint move to the remote players, they only get the computed "sees fluents".
	 * (and this is the only place where we apply two different behaviors: send the sees fluents in GDL-II games,
	 * and send the prior moves in regular GDL games)
	 */
	@SuppressWarnings("unchecked")
	public MoveInterface<TermType> gamePlay(Object seesFluents, ConnectionEstablishedNotifier notifier) {
		MoveInterface<TermType> move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		
		if (this.firstTurn) {
			this.firstTurn = false;
			msg+="NIL";
		}else{
			
			if (this.gdlVersion == GDLVersion.v1) { // Regular GDL player
				
				JointMoveInterface<TermType> jointMove = (JointMoveInterface<TermType>) seesFluents;
				msg+=gameScrambler.scramble(jointMove.getKIFForm()).toUpperCase();
				
			} else { // GDL-II player
				
				Collection<? extends FluentInterface<TermType>> seesFluents2 =
					(Collection<? extends FluentInterface<TermType>>) seesFluents;
				
				// MODIFIED ↓↓
				// Let's send the seesTerms (or jointMove transformed into seesTerms) to each player via the PLAY message
				// TODO: scramble this part of the message
				msg += "(";
				Iterator<? extends FluentInterface<TermType>> itF = seesFluents2.iterator();
				while(itF.hasNext()) {
					FluentInterface<TermType> fluent = itF.next();
					msg += fluent.getKIFForm() + " ";
				}
				msg += ")";

			}
			
		}
		
		msg+=")";
		String reply, descrambledReply;
		notifyStartRunning();
		reply=sendMsg(msg, notifier);
		notifyStopRunning();
		logger.info("reply from "+this.getName()+": "+reply+ " after "+getLastMessageRuntime()+"ms");
		if(reply!=null){
			descrambledReply=gameScrambler.descramble(reply);
			try {
				move=movefactory.getMoveFromKIF(descrambledReply);
			} catch (InvalidKIFException ex) {
				String message = "Error parsing reply \""+reply+"\" from "+this+": "+ex.getMessage();
				logErrorMessage(GameControllerErrorMessage.PARSING_ERROR, message);
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
	public void gameStop(JointMoveInterface<TermType> jointMove, ConnectionEstablishedNotifier notifier) {
		String msg="(STOP "+match.getMatchID()+" ";
		if(jointMove==null){
			msg+="NIL";
		}else{
			msg+=gameScrambler.scramble(jointMove.getKIFForm()).toUpperCase();
		}
		msg+=")";
		//notifyStartRunning(); // don't count time for the stop message
		/*String reply=*/ sendMsg(msg, notifier);
		//notifyStopRunning();
		//logger.info("reply from "+this.getName()+": "+reply+ " after "+getLastMessageRuntime()+"ms");
	}

	private String sendMsg(String msg, ConnectionEstablishedNotifier notifier) {
		String reply=null;
		Socket s=null;
		OutputStream out=null;
		InputStream is=null;
		InetAddress hostAddress=null;
		
		try {
			logger.info("Begin creating Socket for " + this);
			long t0 = System.currentTimeMillis();
			hostAddress = getHostAddress();
			int dnsTime = (int)(System.currentTimeMillis() - t0);
			s = new Socket();
			try {
				s.connect(new InetSocketAddress(hostAddress, port), CONNECTION_TIMEOUT + connectionTimeoutBonus - dnsTime);
			} catch(InterruptedIOException e) {
				connectionTimeoutBonus -= Math.max(0, System.currentTimeMillis() - t0 - CONNECTION_TIMEOUT); // subtract the excess connection time from the bonus if greater than CONNECTION_TIMEOUT
				throw e; // will be catched again below
			}
			connectionTimeoutBonus -= Math.max(0, System.currentTimeMillis() - t0 - CONNECTION_TIMEOUT); // subtract the excess connection time from the bonus if greater than CONNECTION_TIMEOUT 
			notifier.connectionEstablished();
			logger.info("Done creating Socket for " + this);
			
			out=s.getOutputStream();
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
			
			is = s.getInputStream();
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
		} catch (InterruptedIOException e) {
			String message = "error: io error for "+ this+" : "+e.getMessage();
			logErrorMessage(GameControllerErrorMessage.IO_ERROR, message);
			Thread.currentThread().interrupt();
		} catch (UnknownHostException e) {
			String message = "error: unknown host \""+ host+ "\"";
			logErrorMessage(GameControllerErrorMessage.UNKNOWN_HOST, message);
		} catch (IOException e) {
			String message = "error: io error for "+ this+" : "+e.getMessage();
			logErrorMessage(GameControllerErrorMessage.IO_ERROR, message);
		} finally {
			try{
				if(out!=null) out.close();
				if(is!=null) is.close();
				if(s!=null) s.close();
			}catch(Exception ex){ };
			// call the notifier in case of an exception, otherwise
			// the GameController will wait forever if the exception occurred
			// before the sending of the message
			notifier.connectionEstablished();
		}
		return reply;
	}


	private void logErrorMessage(String type, String message) {
		GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(type, message, this.getName());
		if (match instanceof ErrorMessageListener) {
			((ErrorMessageListener<?, ?>) match).notifyErrorMessage(errorMessage);
		}
		logger.log(Level.SEVERE, message, errorMessage);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("remote(");
		sb.append(getName());
		String ip = (hostAddress!=null?hostAddress.getHostAddress():null);
		if(!host.equals(ip)) {
			sb.append(", host:").append(host);
		}
		if(ip!=null) {
			sb.append(", ip:").append(ip);
		}
		sb.append(", port:").append(port).append(")");
		return sb.toString();
	}

	private InetAddress getHostAddress() throws UnknownHostException {
		if (hostAddress == null) {
			hostAddress = InetAddress.getByName(host);
		}
		return hostAddress;
	}
}
