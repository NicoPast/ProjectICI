package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.PositionAproximator;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseAction implements Action {


	GHOST ghostType;
	MapaInfoGhost mapa;
	interseccion pacManPos;
	MOVE pacmanKnownLastMoveMade;
	double fuzzyAccuracy;
	
	public ChaseAction(GHOST ghost, MapaInfoGhost map_, interseccion pacManPos, MOVE pacmanKnownLastMoveMade, double fuzzyAccuracy) {
		this.ghostType = ghost;
		this.mapa = map_;
		this.pacManPos = pacManPos;
		this.pacmanKnownLastMoveMade = pacmanKnownLastMoveMade;
		this.fuzzyAccuracy = fuzzyAccuracy;
	}

	// Se dirige a la interseccion donde es mas probable que se dirija el PacMan y que yo este más cerca
	@Override
	public MOVE execute(Game game) {
		if (!game.doesGhostRequireAction(ghostType))  //if does not require an action	
			return MOVE.NEUTRAL;
		
		int myPos = game.getGhostCurrentNodeIndex(ghostType);
		MOVE mylastMove = game.getGhostLastMoveMade(ghostType);

		//Si estoy seguro de hacia donde va el pacman y se dirije hacia mi, me dirijo yo hacia el pacman
		if(fuzzyAccuracy >= 1 && myPos == pacManPos.identificador && game.getPacmanCurrentNodeIndex() > 0)
			return game.getApproximateNextMoveTowardsTarget(myPos, game.getPacmanCurrentNodeIndex(), mylastMove, DM.EUCLID);

		if(pacManPos != null){
			PositionAproximator aproximator = new PositionAproximator(mapa, game, myPos, mylastMove, pacManPos, pacmanKnownLastMoveMade, fuzzyAccuracy);
			return aproximator.getBestMoveTowardsEntityPos();	
		}
		return MOVE.NEUTRAL;
	}
}
