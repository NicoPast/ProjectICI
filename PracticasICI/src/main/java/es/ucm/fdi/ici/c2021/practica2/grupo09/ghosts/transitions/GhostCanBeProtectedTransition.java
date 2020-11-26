package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostCanBeProtectedTransition implements Transition {

	GHOST ghost;
	MapaInfoGhost mymap;
	double CONST_LIMIT_DISTANCE = 25;

	public GhostCanBeProtectedTransition(GHOST ghost, MapaInfoGhost map) {
		super();
		this.ghost = ghost;
		this.mymap = map;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
		Game g = input.getGame();

		Vector<GHOST> actives = input.getActiveGhosts();
		// si no hay fantasmas activos hay que seguir huyendo
		if (!actives.isEmpty()) {
			interseccion inter = mymap.getInterseccion(g.getGhostCurrentNodeIndex(ghost));

			if (inter != null) {
				int[] posGhosts = new int[3];
				int i = 0;
				i = 0;
				// rellenamos las posiciones de los fantasmas activos para ver a que distancia
				// esta el mas cercano
				for (GHOST gh : actives) {
					if (gh == ghost)
						continue;
					posGhosts[i] = g.getGhostCurrentNodeIndex(gh);
					i++;
				}
				
				MOVE prohibido = input.GetMoveToPacman(ghost);
				double nearest = 0;
				//elegimos el fantasma mas cercano buscando en todas direcciones excepto
				//en la prohibida
				for (MOVE move : inter.destinos.keySet()) {
					if (move == prohibido)
						continue;
					double aux = input.nearestGhostDistance(g.getGhostCurrentNodeIndex(ghost),posGhosts, move).d;
					if (aux < nearest) {
						nearest = aux;

					}
				}
				// si hay algun fantasma activo lo suficientemente cerca, hay que perseguirlo
				// para que si viene el pacman el fantasma lo mate
				return nearest < CONST_LIMIT_DISTANCE;

			}
			// si no hay interseccion estoy en un pasillo y se sigue con el movimiento
			// actual.
			else
				return false;
		} else
			return false;

	}

	public String toString() {
		return ghost.name() + " can be protected";
	}
}
