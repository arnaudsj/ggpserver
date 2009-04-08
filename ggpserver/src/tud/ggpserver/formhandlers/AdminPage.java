package tud.ggpserver.formhandlers;

import tud.ggpserver.scheduler.RoundRobinScheduler;

public class AdminPage {

	public boolean isRunning() {
		return RoundRobinScheduler.getInstance().isRunning();
	}
	
	public void setAction(String action) {
		if (action.equals("start")) {
			RoundRobinScheduler.getInstance().start();
		} else if (action.equals("stop")) {
			RoundRobinScheduler.getInstance().stop();
		}
	}
}
