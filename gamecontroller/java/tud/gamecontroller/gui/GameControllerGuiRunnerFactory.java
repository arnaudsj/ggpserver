package tud.gamecontroller.gui;

import java.io.File;

public class GameControllerGuiRunnerFactory {

	public static AbstractGameControllerGuiRunner<?, ?> createGameControllerGuiRunner(File gameFile){
		return new tud.gamecontroller.game.javaprover.GameControllerGuiRunner(gameFile);
	}
	
}
