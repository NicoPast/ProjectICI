package es.ucm.fdi.ici.practica4.demofuzzy.actions;


import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo.interseccion;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class ChillAction implements Action{
	MapaInfo mapInfo; 
	
    
	public ChillAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		 
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		if(interseccionActual == null) return MOVE.DOWN;
		//buscamos por que camino nos comemos mas pils
		
		MOVE proxMov = MOVE.NEUTRAL;
		int maxPills = 0;
		for(MOVE m:MOVE.values()) {
			if(interseccionActual.pills.get(m) != null) {
				//si existen pills por ese camino
				int aux = interseccionActual.pills.get(m);
				if(aux > maxPills) {
					maxPills = aux;
					proxMov = m;
				}
			}
		}
		
		return proxMov;
    }
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
