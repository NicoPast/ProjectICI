package es.ucm.fdi.ici.c2021.practica0.grupo09;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class Ghosts extends GhostController {
    private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
    private MOVE[] allMoves = MOVE.values();
    private Random rnd = new Random();

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
    	
        moves.clear();
        for (GHOST ghostType : GHOST.values()) {
            if (game.doesGhostRequireAction(ghostType)) {
                moves.put(ghostType, getGhostMove(game, timeDue, ghostType));
            }
        }
        return moves;
    }

    private MOVE getGhostMove(Game game, long timeDue, GHOST ghostType) {
        if (game.isGhostEdible(ghostType) || isPacManCloseToPowerPill(game, 20)) {
            return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghostType),
                    game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), DM.EUCLID);
        }

        float rng = rnd.nextFloat();

        if (rng < 0.9f)
            return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType),
                    game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), DM.EUCLID);
        else
            return allMoves[rnd.nextInt(allMoves.length)];
    }

    private boolean isPacManCloseToPowerPill(Game game, int limit) {
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (int currentPill : game.getActivePowerPillsIndices()) {
            double aux = game.getDistance(pacmanPos, currentPill, DM.EUCLID);
            if (aux < closestDistance) {
                closestDistance = aux;
            }
        }
        return closestDistance < limit;
    }
}
