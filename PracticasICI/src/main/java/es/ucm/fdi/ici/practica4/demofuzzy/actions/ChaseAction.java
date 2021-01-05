package es.ucm.fdi.ici.practica4.demofuzzy.actions;

import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo.interseccion;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class ChaseAction implements Action{
	MapaInfo mapInfo; 
	
	public ChaseAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		 
		return MOVE.NEUTRAL;
    }
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
	
}
