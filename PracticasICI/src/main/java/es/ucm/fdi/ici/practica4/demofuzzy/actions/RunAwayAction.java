package es.ucm.fdi.ici.practica4.demofuzzy.actions;


import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.practica4.demofuzzy.MapaInfo;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class RunAwayAction implements Action {
	MapaInfo mapInfo; 
    
	public RunAwayAction(MapaInfo map) {
		mapInfo = map;
	}
	
	@Override
	public MOVE execute(Game game) {
		int powerPillCercana = mapInfo.getClosestPP(game);
		
		if(powerPillCercana == -1) return MOVE.NEUTRAL; //"best move"
		
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), powerPillCercana,
				game.getPacmanLastMoveMade(), DM.PATH);
    }
           
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}
}
