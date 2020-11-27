package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostRespawnedTransition implements Transition {

	GHOST ghost;
	public GhostRespawnedTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		return in.getGame().getGhostLairTime(ghost) <= 0;
	}
	
	@Override
	public String toString() {
		return ghost.name() + " respawned";
	}
}
