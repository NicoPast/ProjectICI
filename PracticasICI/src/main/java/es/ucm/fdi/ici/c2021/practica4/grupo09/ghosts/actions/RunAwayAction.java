package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayAction implements Action {

	GHOST ghost;
	MapaInfoGhost mymapa;
	Vector<Float> EdibleGhostsPosAccuracies;
	Vector<interseccion> LastGhostsKnownPositions;
	int PacmanLastPosKnown;
	float PacmanPosAccuracy;
	float PACMAN_POS_ACCURACY_LIMIT = .5f;
	float POS_ACCURACY_LIMIT = .7f;
	public RunAwayAction(GHOST ghost, MapaInfoGhost map,Vector<Float> Edibles,Vector<interseccion> LastPos, int PacmanPos, float PacmanAccur) {
		this.ghost = ghost;
		mymapa = map;
		this.EdibleGhostsPosAccuracies = Edibles;
		this.LastGhostsKnownPositions = LastPos;
		this.PacmanLastPosKnown = PacmanPos;
		this.PacmanPosAccuracy = PacmanAccur;
	}
	
//	private Vector<GHOST> edibleGhosts(Game game) {
//		Vector<GHOST> edibleGhosts = new Vector<GHOST>();
//		for (GHOST ghost : GHOST.values()) {
//			if (game.isGhostEdible(ghost))
//				edibleGhosts.add(ghost);
//		}
//		return edibleGhosts;
//	}

	private double furthestGhostDistance(MOVE m, Game game) {

		double furthest = -1;
		for (int i=0;i<4;i++) {
			if(GHOST.values()[i]==ghost)
				continue;
			
			if (LastGhostsKnownPositions.elementAt(i).identificador > -1
					&& EdibleGhostsPosAccuracies.elementAt(i) >= POS_ACCURACY_LIMIT
					) { 
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), p, m, DM.PATH);
			if (aux > furthest) {
				furthest = aux;
							}
		}

			}
		return furthest;
	}

	@Override
	public MOVE execute(Game game) {
		MOVE bestMove =MOVE.NEUTRAL;
		if (game.doesGhostRequireAction(ghost)) 
		{
			
			MOVE prohibido = MOVE.NEUTRAL;
			if(PacmanPosAccuracy > PACMAN_POS_ACCURACY_LIMIT) {
				
			prohibido=game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
					PacmanLastPosKnown, game.getGhostLastMoveMade(ghost),
					DM.EUCLID);
			bestMove = game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
					PacmanLastPosKnown, game.getGhostLastMoveMade(ghost),DM.EUCLID);
			}
			interseccion inter = mymapa.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
			if (inter != null) {
				
				// buscamos el fantasma mas lejano en todas direcciones excepto
				// la prohibida y vamos a por ese
				double furthest = 0;
				for (MOVE move : inter.destinos.keySet()) {
					if (move == prohibido)
						continue;

					double aux = furthestGhostDistance(move, game);
					if (aux > furthest) {
						furthest = aux;
						if(furthest>-1)
						bestMove = move;
					}
				}
			}
		} 
		return bestMove;

	}
}
