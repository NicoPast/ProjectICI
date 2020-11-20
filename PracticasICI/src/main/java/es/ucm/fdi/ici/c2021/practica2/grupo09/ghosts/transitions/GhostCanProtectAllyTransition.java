package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput.NODEANDDISTANCE;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostCanProtectAllyTransition implements Transition {

	GHOST ghost;
	double PACMAN_MIN_DISTANCE = 30;
	double MAX_DISTANCE_TO_WEAK_GHOST= 20;
	int id;

	public GhostCanProtectAllyTransition(GHOST ghost, int id) {
		super();
		this.ghost = ghost;
		this.id = id;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		//Si pacman está cerca de una PowerPill no puedo proteger a los aliados por si me come
		PacManNearPPillTransition nearPPill=new PacManNearPPillTransition(ghost);
		if(nearPPill.evaluate(in))
			return false;
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
			// vemos cual es la distancia al fantasma edible mas cercano del pacman para su
			// ultimo movimiento
			NODEANDDISTANCE nearestNodeAndDistance=input.nearestGhostDistance(game.getPacmanCurrentNodeIndex(), ediblePos,
					game.getPacmanLastMoveMade());
			double nearest = nearestNodeAndDistance.d;
			// No hace falta comprobar si yo estoy mas cerca de el que el pacman
			// porque el otro fantasma tratara de acercarse a mi pero conviene comprobar
			//que estoy lo suficientemente cerca

			return nearest < PACMAN_MIN_DISTANCE && 
					game.getDistance(game.getGhostCurrentNodeIndex(ghost), nearestNodeAndDistance.n, DM.EUCLID)
					< MAX_DISTANCE_TO_WEAK_GHOST;

		} else
			return false;

	}

	public String toString() {
		return ghost.name() + " can protect an ally"+ id;
	}
}
