package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions;

import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PrisonerAction implements Action {

	public PrisonerAction() {
		
	}
	
	public void parseFact(Fact actionFact){
		
	}

	@Override
	public MOVE execute(Game game) {
		//si estoy muerto no puedo hacer nada
		return MOVE.NEUTRAL;
	}

}
