package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.List;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsAction {
	
	Game game;
	
	public GhostsAction(List<Action> actions) {
		
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	/**
	 * Method called when the CBREngine is not able to find a suitable action. 
	 * Simplest implementation returns a random one.
	 * @return
	 */
	public MOVE defaultAction() {

		
		return MOVE.NEUTRAL;
	}

	public MOVE findAnotherMove(MOVE wrongMove){



		return MOVE.NEUTRAL;
	}
}
