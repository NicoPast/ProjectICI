package es.ucm.fdi.ici.practica4.demofuzzy.actions;


import java.util.Random;

import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class RunAwayAction implements Action {
	MapaInfo mapInfo; 
    
	private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
    
	public RunAwayAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		return allMoves[rnd.nextInt(allMoves.length)];
    }
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
