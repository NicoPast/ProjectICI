package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostDiedTransition implements Transition {

	GHOST ghost;
	public GhostDiedTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
			}
	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
		return input.getGame().wasGhostEaten(ghost);
	}
	@Override
	public String toString() {
		return ghost.name()+" died";
	}
}
