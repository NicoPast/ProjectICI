package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PrisonerAction implements Action {

	public PrisonerAction() {
		
	}

	@Override
	public MOVE execute(Game game) {
		//si estoy muerto no puedo hacer nada
		return MOVE.NEUTRAL;
	}

}
