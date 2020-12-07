package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo;
import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseMsPacManAction implements Action{

	MapaInfo mapInfo;
	double distanciaPerseguir = 400;
	
	public ChaseMsPacManAction(MapaInfo map){
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {  
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) == null) return MOVE.NEUTRAL;
		
			return game.getApproximateNextMoveTowardsTarget(mapInfo.getInterseccionActual().identificador,
				game.getGhostCurrentNodeIndex(fantasmaComibleCerca(game)), game.getPacmanLastMoveMade(),
				mapInfo.getMetrica());
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
	}
	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
	

	GHOST fantasmaComibleCerca(Game game) {
		GHOST fantasma =  null;
		double distancia = Double.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
						
			if (game.isGhostEdible(g)) { //la distancia pacMan-ghost(comible) > ghost(comible)-ghost(!combible)
				double distAux = game.getDistance(mapInfo.getInterseccionActual().identificador,
						game.getGhostCurrentNodeIndex(g), mapInfo.getMetrica());
				
				
				if (distAux < distancia && distAux < distanciaPerseguir && !fantasmaEnCamino(game,g)) { //fantasma comible en rango
					fantasma = g;
					distancia = distAux;
				}
			}
		}

		if (distancia < distanciaPerseguir) { // hay fantasmas para comer y está cerca
			return fantasma;
		} 
		else return null;
	}
	
    private boolean fantasmaEnCamino(Game game, GHOST g) {
    	if(game.isGhostEdible(g)) return false;
    	
    	MOVE posibleMovimiento = MOVE.NEUTRAL;
    	
    	posibleMovimiento = game.getApproximateNextMoveTowardsTarget(mapInfo.getInterseccionActual().identificador,
    			game.getGhostCurrentNodeIndex(g), game.getPacmanLastMoveMade(), mapInfo.getMetrica());
    	
    	double distancia = mapInfo.getInterseccionActual().distancias.get(posibleMovimiento) + 2;
    	
    	for(GHOST fantasma:GHOST.values()) {
    		if(fantasma != g && game.getDistance(game.getGhostCurrentNodeIndex(fantasma),
    				mapInfo.getInterseccionActual().destinos.get(posibleMovimiento), mapInfo.getMetrica()) <= distancia) { //nos come un fantasma
    			return true;
    		}
    	}
    	
    	return false;
    }
}
