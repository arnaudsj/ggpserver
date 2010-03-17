package tud.ggpserver.util;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

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

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.tests.RoundRobinSchedulerTest;


public class XMLStatesToRawStates {
	
	public static final String dtd = "<!DOCTYPE match SYSTEM \"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd\">";
	
	
	public static String xmlStateToRawState (String xml) throws ParserConfigurationException, SAXException, IOException {
		
		xml = xml.replace(dtd, "").replaceAll("\n", " ");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		try {
			Document xmldoc = builder.parse(new InputSource(new StringReader(xml)));
			
			NodeList nodes = xmldoc.getElementsByTagName("fact");
			String result = "(";
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				result += getText(node);
			}
			
			return result+")";
		}
		catch (SAXParseException saxpe) { // probably, this state is already a raw state
//			System.out.println("Already raw(1): "+xml);
			return null;
		}
		
	}
	
	
	public static String getText (Node n) {
		
//		System.out.println(n.getClass().getCanonicalName() + ":" + n);
		
		if(n.getNodeName().equals("fact") || n.getNodeName().equals("prop-f") || n.getNodeName().equals("arg") ) {
			
			if (! n.hasChildNodes() || n.getChildNodes().getLength() == 1) {
//				System.out.println("<"+n.getTextContent().trim()+">");
				return n.getTextContent().trim();
			} else {
//				System.out.println("\thas children");
				String str = "(";
				NodeList children = n.getChildNodes();
				boolean firstNonEmpty = true;
				for (int i = 0; i < children.getLength(); i++) {
					String childString = getText(children.item(i));
					if (!childString.equals("")) {
						if (firstNonEmpty) {
							firstNonEmpty = false;
						}else{
							str += " ";
						}
						str += childString;
					}
				}
				return str+")";
			}
			
		} else {
			
			return "";
			
		}
		
	}
	
	
	public static void xmlStatesToRawStates () throws SQLException, ParserConfigurationException, SAXException, IOException, NamingException {
		
		RoundRobinSchedulerTest.setupJNDI();
		AbstractDBConnector<?, ?> db = getDBConnector();
		
		ResultSet rs = db.getAllStates();
		//int c = 0;
		while (rs.next()) {
			
//			System.out.print("State "+(++c)+"/???, ["+rs.getString("match_id")+"; "+rs.getInt("step_number")+"]: ");
			String xmlState = rs.getString("state");
			
			String rawState = null;
			
			if (xmlState.charAt(0) != '(') {
				rawState = xmlStateToRawState(xmlState);
			} else {
//				System.out.println("Already raw(0): "+xmlState);
			}
			
			if (rawState != null) {
//				System.out.println(xmlState);
				System.out.println(rawState+"\n");
				rs.updateString("state", rawState);
				rs.updateRow();
			}
			//System.exit(0);
			
		}
		rs.close();
	}
	
	
	public static void main (String args[]) throws ParserConfigurationException, SAXException, IOException, SQLException, NamingException {
		
		//String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?> <?xml-stylesheet type=\"text/xsl\" href=\"../stylesheets/generic/generic.xsl\"?> <!DOCTYPE match SYSTEM \"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd\">  <match> <match-id>themathematician_easy.1267937366123</match-id> <role>mathematician</role> <player>FLUXPLAYER_TEST</player> <timestamp>1267937366177</timestamp> <startclock>85</startclock> <playclock>15</playclock>  <history/> <state> <fact> <prop-f>STEP</prop-f> <arg>0</arg> </fact> <fact> <prop-f>TERM</prop-f> <arg> <prop-f>OP</prop-f> <arg>*</arg> <arg>2</arg>  <arg>8</arg> </arg> </fact> </state> </match> ";
		//System.out.println(xmlStateToRawState(xml));
		
		xmlStatesToRawStates();
		
	}
	
	
	
}
