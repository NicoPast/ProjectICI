package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class PacManNearPPillTransition implements Transition {

	public int threshold = 30;
	
	public PacManNearPPillTransition() {
		super();
	}


	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.isPacManCloserToPowerPill() && input.getMinPacmanDistancePPill() < threshold;
	}


	@Override
	public String toString() {
		return "MsPacman near PPill";
	}
}
