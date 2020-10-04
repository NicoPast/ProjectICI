package es.ucm.ici.c2021.practica0.grupo09;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class MsPacMan extends PacmanController {
	@Override
	public MOVE getMove(Game game, long timeDue) {

        int limit = 20;

        GHOST nearestGhost = getClosestGhost(game, DM.EUCLID, limit, false);
		if(nearestGhost != null){
            return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(nearestGhost), game.getPacmanLastMoveMade(), DM.EUCLID);
        }

        nearestGhost = getClosestGhost(game, DM.EUCLID, limit, true);
        if(nearestGhost != null){
            return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(nearestGhost), game.getPacmanLastMoveMade(), DM.EUCLID);
        }

        return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), getClosestPill(game, DM.EUCLID), game.getPacmanLastMoveMade(), DM.EUCLID);
    }

    private GHOST getClosestGhost(Game game, DM measure, int limit, boolean edible) {
        GHOST closestGhost = null;
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (GHOST currentGhost : GHOST.values()) {
            if(edible == game.isGhostEdible(currentGhost)){ //xnor, que es igual a (edible && game.isGhostEdible(currentGhost) || !edible && !game.isGhostEdible(currentGhost))
                double aux = game.getDistance(pacmanPos, game.getGhostCurrentNodeIndex(currentGhost), measure);
                if (aux < closestDistance && aux < limit) {
                    closestGhost = currentGhost;
                    closestDistance = aux;
                }
            }
        }
        return closestGhost;
    }

    private int getClosestPill(Game game, DM measure) {
        int closestPill = -1;
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (int currentPill : game.getActivePillsIndices()) {
            double aux = game.getDistance(pacmanPos, currentPill, measure);
            if (aux < closestDistance) {
                closestPill = currentPill;
                closestDistance = aux;
            }
        }
        return closestPill;
    }
}