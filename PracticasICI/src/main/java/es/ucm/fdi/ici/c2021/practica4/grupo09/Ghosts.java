package es.ucm.fdi.ici.c2021.practica4.grupo09;

import pacman.controllers.POGhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends POGhostController {

	MapaInfoGhost mapInfo;

	private static final String RULES_PATH = "es/ucm/fdi/ici/c2021/practica4/grupo09/ghosts/rules";
	
	public Ghosts() {
		
		
	}

	@Override
	public MOVE getMove(GHOST ghost, Game game, long timeDue) {
		
		return null;
	}
}
