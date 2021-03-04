package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo;
import es.ucm.fdi.ici.rules.Action;
import jess.Fact;
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
	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
	}
}
