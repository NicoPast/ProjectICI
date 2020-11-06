package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostFarFromActiveGhostTransition implements Transition {

	GHOST ghost;
	MapaInfo mymap;
	double CONST_LIMIT_DISTANCE = 15;

	public GhostFarFromActiveGhostTransition(GHOST ghost, MapaInfo map) {
		super();
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

		double nearestDist = Double.MAX_VALUE;
		for (int p : pos) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), p, m, /* constant dm */DM.EUCLID);
			if (aux < nearestDist) {
				nearestDist = aux;
			}
		}

		return nearestDist;
	}

	@Override
	public boolean evaluate(Input in) {
		in.parseInput();
		Game g = in.getGame();

		Vector<GHOST> actives = activeGhosts(g);
		// si no hay fantasmas activos hay que cambiar a huir
		if (!actives.isEmpty()) {
			interseccion inter = mymap.getInterseccion(g.getGhostCurrentNodeIndex(ghost));

			if (inter != null) {
				int[] posGhosts = new int[3];
				int i = 0;
				i = 0;
				// rellenamos las posiciones de los fantasmas activos para ver a qué distancia
				// esta el más cercano
				for (GHOST gh : actives) {
					if (gh == ghost)
						continue;
					posGhosts[i] = g.getGhostCurrentNodeIndex(gh);
					i++;
				}
				MOVE prohibido = g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost),
						g.getPacmanCurrentNodeIndex(), g.getGhostLastMoveMade(ghost),
						/* CONSTANT_DIRECTION_MEASURE */DM.EUCLID);
				double nearest = 0;
				for (MOVE move : inter.destinos.keySet()) {
					if (move == prohibido)
						continue;
					double aux = nearestGhostDistance(posGhosts, move, g);
					if (aux < nearest) {
						nearest = aux;

					}
				}
				// si el fantasma activo más cercano (mirando en todas direcciones menos
				// en la prohibida) está muy lejos hay que huir de todo
				return nearest > CONST_LIMIT_DISTANCE;

			}
			//si no hay interseccion estoy en un pasillo y se sigue con el movimiento actual.
			else return false;
		} else
			return true;

	}

	@Override
	public String toString() {
		return ghost.name() + " is not near any active ghost";
	}
}
