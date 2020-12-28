package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.PositionAproximator;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToActiveGhostAction implements Action {
//La idea es de la última posición del resto de fantasmas (siempre que estemos relativamente seguros de que no están edibles) coger la más cercana
//siempre que sea lo suficientemente fiable y aproximar la posición del compañero que me va a proteger
	private MapaInfoGhost mymap;
	GHOST ghost;
	Vector<Float> ActiveGhostsPosAccuracies;
	Vector<MOVE> GhostsLastMoveKnown;
	Vector<Float> EdibleAccurracy;
	Vector<interseccion> LastGhostsKnownPositions;
	int PacmanLastPosKnown;
	float PacmanPosAccuracy;

	float PACMAN_POS_ACCURACY_LIMIT = .7f;
	float POS_ACCURACY_LIMIT = .7f;
	float EDIBLE_ACCURACY_LIMIT = .3f;

	public GoToActiveGhostAction(GHOST ghost, MapaInfoGhost map, Vector<Float> Actives, Vector<Float> edibles,
			Vector<interseccion> LastPos, int PacmanPos, float PacmanAccur, Vector<MOVE> lastMoves) {
		this.ghost = ghost;
		this.mymap = map;
		this.ActiveGhostsPosAccuracies = Actives;
		this.EdibleAccurracy = edibles;
		this.GhostsLastMoveKnown = lastMoves;
		this.LastGhostsKnownPositions = LastPos;
		this.PacmanLastPosKnown = PacmanPos;
		this.PacmanPosAccuracy = PacmanAccur;

	}

//	private EnumMap<GHOST,Float> activeGhosts(Game game) {
//		EnumMap<GHOST,Float> activeGhosts = new EnumMap<GHOST,Float>(GHOST.class);
//		for (GHOST ghost : GHOST.values()) {
//			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0)
//				activeGhosts.add(ghost);
//		}
//		return activeGhosts;
//	}

	private class DistanceAndGhost {
		int ghostIndex;
		double distance;

		public DistanceAndGhost(double d, int i) {
			ghostIndex = i;
			distance = d;
		}
	}

	private DistanceAndGhost nearestGhostDistance(MOVE m, Game game) {

		double nearestDist = Double.MAX_VALUE;
		// TENEMOS EN CUENTA LOS PARAMETROS FUZZY
		int index = 0;
		for (int i = 0; i < 4; i++) {
			if(GHOST.values()[i]==ghost)
				continue;
			////Si la seguridad de que el fantasma que buscamos es edible es baja es que no es edible muy probablemente, ya 
			//que esta variable fuzzy bajará muy rápidamente(el tiempo durante el que se está edible es corto)
			if (LastGhostsKnownPositions.elementAt(i).identificador > -1
					&& ActiveGhostsPosAccuracies.elementAt(i) >= POS_ACCURACY_LIMIT
					&& this.EdibleAccurracy.elementAt(i) < EDIBLE_ACCURACY_LIMIT) { 
				double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost),
						LastGhostsKnownPositions.elementAt(i).identificador, m, DM.PATH);
				if (aux < nearestDist) {
					nearestDist = aux;
					index = i;
				}
			}
		}
		return new DistanceAndGhost(nearestDist, index);
	}

	@Override
	public MOVE execute(Game game) {

		MOVE best = MOVE.NEUTRAL;
		interseccion inter = mymap.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
		double nearest = 0;
		if (inter != null) {
			MOVE prohibido = MOVE.NEUTRAL;
			int selectedGhostIndex = 0;
			int myPos = game.getGhostCurrentNodeIndex(ghost);
			MOVE mylastMove = game.getGhostLastMoveMade(ghost);
			if (PacmanLastPosKnown > -1)
				prohibido = game.getApproximateNextMoveTowardsTarget(myPos, PacmanLastPosKnown, mylastMove, DM.EUCLID);
			// elegimos el fantasma mï¿½s cercano buscando en todas direcciones excepto
			// en la prohibida
			for (MOVE move : inter.destinos.keySet()) {
				// si tenemos relativamente claro que el pacman está en esa dirección (la variable prohibido es suficientemente fiable) no
				// buscamos en esa dirección 
				if (prohibido != MOVE.NEUTRAL && move == prohibido
						&& this.PacmanPosAccuracy >= PACMAN_POS_ACCURACY_LIMIT)
					continue;
				DistanceAndGhost aux = nearestGhostDistance(move, game);
				if (aux.distance < nearest) {
					nearest = aux.distance;
					best = move;
					selectedGhostIndex = aux.ghostIndex;

				}
			}
			// si la búsqueda ha dado alún resultado significativo aproximamos la posición.
			// Si no da igual el movimiento
			// porque no tenemos información suficiente para decidir
			if (nearest < Double.MAX_VALUE) {
				PositionAproximator aprox = new PositionAproximator(mymap, game, myPos, mylastMove,
						this.LastGhostsKnownPositions.elementAt(selectedGhostIndex),
						GhostsLastMoveKnown.elementAt(selectedGhostIndex),
						ActiveGhostsPosAccuracies.elementAt(selectedGhostIndex));
				best = aprox.getBestMoveTowardsEntityPos();
			}
		}
		return best;

	}
}
