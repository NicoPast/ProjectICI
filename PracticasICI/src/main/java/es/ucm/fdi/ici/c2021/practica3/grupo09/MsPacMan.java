package es.ucm.fdi.ici.c2021.practica3.grupo09;

import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.MsPacManInput;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.ChaseMsPacManAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.ChillAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.EatPowePillAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.RunAwayMsPacManAction;
import es.ucm.fdi.ici.rules.Action;
import es.ucm.fdi.ici.rules.Input;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan  extends PacmanController {
	
	
	HashMap<String,Action> map;
	private MapaInfo mapInfo = null;
	RuleEngine msPacManRuleEngine;
	
	public MsPacMan() {
		map = new HashMap<String,Action>();
    	mapInfo = new MapaInfo();
		
		Action eatghost = new ChaseMsPacManAction(mapInfo);
		Action chill = new ChillAction(mapInfo);
		Action eatPP = new EatPowePillAction(mapInfo);
		Action runaway = new RunAwayMsPacManAction(mapInfo);
		
		map.put("EatGhost", eatghost);
		map.put("Chill", chill);
		map.put("EatPowerPill", eatPP);
		map.put("RunAway", runaway);
		
		
		msPacManRuleEngine = new RuleEngine("MsPacManEngine","es/ucm/fdi/ici/c2021/practica3/grupo09/MsPacManRules/mspacmanrules.clp", map);
	
	
		ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver("MsPacMan", true);
		msPacManRuleEngine.addObserver(observer);
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		mapInfo.update(game);
		
		//Process input
		Input input = new MsPacManInput(game, mapInfo);
		//load facts
		//reset the rule engines
		msPacManRuleEngine.reset();
		msPacManRuleEngine.assertFacts(input.getFacts());
				
				
		return msPacManRuleEngine.run(game);
				
	}

}
