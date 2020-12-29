package es.ucm.fdi.ici.c2021.practica4.grupo09;

import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import pacman.controllers.POGhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends POGhostController {

	MapaInfoGhost mapInfo;
	GhostsInput input;
	GhostActionSelector actionSelector;

	FuzzyEngine fuzzyEngine;
	
	private static final String RULES_PATH = "es/ucm/fdi/ici/c2021/practica4/grupo09/ghosts/";
	
	public Ghosts() {
		mapInfo = new MapaInfoGhost();
		input = new GhostsInput(mapInfo);

		actionSelector = new GhostActionSelector(input, mapInfo);
		fuzzyEngine = new FuzzyEngine("Ghost", RULES_PATH + "ghosts.fcl", "FuzzyGhosts", actionSelector);
	}

	@Override
	public MOVE getMove(GHOST ghost, Game game, long timeDue) {
		input.setGhost(ghost);
		input.parseInput(game);
		actionSelector.setGhost(ghost);
		return fuzzyEngine.run(input.getFuzzyValues(),game);
	}
}
