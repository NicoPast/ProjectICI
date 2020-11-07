package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.GhostsFSM;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;
//or pacman is near from powerPill
public class GhostsEdibleTransition implements Transition  {

	GHOST ghost;
	public GhostsEdibleTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}



	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
		input.parseInput();
		return input.getGame().isGhostEdible(ghost);
	}



	@Override
	public String toString() {
		return ghost.name()+" is edible";
	}

	
	
}
