package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class PacManNearPPillTransition implements Transition {

	int threshold = 25;
	int closeThreshold = 25;
	GHOST ghost;
	
	public PacManNearPPillTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		//Que el pacman este mas cerca que nadie de una ppill, que esté lo suficientemente cerca de ella y que yo este cerca del pacman
		return input.isPacManCloserToPowerPill() && input.getMinPacmanDistancePPill() < threshold && input.getDistanceToPacMan(ghost) < closeThreshold;
	}

	@Override
	public String toString() {
		return "MsPacman near PPill";
	}
}
