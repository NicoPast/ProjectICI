package es.ucm.fdi.ici.practica4.demofuzzy.actions;


import java.util.Random;

import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo.interseccion;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class EatPowePillAction implements Action {
	MapaInfo mapInfo; 
    
    
    public EatPowePillAction(MapaInfo map) {
		mapInfo = map;
	}
	
	
	@Override
	public MOVE execute(Game game) {
		
		System.out.println("Eating");
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) == null) return MOVE.NEUTRAL;
		
		int powerPillCercana = mapInfo.getClosestPP(game);
		
		if(powerPillCercana == -1); //"best move"
		
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		
		//existe una power pill por el mapa
		double distanciaPP = game.getDistance(interseccionActual.identificador, powerPillCercana, mapInfo.getMetrica());
		MOVE direccion = MOVE.NEUTRAL;
		for(MOVE m:MOVE.values()) {
			if (m != mapInfo.getUltimoMovimientoDeLlegada() &&
					m != MOVE.NEUTRAL && interseccionActual.distancias.get(m) != null) {
				//entre todos estos caminos, por cual te quedas mas cerca de la pill
				
				boolean fantasmaDetectado = false;
				for(GHOST g: GHOST.values()){ //recorremos todos los fantasmas
					if(game.getGhostCurrentNodeIndex(g)==-1) continue;
					double distanciaFantasma = game.getDistance(interseccionActual.destinos.get(m), 
							game.getGhostCurrentNodeIndex(g), DM.PATH);
					
					if(distanciaFantasma <= interseccionActual.distancias.get(m) + 2 || 
							mapInfo.lairDanger(game, interseccionActual.destinos.get(m), m)) {//por este camino me pillan						
						fantasmaDetectado = true;
						break;
					}
				}								
				
				if(!fantasmaDetectado) {
					double distanciaProximoNodoPP = game.getDistance(interseccionActual.destinos.get(m),
							powerPillCercana, mapInfo.getMetrica());
					if(distanciaProximoNodoPP < distanciaPP) {
						direccion = m;
					}				
				}
			}
		}
		
		//direccion sigue siento nuetral, estamos en la interseccion mas cercana a la pill o hay fantasmas por todos los lados posibles
		//System.out.println("estoy en riesgo");
		if(direccion == MOVE.NEUTRAL) { //por todo hay fantasmas o por ningun lado nos hacercamos (estamos en la interseccion mas cercana)
			//System.out.println("estoy muy cerca");
			MOVE mAux = game.getApproximateNextMoveTowardsTarget(interseccionActual.identificador,
						powerPillCercana, game.getPacmanLastMoveMade(), DM.PATH); //no deberia entrar aqui pero para asegurar
			
			boolean fantasma = false;
			for(GHOST g:GHOST.values()){ //recorremos todos los fantasmas
				if(game.getGhostCurrentNodeIndex(g)==-1) continue;
				double distanciaFantasma = game.getDistance(powerPillCercana, 
						game.getGhostCurrentNodeIndex(g), DM.PATH);
				
				if(distanciaFantasma < game.getDistance(interseccionActual.identificador,
						powerPillCercana, game.getPacmanLastMoveMade(),DM.PATH)) {//por este camino me pillan						
					fantasma = true;
					break;
				}
			}				
			
			if(fantasma) return mapInfo.getBestMove(game);
			else return mAux;
		}
		else return direccion;
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
