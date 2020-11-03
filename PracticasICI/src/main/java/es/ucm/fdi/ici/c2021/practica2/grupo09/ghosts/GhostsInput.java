package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts;

import es.ucm.fdi.ici.c2021.practica2.grupo09.GhostsFSM;
import es.ucm.fdi.ici.c2021.practica2.grupo09.GhostsFSM.interseccion;
import es.ucm.fdi.ici.fsm.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsInput extends Input {

	private GhostsFSM myFsm;

	private double minPacmanDistancePPill;
	private interseccion proximaInterseccionPacMan;
	
	public GhostsInput(Game game, GhostsFSM ghostsFsm) {
		super(game);
		myFsm = ghostsFsm;
	}

	@Override
	public void parseInput() {
		int pacman = game.getPacmanCurrentNodeIndex();
		this.minPacmanDistancePPill = Double.MAX_VALUE;
		for(int ppill: game.getPowerPillIndices()) {
			double distance = game.getDistance(pacman, ppill, DM.PATH);
			this.minPacmanDistancePPill = Math.min(distance, this.minPacmanDistancePPill);
		}

		if(myFsm.getCheckLastModeMade()) proximaInterseccionPacMan = myFsm.getInterseccionActual();
		else proximaInterseccionPacMan = myFsm.getInterseccion(myFsm.getInterseccionActual().destinos.get(myFsm.getUltimoMovReal()));
	}

	public double getMinPacmanDistancePPill() {
		return minPacmanDistancePPill;
	}

	public interseccion getProximaInterseccionPacMan() { 
		return proximaInterseccionPacMan;
	}
	
	
}
