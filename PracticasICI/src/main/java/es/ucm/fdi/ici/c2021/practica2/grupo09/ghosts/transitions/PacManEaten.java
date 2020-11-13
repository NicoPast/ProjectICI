package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput.ClosestPowerPillAndDistance;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class PacManEaten implements Transition {

	GHOST ghost;
	public PacManEaten(GHOST ghost) {
		super();
		this.ghost = ghost;
	}
	
	//Si o bien ya no puedo asegurar la powerpill o no es rentable
	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
					
		return input.pacManEaten;
	}
	@Override
	public String toString() {
		return ghost.name()+" is now attacking";
	}
}
