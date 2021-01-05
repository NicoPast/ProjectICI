package es.ucm.fdi.ici.practica4.demofuzzy;

import java.util.HashMap;

import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import es.ucm.fdi.ici.practica4.demofuzzy.actions.ChaseAction;
import es.ucm.fdi.ici.practica4.demofuzzy.actions.ChillAction;
import es.ucm.fdi.ici.practica4.demofuzzy.actions.RunAwayAction;

public class MsPacManActionSelector implements ActionSelector {

	private final Double RUN_AWAY_LIMIT = 20.0;
	private final Double CHASE_LIMIT = 100.0;
	MapaInfo mapInfo;
	
	public MsPacManActionSelector(MapaInfo map) {
		mapInfo = map;
	}

	
	@Override
	public Action selectAction(HashMap<String, Double> fuzzyOutput) {
		Double runAway = fuzzyOutput.get("runAway");
		Double edible = fuzzyOutput.get("eadable");
		System.out.println(edible);
		
		
		if(runAway > this.RUN_AWAY_LIMIT)
			return new RunAwayAction(mapInfo);
		else if(edible > this.CHASE_LIMIT) 
			return new ChaseAction(mapInfo);
		else
			return new ChillAction(mapInfo);
	}
	
	public void SetMap(MapaInfo map) {
		mapInfo=map;
	}
}
