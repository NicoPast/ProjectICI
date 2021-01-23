package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.List;
import java.util.Random;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsAction {
	
	Game game;
	GHOST ghostType;
	
	public GhostsAction() {
		
	}
	
	public void setGameAndGhost(Game game, GHOST ghost) {
		this.game = game;
		this.ghostType = ghost;
	}
	
	/**
	 * Method called when the CBREngine is not able to find a suitable action. 
	 * Simplest implementation returns a random one.
	 * @return
	 */
	public MOVE defaultAction() {
		int pacmanPos = game.getPacmanCurrentNodeIndex();
		int ghostPos = game.getGhostCurrentNodeIndex(ghostType);
		if(game.isGhostEdible(ghostType) || //Es edible o  el pacman esta cerca de una powerpill
			game.getEuclideanDistance(pacmanPos, game.getClosestNodeIndexFromNodeIndex(pacmanPos, game.getActivePowerPillsIndices(), DM.EUCLID)) < 30){
				return game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, game.getGhostLastMoveMade(ghostType), DM.EUCLID);
		}
		return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, game.getGhostLastMoveMade(ghostType), DM.EUCLID);
	}

	public MOVE findAnotherMove(MOVE wrongMove){
		int length = 4;
		int randomIndex = new Random().nextInt(length);
		MOVE other = MOVE.values()[randomIndex];
		if(other.equals(wrongMove))
			randomIndex = (randomIndex+1) % length;
		return MOVE.values()[randomIndex];
	}
}
