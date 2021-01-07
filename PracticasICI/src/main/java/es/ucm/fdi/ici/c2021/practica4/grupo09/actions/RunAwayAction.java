package es.ucm.fdi.ici.c2021.practica4.grupo09.actions;


import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfo.interseccion;
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
				
		//System.out.println("Running");
		int powerPillCercana = mapInfo.getClosestPP(game);
		
		if(powerPillCercana == -1) {
			// best move
			return mapInfo.getBestMove(game);
			/*return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), mapInfo.getClosestPill(game),
					DM.PATH);*/
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
		
		if(dist == Double.MAX_VALUE) return mapInfo.getBestMove(game);
		else {
			MOVE proxMovAux = game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostDest),
					game.getPacmanLastMoveMade(), DM.PATH);
			if(!ghostInWay(game, proxMovAux, mapInfo.getInterseccionActual())) return proxMovAux;
			else return mapInfo.getBestMove(game);
		}
				
	}
	
	private boolean ghostInWay(Game game, MOVE m, interseccion interseccionActual) {
		boolean hasGhost = false;
		// mira si m no es de donde vienes, si no es neutral y si existe camino

		// mira para todos los fantasmas, si avanzando por ese camino me pillan
		for (GHOST g : GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1 && !game.isGhostEdible(g)) { //si vemos el fantasma y ademas no es comible
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
}
