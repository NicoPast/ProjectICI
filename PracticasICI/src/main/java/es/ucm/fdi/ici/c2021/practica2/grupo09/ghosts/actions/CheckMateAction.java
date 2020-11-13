package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CheckMateAction implements Action {

	private MapaInfo mapa;
	GHOST ghost;
	
	public CheckMateAction( GHOST ghost, MapaInfo mapa) {
		this.ghost = ghost;
		this.mapa = mapa;
	}

	@Override
	public MOVE execute(Game game) {
		if(!game.doesGhostRequireAction(ghost))
			return null;
		int mypos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastmove = game.getGhostLastMoveMade(ghost);
		//Si no tengo movimiento o ya he llegado al destino, me dirijo al pacman
		if(mapa.movesCheckMate.get(ghost) == null || game.getDistance(mypos, mapa.movesCheckMate.get(ghost), lastmove, DM.PATH) < 2)
			return game.getNextMoveTowardsTarget(mypos, game.getPacmanCurrentNodeIndex(), lastmove, DM.EUCLID);
		else {
			return game.getNextMoveTowardsTarget(mypos, mapa.movesCheckMate.get(ghost), lastmove, DM.EUCLID);
		}
	}
}

