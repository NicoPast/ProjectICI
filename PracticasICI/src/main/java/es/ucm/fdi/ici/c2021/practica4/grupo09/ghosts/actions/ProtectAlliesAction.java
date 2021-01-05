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

public class ProtectAlliesAction implements Action {
	// La idea es de la �ltima posici�n del resto de fantasmas (siempre que estemos
	// relativamente seguros de que est�n edibles) coger la m�s cercana
	// al pacman (siempre que sea lo suficientemente fiable tanto la posici�n del
	// pacman como la de los fantasmas)
	// y aproximar la posici�n del compa�ero que voy a proteger
	private MapaInfoGhost mymap;
	GHOST ghost;
	Vector<Double> EdibleGhostsPosAccuracies;
	Vector<MOVE> GhostsLastMoveKnown;
	Vector<Double> EdibleAccurracy;
	Vector<interseccion> LastGhostsKnownPositions;

	float PACMAN_POS_ACCURACY_LIMIT = .5f;
	float POS_ACCURACY_LIMIT = .7f;
	float EDIBLE_ACCURACY_LIMIT = .5f;

	public ProtectAlliesAction(GHOST ghost, MapaInfoGhost map, Vector<Double> Edibles, Vector<Double> edibles,
			Vector<interseccion> LastPos, Vector<MOVE> lastMoves) {
		this.ghost = ghost;
		this.mymap = map;
		this.EdibleGhostsPosAccuracies = Edibles;
		this.EdibleAccurracy = edibles;
		this.GhostsLastMoveKnown = lastMoves;
		this.LastGhostsKnownPositions = LastPos;

	}

//	private Vector<GHOST> EdibleGhosts(Game game) {
//		Vector<GHOST> edibleGhosts = new Vector<GHOST>();
//		for (GHOST ghost : GHOST.values()) {
//			if (game.isGhostEdible(ghost))
//				edibleGhosts.add(ghost);
//		}
//		return edibleGhosts;
//	}

	public int nearestGhostDistance(Game game, int myPos, MOVE m) {

		int nearestP = -1;
		double nearestDist = Double.MAX_VALUE;
		for (int i = 0; i < 4; i++) {
			if (GHOST.values()[i] == ghost)
				continue;
			//// Si la seguridad de que el fantasma que buscamos es edible es baja es que no
			//// es edible muy probablemente, ya
			// que esta variable fuzzy bajar� muy r�pidamente(el tiempo durante el que se
			//// est� edible es corto)
			if (LastGhostsKnownPositions.elementAt(i) != null
					&& EdibleGhostsPosAccuracies.elementAt(i) >= POS_ACCURACY_LIMIT
					&& EdibleAccurracy.elementAt(i) > EDIBLE_ACCURACY_LIMIT) {

				double aux = game.getDistance(myPos, LastGhostsKnownPositions.elementAt(i).identificador, m, DM.PATH);

				if (aux < nearestDist) {
					nearestDist = aux;
					nearestP = LastGhostsKnownPositions.elementAt(i).identificador;
				}
			}
		}
		return nearestP;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost) && PacmanPosAccuracy >= PACMAN_POS_ACCURACY_LIMIT) {
			// vemos cu�l es la distancia al fantasma edible m�s cercano a mi en todas
			// direcciones
			MOVE prohibido = game.getGhostLastMoveMade(ghost);
			int nearest = -1;
			for (MOVE m : MOVE.values()) {
				if (m == prohibido)
					continue;
				nearest = nearestGhostDistance(game, game.getGhostCurrentNodeIndex(ghost), m);
			}
			// Si la b�squeda ha sido infructuosa
			if (nearest == -1)
				return MOVE.NEUTRAL;
			// si hemos encontrado un fantasma en apuros con suficiente seguridad
			// aproximamus su posici�n
			PositionAproximator aprox = new PositionAproximator(mymap, game, game.getGhostCurrentNodeIndex(ghost),
					game.getGhostLastMoveMade(ghost), LastGhostsKnownPositions.elementAt(nearest),
					GhostsLastMoveKnown.elementAt(nearest), EdibleGhostsPosAccuracies.elementAt(nearest));
			return aprox.getBestMoveTowardsEntityPos();
		} else
			return MOVE.NEUTRAL;

	}
}
