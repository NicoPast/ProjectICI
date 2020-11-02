package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostFarFromActiveGhostTransition implements Transition {

	GHOST ghost;
	public GhostFarFromActiveGhostTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
			}
	@Override
	public boolean evaluate(Input in) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String toString() {
		return ghost.name()+" is not near any active ghost";
	}
}
