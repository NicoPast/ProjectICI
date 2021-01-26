package es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions;

import es.ucm.fdi.ici.c2021.practica5.grupo09.Action;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo.interseccion;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChillAction implements Action{
	MapaInfo mapInfo; 
	
    
	public ChillAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
			
		//System.out.println("Chill");
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		if(interseccionActual == null) return MOVE.DOWN;
		//buscamos por que camino nos comemos mas pils
		
		MOVE proxMov = MOVE.NEUTRAL;
		int maxPills = 0;
		for(MOVE m:MOVE.values()) {
			if( interseccionActual.pills.get(m) != null && considerPath(game, m, interseccionActual)) {
				//si existen pills por ese camino
				int aux = interseccionActual.pills.get(m);
				if(aux > maxPills) {
					maxPills = aux;
					proxMov = m;
				}
			}
		}
		
		if(proxMov == MOVE.NEUTRAL) proxMov = proxMovNotPills(game);
		
		if(proxMov == MOVE.NEUTRAL)
			System.out.println("Neutral");
		return proxMov;
    }
	
	private Boolean considerPath(Game game, MOVE mov, interseccion interseccionActual) {
		if(interseccionActual.powerPill.get(mov) > 0) {
			int powerPill = mapInfo.getClosestPP(game);
			if(mapInfo.ignoresPP.get(powerPill) > 0) {
				int aux = mapInfo.ignoresPP.get(powerPill);
				mapInfo.ignoresPP.remove(powerPill);
				aux--;
				mapInfo.ignoresPP.put(powerPill, aux);
				return false;
			}
		}
		
		return true;
	}
    
	private MOVE proxMovNotPills(Game game) {
		MOVE proxMov = MOVE.NEUTRAL;
		
		int closestPill = mapInfo.getClosestPillAnchura(game);
		if(closestPill  != -1) 
			proxMov = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closestPill,
					DM.PATH);
		
		return proxMov;
	}
	

	@Override
	public String getActionId() {
		return "ChillAction";
	}   
	
	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
