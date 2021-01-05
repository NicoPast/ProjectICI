package es.ucm.fdi.ici.practica4.demofuzzy.actions;

import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo.interseccion;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class ChaseAction implements Action{
	MapaInfo mapInfo; 
	
	public ChaseAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		 
		
		//miramos si hay algun fantasma al que estamos viendo 
		GHOST ghostDest = GHOST.BLINKY;
		double distToGhost = Double.MAX_VALUE;
		
		for(GHOST g: GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1) { //lo podemos ver
				if(game.isGhostEdible(g)) {
					double aux = game.getDistance(game.getPacmanCurrentNodeIndex(), pos,
							game.getGhostLastMoveMade(g), DM.PATH);
					if(aux < distToGhost) {
						distToGhost = aux;
						ghostDest = g;
					}
				}
			}
		}
		
		if(distToGhost != Double.MAX_VALUE)  //se ha encontrado un fantasma objetivo (el mas cercano comible)
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostDest),
					game.getPacmanLastMoveMade(), DM.PATH);
		
		//no hemos visto nada y tenemos que aproximar jeje
		
		return MOVE.NEUTRAL;
    }
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
	
}
