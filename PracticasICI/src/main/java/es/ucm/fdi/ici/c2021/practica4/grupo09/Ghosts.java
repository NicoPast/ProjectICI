package es.ucm.fdi.ici.c2021.practica4.grupo09;

import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.CheckMateAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.GoToActiveGhostAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.ProtectAlliesAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.SecurePPillAction;

import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	MapaInfoGhost mapInfo;

	private static final String RULES_PATH = "es/ucm/fdi/ici/c2021/practica4/grupo09/ghosts/rules";
	
	public Ghosts() {
		
		
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		
		return null;
	}
}
