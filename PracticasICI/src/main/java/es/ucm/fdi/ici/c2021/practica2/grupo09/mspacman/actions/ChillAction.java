package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChillAction implements Action{

	MapaInfo mapInfo;
	
	public ChillAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) != null) return mapInfo.getBestMove(game);
		else return MOVE.NEUTRAL;
	}
	
}
