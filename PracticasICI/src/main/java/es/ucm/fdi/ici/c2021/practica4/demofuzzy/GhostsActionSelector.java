package es.ucm.fdi.ici.c2021.practica4.demofuzzy;

import java.util.HashMap;
import java.util.EnumMap;
import java.lang.Float;

import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import pacman.game.Constants.GHOST;

public class GhostsActionSelector implements ActionSelector {

	private final Double RUN_AWAY_LIMIT = 20.0;

	@Override
	public Action selectAction(HashMap<String, Double> fuzzyOutput) {
		Double runAway = fuzzyOutput.get("runAway");
		
		if(runAway> this.RUN_AWAY_LIMIT)
			return new RunAwayAction(null, null);
		else
			return new GoToPPillAction();
	}

}
