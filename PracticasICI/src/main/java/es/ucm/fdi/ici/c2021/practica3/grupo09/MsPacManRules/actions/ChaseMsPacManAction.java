package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseMsPacManAction implements Action{

	MapaInfo mapInfo;
	interseccion interseccionActual;
	float distanciaMaximaPerseguir = 80;
	
	public ChaseMsPacManAction(MapaInfo map){
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {  
		//filtra que estemos en una interseccion
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) == null) return MOVE.NEUTRAL;		
		interseccionActual = mapInfo.getInterseccionActual();	
		
		
		//necesitamos el fantasma comible mas cercano
		GHOST proxGhost = nearestEadableGhpost(game);
		
		
		MOVE moveToGhost = game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(proxGhost),
				game.getPacmanLastMoveMade(), DM.PATH);

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

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
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
														//hay que poner un +2 para que se cuenten las posiciones de las intersecciones
				if (distancia != -1 && distancia <= (interseccionActual.distancias.get(m) + 2) && !hasPowerPill(m,g, game)) { // no pillar el camino
					hasGhost = true;
					break; // hacemos el breake por que ya no nos interesa seguir buscando
				}
			}	
		}

		return hasGhost;
	}
	
	
	//mira si llegamos antes a la power pill que el proximo fantasma
	private boolean hasPowerPill(MOVE proxMove, GHOST proxGhost, Game game) { 
		
		if(interseccionActual.powerPill.get(proxMove) > 0) {
			//hay power pill, ahora hay que mirar quien llega antes
			int powerPillIndex = getPowerPillCercana(game);
			
			if(game.getDistance(game.getPacmanCurrentNodeIndex(), powerPillIndex,
					game.getPacmanLastMoveMade(), DM.PATH) < game.getDistance(game.getGhostCurrentNodeIndex(proxGhost), powerPillIndex,
							game.getGhostLastMoveMade(proxGhost), DM.PATH)) return true;
			
		}
		
		return false;
	}
	
	private int getPowerPillCercana(Game game) {
        int closestPowerPill = -1;
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (int currentPill : game.getActivePowerPillsIndices()) {
            double aux = game.getDistance(pacmanPos, currentPill, mapInfo.getMetrica());
            if (aux < closestDistance) {
                closestPowerPill = currentPill;
                closestDistance = aux;
            }
        }
        return closestPowerPill;
	}

	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
