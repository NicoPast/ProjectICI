package es.ucm.fdi.ici.c2021.practica4.grupo09.actions;


import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfo;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class RunAwayAction implements Action {
	MapaInfo mapInfo; 
    
	public RunAwayAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		
		System.out.println("Running");
		int powerPillCercana = mapInfo.getClosestPP(game);
		
		if(powerPillCercana == -1) {
			// best move
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), mapInfo.getClosestPill(game),
					DM.PATH);
		}
		
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), powerPillCercana,
				game.getPacmanLastMoveMade(), DM.PATH);
    }
	
	private MOVE runAway(Game game) {
		GHOST ghostDest = GHOST.BLINKY;
		double dist = Double.MAX_VALUE;
		
		for(GHOST g:GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1) { //lo podemos ver
				if(!game.isGhostEdible(g)) {
					double aux = game.getDistance(game.getPacmanCurrentNodeIndex(), pos,
							game.getGhostLastMoveMade(g), DM.PATH);
					if(aux < dist) {
						dist = aux;
						ghostDest = g;
					}
				}
			}
		}
		
		if(dist == Double.MAX_VALUE) return MOVE.NEUTRAL;
		else return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostDest),
				game.getPacmanLastMoveMade(), DM.PATH);
	}
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
