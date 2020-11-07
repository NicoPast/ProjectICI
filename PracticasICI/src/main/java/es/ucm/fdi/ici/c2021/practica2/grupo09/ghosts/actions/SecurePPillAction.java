package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class SecurePPillAction implements Action {

	private MapaInfo mymap;
    GHOST ghost;
	public SecurePPillAction( GHOST ghost,MapaInfo map ) {
		this.ghost = ghost;
		this.mymap=map;
	}
	@Override
	public MOVE execute(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

}
