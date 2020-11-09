package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions;

import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class RunAwayAction implements Action{

	
	@Override
	public MOVE execute(Game game) {
		return MOVE.NEUTRAL;
	}
}
