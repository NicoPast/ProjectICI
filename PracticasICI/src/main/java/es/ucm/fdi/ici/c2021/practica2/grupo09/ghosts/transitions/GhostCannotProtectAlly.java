package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostCannotProtectAlly implements Transition {

	GHOST ghost;
	double PACMAN_MIN_DISTANCE = 15;

	public GhostCannotProtectAlly(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		Game game = input.getGame();
		if(game.isGhostEdible(ghost))
			return true;
		if (game.doesGhostRequireAction(ghost)) {
			Vector<GHOST> edibleGhosts = input.getEdibleGhosts();
			// si ya no hay fantasmas comestibles no hay que proteger nada
			if (edibleGhosts.isEmpty())
				return true;
			int[] ediblePos = new int[edibleGhosts.size()];

			// rellenamos un array con las posiciones los fantasmas comestibles
			for (int i = 0; i < edibleGhosts.size(); i++) {
				ediblePos[i] = game.getGhostCurrentNodeIndex(edibleGhosts.elementAt(i));
			}

			// vemos cuál es la distancia al fantasma edible más cercano del pacman para su
			// último
			// movimiento
			double nearest = input.nearestGhostDistance(game.getPacmanCurrentNodeIndex(), ediblePos,
					game.getPacmanLastMoveMade()).d;
			// No hace falta comprobar si yo estoy más cerca de él que el pacman
			// porque el otro fantasma tratará de acercarse a mi

			return nearest >= PACMAN_MIN_DISTANCE;

		} else
			return false;
	}

	public String toString() {
		return ghost.name() + " cannot protect any allies";
	}
}
