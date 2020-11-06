package es.ucm.fdi.ici.fsm;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * An action to be executed in a state.
 * @author Juan Ant. Recio García - Universidad Complutense de Madrid
 */
public interface Action {
		
	/**
	 * Executes the action according to the game and returns the following move.
	 */
	public MOVE execute(Game game);
}
