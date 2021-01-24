package es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions;


import java.util.Random;

import es.ucm.fdi.ici.c2021.practica5.grupo09.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPPillAction implements Action {
    
	private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
	public GoToPPillAction() {
	}
	
	@Override
	public MOVE execute(Game game) {
		return allMoves[rnd.nextInt(allMoves.length)];
    }

	@Override
	public String getActionId() {
		return "Go to PPill";
	}
            
}