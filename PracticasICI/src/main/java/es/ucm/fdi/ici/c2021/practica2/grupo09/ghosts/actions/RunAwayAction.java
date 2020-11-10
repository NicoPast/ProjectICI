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
		mymapa = map;
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

		double furthest = 0;
		for (int p : pos) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), p, m, /* constant dm */DM.EUCLID);
			if (aux > furthest) {
				furthest = aux;
							}
		}

		return furthest;
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
				// buscamos el fantasma m�s lejano en todas direcciones excepto
				// la prohibida y vamos a por ese
				double furthest = 0;
				for (MOVE move : inter.destinos.keySet()) {
					if (move == prohibido)
						continue;

					double aux = furthestGhostDistance(posGhosts, move, game);
					if (aux > furthest) {
						furthest = aux;
						bestMove = move;
					}
				}
			}
			return bestMove;
		} else
			return MOVE.NEUTRAL;

	}
}
