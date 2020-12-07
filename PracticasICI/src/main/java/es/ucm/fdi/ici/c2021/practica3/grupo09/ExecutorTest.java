package es.ucm.fdi.ici.c2021.practica3.grupo09;

import es.ucm.fdi.ici.c2021.practica3.grupo09.demorules.GhostsRules;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {
	
	public static void main(String[] args) {
		Executor executor = new Executor.Builder()
		.setTickLimit(4000)
		.setTimeLimit(40)
		.setVisual(true)
		.setScaleFactor(3.0)
		.build();
		
		
		PacmanController pacMan = new MsPacMan();
		GhostController ghosts = new GhostsRules();
		System.out.println(
				executor.runGame(pacMan, ghosts, 30)
		);
		
	}
}

