package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.GhostsFSM;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayAction implements Action {

	GHOST ghost;
	MapaInfo mymapa;

	public RunAwayAction(GHOST ghost, MapaInfo map) {
		this.ghost = ghost;
		mymapa= map;
	}

	private Vector<GHOST> edibleGhosts(Game game) {
		Vector<GHOST> edibleGhosts = new Vector<GHOST>();
		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost))
				edibleGhosts.add(ghost);
		}
		return edibleGhosts;
	}

	private double furthestGhostDistance(int[] pos, MOVE m, Game game) {

		double furthestDist = 0;
		int fpos = 0;
		for (int p : pos) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), p, m, /* constant dm */DM.EUCLID);
			if (aux > furthestDist) {
				furthestDist = aux;
				fpos = p;
			}
		}

		return furthestDist;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost)) // if it requires an action
		{
			MOVE prohibido = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
					game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost),
					/* CONSTANT_DIRECTION_MEASURE */DM.EUCLID);
			MOVE bestMove = game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
					game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost),
					/* CONSTANT_DIRECTION_MEASURE */DM.EUCLID);
			interseccion inter = mymapa.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
			if (inter != null) {
				int[] posGhosts = new int[3];
				int i = 0;
				i = 0;
				// rellenamos las posiciones de los fantasmas que se pueden comer para huir de
				// ellos
				for (GHOST g : edibleGhosts(game)) {
					if (g == ghost)
						continue;
					posGhosts[i] = game.getGhostCurrentNodeIndex(g);
					i++;
				}
				//aqu� no se usa move para nada. En cada iteraci�n del bucle el fantasma m�s cercano
				//va a ser el mismo y por tanto furthest solo va a cambiar la primera vez
				double furthest = 0;
				for (MOVE move : inter.destinos.keySet()) {
					if (move == prohibido)
						continue;
					double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost),
							game.getClosestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(ghost), posGhosts,
									DM.EUCLID),
							DM.EUCLID);
					if (aux > furthest) {
						furthest = aux;
						bestMove = move;
					}
				}
			}
//
//			// recorremos los posibles movimientos que no sean el prohibido en la
//			// interseccion
//			// actual y elegimos el movimiento que nos lleve al fantasma m�s lejano
//			boolean AllIntersectionsOccuped = true;
//
//			// buscamos la primera interseccion libre de otros fantasmas en
//			// todas las direcciones posibles excepto la prohibida
//			for (MOVE move : inter.destinos.keySet()) {
//
//				if (move == prohibido)
//					continue;
//				for (int p : posGhosts)
//					if (inter.destinos.get(move) != p) {
//						bestMove = move;
//						AllIntersectionsOccuped = false;
//					}
//			}
//			// si ya hay un fantasma en cada una de las otras intersecciones
//			// (poco probable) elegimos el movimiento que nos llevar�a al m�s lejano
//			if (AllIntersectionsOccuped) {
//				double furthest = 0;
//				for (MOVE move : inter.destinos.keySet()) {
//
//					if (move == prohibido)
//						continue;
//
//					double aux = furthestGhostDistance(posGhosts, move, game);
//					if (aux > furthest) {
//						furthest = aux;
//						bestMove = move;
//					}
//				}
//			}
			return bestMove;
		} else
			return MOVE.NEUTRAL;

	}
}
