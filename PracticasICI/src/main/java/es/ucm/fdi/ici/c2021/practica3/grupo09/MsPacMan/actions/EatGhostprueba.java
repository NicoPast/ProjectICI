package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan.actions;

import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EatGhostprueba implements Action {
	
	
	
	@Override
	public MOVE execute(Game game) {  
		System.out.println("EEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAATTTTTTTTTTTTTTTTTT");
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(fantasmaComibleCerca(game)),
				game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
	}
	
	GHOST fantasmaComibleCerca(Game game) {
		GHOST fantasma =  null;
		double distancia = Double.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
			
			if (game.isGhostEdible(g)) { //la distancia pacMan-ghost(comible) > ghost(comible)-ghost(!combible)
				double distAux = game.getDistance(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(g), DM.EUCLID);
				
				
				if (distAux < distancia) { //fantasma comible en rango
					fantasma = g;
					distancia = distAux;
				}
			}
		}
		return fantasma;
	}
}
