package tud.ggpserver.datamodel;


public class DBConnectorFactory {
	public static AbstractDBConnector getDBConnector() {
		return DBConnector.getInstance();
	}
}
