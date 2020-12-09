package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayMsPacManAction implements Action{
	
	MapaInfo mapInfo;
	
	public RunAwayMsPacManAction(MapaInfo map) {
		mapInfo = map;
	}
	
	
	@Override
	public MOVE execute(Game game) {  
		//ir a por la pill mas cercana sin que te maten
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) == null) return MOVE.NEUTRAL;
		
		int pillCercana = mapInfo.getClosestPill(game);
		
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		
		MOVE direccionPosible = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), 
				pillCercana, mapInfo.getMetrica());
		
		boolean fantasmaEncontrado = false;
		
		for(GHOST g:GHOST.values()){ //recorremos todos los fantasmas
			double distanciaFantasma = game.getDistance(interseccionActual.destinos.get(direccionPosible), 
					game.getGhostCurrentNodeIndex(g), DM.PATH);
			
			if(distanciaFantasma <= interseccionActual.distancias.get(direccionPosible) + 2 ||
					mapInfo.lairDanger(game, interseccionActual.destinos.get(direccionPosible), direccionPosible)) 
			{//por este camino me pillan						
				fantasmaEncontrado = true;
				break;
			}
		}
		
		if(fantasmaEncontrado) return mapInfo.getBestMove(game);
		else return direccionPosible;
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
	}
	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
