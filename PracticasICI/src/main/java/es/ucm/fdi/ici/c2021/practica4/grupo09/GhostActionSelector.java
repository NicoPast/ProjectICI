package es.ucm.fdi.ici.c2021.practica4.grupo09;

import java.util.HashMap;

import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import pacman.game.Constants.GHOST;

public class GhostActionSelector implements ActionSelector {

	private GHOST ghost;
	
	public void setGhost(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public Action selectAction(HashMap<String, Double> fuzzyOutput) {
		Double runAway = fuzzyOutput.get("runAway");
		return null;
	}
}
