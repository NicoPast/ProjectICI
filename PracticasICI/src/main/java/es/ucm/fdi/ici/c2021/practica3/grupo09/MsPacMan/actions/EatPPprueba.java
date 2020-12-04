package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan.actions;

import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

public class EatPPprueba implements Action {
	
	
	
	@Override
	public MOVE execute(Game game) {    
		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPIIIIIIIIIIIIIIIIIIIIIILLLLLLLLLLLLLLLLL");    
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				getPowerPillCercana(game), game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
	}
	
	private int getPowerPillCercana(Game game) {
        int closestPowerPill = -1;
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (int currentPill : game.getActivePowerPillsIndices()) {
            double aux = game.getDistance(pacmanPos, currentPill, DM.PATH);
            if (aux < closestDistance) {
                closestPowerPill = currentPill;
                closestDistance = aux;
            }
        }
        return closestPowerPill;
	}
}
