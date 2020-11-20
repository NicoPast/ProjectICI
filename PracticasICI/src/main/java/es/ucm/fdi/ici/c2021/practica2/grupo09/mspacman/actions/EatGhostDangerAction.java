package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class EatGhostDangerAction implements Action {

	MapaInfo mapInfo;
	interseccion interseccionActual;
	float distanciaMaximaPerseguir = 80;

	public EatGhostDangerAction(MapaInfo map) {
		mapInfo = map;
	}

	@Override
	public MOVE execute(Game game) {

		//filtra que estemos en una interseccion
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) == null) return MOVE.NEUTRAL;		
		interseccionActual = mapInfo.getInterseccionActual();	
		
		
		//necesitamos el fantasma comible mas cercano
		GHOST proxGhost = nearestEadableGhpost(game);
		
		MOVE moveToGhost = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(proxGhost), DM.PATH);

		//si hay un fantasma no comible en el camino o esta muy lejos
		if(ghostInWay(moveToGhost, game) || game.getDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(proxGhost),
				game.getPacmanLastMoveMade(), DM.PATH) > distanciaMaximaPerseguir) {
			//usar get best move
			return mapInfo.getBestMove(game);
		}
		else {
			return moveToGhost;
		}		
	}
	
	private GHOST nearestEadableGhpost(Game game) {
		GHOST ghost = null;
		double distancia = Double.MAX_VALUE;
		
		for(GHOST g:GHOST.values()) {
			if(game.isGhostEdible(g)) {
				double distAux = game.getDistance(game.getGhostCurrentNodeIndex(g), 
						interseccionActual.identificador, DM.PATH);
				
				//System.out.println(g + " " + distAux);
				
				if(distAux < distancia) {
					distancia = distAux;
					ghost = g;
				}
			}
		}

		//System.out.println("Fantasma objetivo: " + ghost);
		return ghost;
	}

	// mira su hay un fantasma en el camino
	private boolean ghostInWay(MOVE m, Game game) {

		boolean hasGhost = false;
		// mira si m no es de donde vienes, si no es neutral y si existe camino

		// mira para todos los fantasmas, si avanzando por ese camino me pillan
		for (GHOST g : GHOST.values()) {
			if(!game.isGhostEdible(g)) {
				double distancia = game.getDistance(interseccionActual.destinos.get(m), game.getGhostCurrentNodeIndex(g),
					DM.PATH);
				if (distancia != -1 && distancia <= interseccionActual.distancias.get(m)) { // no pillar el camino
					hasGhost = true;
					break; // hacemos el breake por que ya no nos interesa seguir buscando
				}
			}	
		}

		return hasGhost;
	}


}
