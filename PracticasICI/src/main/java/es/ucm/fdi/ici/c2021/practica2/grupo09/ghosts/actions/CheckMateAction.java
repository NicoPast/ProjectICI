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
		if(mapa.movesCheckMate.get(ghost) == null)
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.EUCLID);
		else 
			return mapa.movesCheckMate.get(ghost);
	}
}

