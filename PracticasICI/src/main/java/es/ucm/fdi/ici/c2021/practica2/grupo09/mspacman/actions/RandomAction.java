package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions;

import java.util.Random;

import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RandomAction implements Action {

	public RandomAction() {
		// TODO Auto-generated constructor stub
	}

    private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
	
	@Override
	public MOVE execute(Game game) {
		return allMoves[rnd.nextInt(allMoves.length)];
	}

}
