package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.GhostsFSM;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToActiveGhostAction implements Action {

	private GhostsFSM myFSM;
    GHOST ghost;
	public GoToActiveGhostAction( GHOST ghost,GhostsFSM fsm ) {
		this.ghost = ghost;
		this.myFSM=fsm;
	}
	@Override
	public MOVE execute(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

}
