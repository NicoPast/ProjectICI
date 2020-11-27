package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CheckMateAction implements Action {

	private MapaInfoGhost mapa;
	GHOST ghost;
	
	public CheckMateAction( GHOST ghost, MapaInfoGhost mapa) {
		this.ghost = ghost;
		this.mapa = mapa;
	}

	public void parseFact(Fact actionFact){
		
	}

	@Override
	public MOVE execute(Game game) {
		if(!game.doesGhostRequireAction(ghost))
			return null;

		if(mapa.movesCheckMate.get(ghost) != null)
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), mapa.movesCheckMate.get(ghost), game.getGhostLastMoveMade(ghost), DM.EUCLID);
		else
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.EUCLID);
	}
}

