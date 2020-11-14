package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToActiveGhostAction implements Action {

	private MapaInfo mymap;
	GHOST ghost;

	public GoToActiveGhostAction(GHOST ghost, MapaInfo map) {
		this.ghost = ghost;
		this.mymap = map;
	}

	private Vector<GHOST> activeGhosts(Game game) {
		Vector<GHOST> activeGhosts = new Vector<GHOST>();
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0)
				activeGhosts.add(ghost);
		}
		return activeGhosts;
	}

	private double nearestGhostDistance(int[] pos, MOVE m, Game game) {

		int nearestp = -1;
		double nearestDist = Double.MAX_VALUE;
		for (int p : pos) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), p, m, /* constant dm */DM.PATH);
			if (aux < nearestDist) {
				nearestDist = aux;
				nearestp = p;
			}
		}

		return nearestDist;
	}

	@Override
	public MOVE execute(Game game) {

		//No hace falta comprobar que el vector de activos está vacío ya que si estamos en 
		//este estado existe alguno que está a la distancia suficiente para perseguirlo
		Vector<GHOST> actives = activeGhosts(game);
		MOVE best = MOVE.NEUTRAL;
		interseccion inter = mymap.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
		if (inter != null) {
			int[] posGhosts = new int[3];
			int i = 0;
			i = 0;
			// rellenamos las posiciones de los fantasmas activos para ver a qué distancia
			// esta el más cercano y elegirlo a él para perseguirlo
			for (GHOST gh : actives) {
				if (gh == ghost)
					continue;
				posGhosts[i] = game.getGhostCurrentNodeIndex(gh);
				i++;
			}
			MOVE prohibido = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
					game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost),
					/* CONSTANT_DIRECTION_MEASURE */DM.EUCLID);
			double nearest = 0;
			// elegimos el fantasma más cercano buscando en todas direcciones excepto
			// en la prohibida
			for (MOVE move : inter.destinos.keySet()) {
				if (move == prohibido)
					continue;
				double aux = nearestGhostDistance(posGhosts, move, game);
				if (aux < nearest) {
					nearest = aux;
					best = move;

				}
			}
		}
		return best;

	}
}
