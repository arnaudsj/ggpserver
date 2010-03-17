package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class Tests {
	
	public static void main (String[] args) throws SQLException, NamingException {
		
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser("ggpserver");
		dataSource.setPassword("UnvdhZcyGPYFKUJZ");
		dataSource.setServerName("localhost");
		dataSource.setPort(8888);
		dataSource.setDatabaseName("ggpserver");
		dataSource.setAutoReconnect(true);

		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.rmi.registry.RegistryContextFactory");
		env.put(Context.PROVIDER_URL, "rmi://localhost:1099");
		InitialContext context = new InitialContext(env);
		
		Set<Entry<Object, Object>> entrySet = env.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			System.setProperty((String) entry.getKey(), (String) entry.getValue());
		}
		
		//context.rebind("java:comp/env/jdbc/ggpserver", dataSource);
		
		ViewState vs = new ViewState();
		vs.setMatchID("tictactoe.1268301677179");
		vs.setStepNumber(1);
		vs.setRole("xplayer");
		
		String str = vs.getXmlState();
		System.out.println(str);
		
	}
	
}
