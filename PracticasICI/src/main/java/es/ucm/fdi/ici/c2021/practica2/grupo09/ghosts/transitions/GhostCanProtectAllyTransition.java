package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostCanProtectAllyTransition implements Transition {

	GHOST ghost;
	double PACMAN_MIN_DISTANCE = 15;
	int id;

	public GhostCanProtectAllyTransition(GHOST ghost, int id) {
		super();
		this.ghost = ghost;
		this.id = id;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		Game game = input.getGame();
		if (game.doesGhostRequireAction(ghost) && !game.isGhostEdible(ghost)) {
			Vector<GHOST> edibleGhosts = input.getEdibleGhosts();
			// si no hay fantasmas comestibles no hay que proteger nada
			if (edibleGhosts.isEmpty())
				return false;
			int[] ediblePos = new int[edibleGhosts.size()];
			// rellenamos un array con las posiciones los fantasmas comestibles
			for (int i = 0; i < edibleGhosts.size(); i++) {
				ediblePos[i] = game.getGhostCurrentNodeIndex(edibleGhosts.elementAt(i));
			}
			// vemos cu�l es la distancia al fantasma edible m�s cercano del pacman para su
			// �ltimo
			// movimiento
			double nearest = input.nearestGhostDistance(game.getPacmanCurrentNodeIndex(), ediblePos,
					input.getPacmanRealMoveMade()).d;
			// No hace falta comprobar si yo estoy m�s cerca de �l que el pacman
			// porque el otro fantasma tratar� de acercarse a mi

			return nearest < PACMAN_MIN_DISTANCE;

		} else
			return false;

	}

	public String toString() {
		return ghost.name() + " can protect an ally"+ id;
	}
}
