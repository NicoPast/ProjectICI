package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;
//or pacman is near from powerPill
public class GhostsWeakTransition implements Transition  {

	GHOST ghost;
	public GhostsWeakTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}



	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
		PacManNearPPillTransition near = new PacManNearPPillTransition();
		return input.getGame().isGhostEdible(ghost) || near.evaluate(in);
	}



	@Override
	public String toString() {
		return ghost.name()+" is edible";
	}

	
	
}
