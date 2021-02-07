package es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions;


import es.ucm.fdi.ici.c2021.practica5.grupo09.Action;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo.interseccion;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseAction implements Action{
	MapaInfo mapInfo; 
	
	public ChaseAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {	
		//System.out.println("Chasing");
				
		//miramos si hay algun fantasma al que estamos viendo 
		GHOST ghostDest = GHOST.BLINKY;
		double distToGhost = Double.MAX_VALUE;
		
		for(GHOST g: GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1) { //lo podemos ver
				if(game.isGhostEdible(g)) {
					double aux = game.getDistance(game.getPacmanCurrentNodeIndex(), pos,
							game.getPacmanLastMoveMade(), DM.PATH);
					if(aux < distToGhost) {
						distToGhost = aux;
						ghostDest = g;
					}
				}
			}
		}
		
		if(distToGhost != Double.MAX_VALUE) {//se ha encontrado un fantasma objetivo (el mas cercano comible)
			MOVE auxMov = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostDest),
					DM.PATH);
			
			//mira si hay un fantama no comible en nuestro comino
			if(!ghostInWay(game, auxMov, mapInfo.getInterseccionActual())) return auxMov;
		}
		
		
		//no hemos visto nada y tenemos que aproximar
		double minDist = Double.MAX_VALUE;
		int target = -1;
		for(int i = 0; i < 4; i++) {
			if(mapInfo.isGhostEdible[i]) {
				 double dist = game.getDistance(game.getPacmanCurrentNodeIndex(), mapInfo.ghostLastPos[i], DM.PATH);
				 if(dist < minDist) {
					 target = i;
					 minDist = dist;
				 }
			}
		}
		
		if(target != -1) {
			MOVE auxMov = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), mapInfo.ghostLastPos[target],
					DM.PATH);

			//si por ese camino nos vamos a comer una PP, lo ignoramos y miramos el bestMove
			if(mapInfo.getInterseccionActual().powerPill.get(auxMov) != null &&
					mapInfo.getInterseccionActual().powerPill.get(auxMov) > 0) 
				return mapInfo.getBestMove(game);
			else return auxMov;
		}
		
		return MOVE.NEUTRAL;
    }
	
	private boolean ghostInWay(Game game, MOVE m, interseccion interseccionActual) {
		if(interseccionActual == null) return false;
		boolean hasGhost = false;
		// mira si m no es de donde vienes, si no es neutral y si existe camino

		// mira para todos los fantasmas, si avanzando por ese camino me pillan
		for (GHOST g : GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1 && !game.isGhostEdible(g) && interseccionActual.destinos.get(m) != null) { //si vemos el fantasma y ademas no es comible
				double distancia = game.getDistance(interseccionActual.destinos.get(m), game.getGhostCurrentNodeIndex(g),
					DM.PATH);
														//hay que poner un +2 para que se cuenten las posiciones de las intersecciones
				if (distancia != -1 && distancia <= (interseccionActual.distancias.get(m) + 2)) { // no pillar el camino
					hasGhost = true;
					break; // hacemos el breake por que ya no nos interesa seguir buscando
				}
			}	
		}

		return hasGhost;
	}
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
	

	@Override
	public String getActionId() {
		return "ChaseAction";
	}   
	
}
