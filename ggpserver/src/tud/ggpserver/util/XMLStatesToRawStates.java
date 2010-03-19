package tud.ggpserver.util;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import tud.gamecontroller.auxiliary.Pair;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.tests.RoundRobinSchedulerTest;


public class XMLStatesToRawStates {
	
	public static final String dtd = "<!DOCTYPE match SYSTEM \"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd\">";
	
	public static DocumentBuilder builder;
	
	public static Pair<String, Long> xmlStateToRawState(String xml) throws ParserConfigurationException, SAXException, IOException {
		
		xml = xml.replace(dtd, "").replaceAll("\n", " ");
		try {
			StringReader stringReader = new StringReader(xml);
			InputSource inputSource = new InputSource(stringReader);
			Document xmldoc = builder.parse(inputSource);
			
			NodeList nodes = xmldoc.getElementsByTagName("fact");
			StringBuilder result = new StringBuilder("(");
			boolean needSpace = false;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				needSpace = getText(needSpace, node, result);
			}
			
			result.append(")");
			nodes = xmldoc.getElementsByTagName("timestamp");
			Long timestamp = null;
			if(nodes.getLength()>0){
				String timestampString = nodes.item(0).getTextContent().trim();
				timestamp = Long.valueOf(timestampString);
			}
			stringReader.close();
			return new Pair<String, Long>(result.toString(), timestamp);
		}
		catch (SAXParseException saxpe) { // probably, this state is already a raw state
			//System.out.println("  Already raw? "+xml);
			return null;
		}
		
	}
	
	
	public static boolean getText(boolean needSpace, Node n, StringBuilder result) {
		
//		System.out.println(n.getClass().getCanonicalName() + ":" + n);
		
		if(n.getNodeName().equals("fact") || n.getNodeName().equals("prop-f") || n.getNodeName().equals("arg") ) {
			if (! n.hasChildNodes() || n.getChildNodes().getLength() == 1) {
//				System.out.println("<"+n.getTextContent().trim()+">");
				String s = n.getTextContent().trim();
				if(s.isEmpty())
					return needSpace;
				else {
					if(needSpace)
						result.append(' ');
					result.append(s);
					return true;
				}
			} else {
//				System.out.println("\thas children");
				result.append('(');
				needSpace = false;
				NodeList children = n.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					needSpace = getText(needSpace, children.item(i), result);
				}
				result.append(')');
				return false;
			}
		} else {
			return needSpace;
		}
		
	}
	
	
	public static void xmlStatesToRawStates() throws SQLException, ParserConfigurationException, SAXException, IOException, NamingException {
		
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		RoundRobinSchedulerTest.setupJNDI();
		
		final long startAt = 950000; // TODO: reset to 0
		
		final long stepSize = 50000;
		boolean noResult = false;
		Connection con = AbstractDBConnector.getConnection();
		PreparedStatement ps = con.prepareStatement(
				"SELECT `match_id` , `step_number` , `state`, `timestamp` " +
				"FROM `states` " +
				"LIMIT ? , " + stepSize + " ; ", 
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		for(long firstStateIndex = startAt ; !noResult ; firstStateIndex+=stepSize) {
			System.out.println("\nprocessing states " + firstStateIndex + " - " + (firstStateIndex+stepSize-1));
			ps.setLong(1, firstStateIndex);
			ResultSet rs = ps.executeQuery();
			//int c = 0;
			noResult = true;
			int i = 0;
			while (rs.next()) {
				noResult = false;
	//			System.out.print("State "+(++c)+"/???, ["+rs.getString("match_id")+"; "+rs.getInt("step_number")+"]: ");
				String xmlState = rs.getString("state");
				
				Pair<String, Long> rawState = null;
				
				if (xmlState.charAt(0) != '(') {
					rawState = xmlStateToRawState(xmlState);
					if(i % 1000 == 0) { System.out.println("."); }
					i++;
				} else {
					//System.out.println("  Already raw(0): "+xmlState);
					System.out.print("-"); System.out.flush();
				}
				
				if (rawState != null) {
	//				System.out.println(xmlState);
//					System.out.println("  " + rawState.getLeft());
					rs.updateString("state", rawState.getLeft());
					if(rawState.getRight()!=null){
						rs.updateTimestamp("timestamp", new Timestamp(rawState.getRight()));
					}
					// System.out.println(rawState.getLeft());
					rs.updateRow();
				}
			}
			rs.close();
		}
		ps.close();
		con.close();
		System.out.println("\ndone.");
		System.exit(0);
	}
	
	
	public static void main (String args[]) throws ParserConfigurationException, SAXException, IOException, SQLException, NamingException {
		
		//String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?> <?xml-stylesheet type=\"text/xsl\" href=\"../stylesheets/generic/generic.xsl\"?> <!DOCTYPE match SYSTEM \"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd\">  <match> <match-id>themathematician_easy.1267937366123</match-id> <role>mathematician</role> <player>FLUXPLAYER_TEST</player> <timestamp>1267937366177</timestamp> <startclock>85</startclock> <playclock>15</playclock>  <history/> <state> <fact> <prop-f>STEP</prop-f> <arg>0</arg> </fact> <fact> <prop-f>TERM</prop-f> <arg> <prop-f>OP</prop-f> <arg>*</arg> <arg>2</arg>  <arg>8</arg> </arg> </fact> </state> </match> ";
		//System.out.println(xmlStateToRawState(xml));
		
		xmlStatesToRawStates();
		
	}
	
	
	
}
