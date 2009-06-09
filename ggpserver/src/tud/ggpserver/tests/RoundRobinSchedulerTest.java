/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.tests;

import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;

import tud.ggpserver.scheduler.AbstractRoundRobinScheduler;
import tud.ggpserver.scheduler.RoundRobinScheduler;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class RoundRobinSchedulerTest {
	@Test
	public void testStartIsrunningStop() throws InterruptedException, RemoteException, NamingException {
		setupJNDI();
		
		AbstractRoundRobinScheduler<?, ?> scheduler = RoundRobinScheduler.getInstance();
		assertTrue(!scheduler.isRunning());
		scheduler.start();
		assertTrue(scheduler.isRunning());
		Thread.sleep(600000);
		scheduler.stop();
		assertTrue(!scheduler.isRunning());
	}
	
	/**
	 * This is only necessary when running the program directly, e.g. as a JUnit
	 * test. When run as a WebApp, Tomcat takes care of this.
	 */
	public static void setupJNDI() throws NamingException, RemoteException {
		startRegistry();
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser("ggpserver");
		dataSource.setPassword("UnvdhZcyGPYFKUJZ");
		dataSource.setServerName("localhost");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("ggpserver");
		dataSource.setAutoReconnect(true);

		InitialContext context = createContext();
		context.rebind("java:comp/env/jdbc/ggpserver", dataSource);
	}
	
	private static void startRegistry() throws RemoteException {
		LocateRegistry.createRegistry(1099);
		System.out.println("RMI registry ready.");
	}

	private static InitialContext createContext() throws NamingException {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.rmi.registry.RegistryContextFactory");
		env.put(Context.PROVIDER_URL, "rmi://localhost:1099");
		InitialContext context = new InitialContext(env);
		
		Set<Entry<Object, Object>> entrySet = env.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			System.setProperty((String) entry.getKey(), (String) entry.getValue());
		}

		return context;
	}
}
