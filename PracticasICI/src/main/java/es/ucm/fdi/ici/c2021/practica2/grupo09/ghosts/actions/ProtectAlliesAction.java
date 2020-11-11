package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput.NODEANDDISTANCE;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ProtectAlliesAction implements Action {

	private MapaInfo mymap;
	GHOST ghost;

	public ProtectAlliesAction(GHOST ghost, MapaInfo map) {
		this.ghost = ghost;
		this.mymap = map;
	}

	private Vector<GHOST> EdibleGhosts(Game game) {
		Vector<GHOST> edibleGhosts = new Vector<GHOST>();
		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost))
				edibleGhosts.add(ghost);
		}
		return edibleGhosts;
	}

	public int nearestGhostDistance(Game game, int myPos, int[] pos, MOVE m) {

		int nearestP = -1;
		double nearestDist = Double.MAX_VALUE;
		for (int p : pos) {
			double aux = game.getDistance(myPos, p, m, /* constant dm */DM.EUCLID);
			if (aux < nearestDist) {
				nearestDist = aux;
				nearestP = p;
			}
		}

		return nearestP;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost)) {
			Vector<GHOST> edibleGhosts = EdibleGhosts(game);
			int[] ediblePos = new int[edibleGhosts.size()];
			// rellenamos un array con las posiciones los fantasmas comestibles
			for (int i = 0; i < edibleGhosts.size(); i++) {
				ediblePos[i] = game.getGhostCurrentNodeIndex(edibleGhosts.elementAt(i));
			}
			// vemos cuál es la distancia al fantasma edible más cercano del pacman para su
			// último movimiento
			int nearest = nearestGhostDistance(game, game.getPacmanCurrentNodeIndex(), ediblePos,
					mymap.getUltimoMovReal());
			// No hace falta comprobar si yo estoy más cerca de él que el pacman
			// porque el otro fantasma tratará de acercarse a mi

			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), nearest,
					game.getGhostLastMoveMade(ghost), /* constant */ DM.EUCLID);
		} else
			return MOVE.NEUTRAL;

	}
}
