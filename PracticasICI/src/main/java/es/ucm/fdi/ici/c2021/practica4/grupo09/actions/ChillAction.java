package es.ucm.fdi.ici.c2021.practica4.grupo09.actions;


import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

public class ChillAction implements Action{
	MapaInfo mapInfo; 
	
    
	public ChillAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		
		System.out.println("Chilling");
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		if(interseccionActual == null) return MOVE.DOWN;
		//buscamos por que camino nos comemos mas pils
		
		MOVE proxMov = MOVE.NEUTRAL;
		int maxPills = 0;
		for(MOVE m:MOVE.values()) {
			if(interseccionActual.pills.get(m) != null && considerPath(game, m, interseccionActual)) {
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
	
	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
