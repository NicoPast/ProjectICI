package es.ucm.fdi.ici.c2021.practica0.grupo09;

import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {
	
	public static void main(String[] args) {
		Executor executor = new Executor.Builder()
		.setTickLimit(400000000)
		.setTimeLimit(400000000)
		.setVisual(true)
		.setScaleFactor(3.0)		
		.build();
		
		PacmanController pacMan = new MsPacMan();
		GhostController ghosts = new Ghosts();
		System.out.println(
		executor.runGame(pacMan, ghosts, 50)
		);
		
	}
}

