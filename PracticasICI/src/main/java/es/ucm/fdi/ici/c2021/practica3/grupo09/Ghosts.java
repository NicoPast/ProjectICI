package es.ucm.fdi.ici.c2021.practica3.grupo09;

import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions.CheckMateAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions.GoToActiveGhostAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions.ProtectAlliesAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.actions.SecurePPillAction;
import es.ucm.fdi.ici.rules.Action;
import es.ucm.fdi.ici.rules.Input;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	MapaInfoGhost mapInfo;

	private static final String RULES_PATH = "es/ucm/fdi/ici/c2021/practica3/grupo09/rules";
	
	HashMap<String,Action> map;
	
	EnumMap<GHOST,RuleEngine> ghostRuleEngines;
	
	public Ghosts() {
		
		map = new HashMap<String,Action>();
		mapInfo = new MapaInfoGhost();
		for(GHOST ghost : GHOST.values()){
			Action checkmate = new CheckMateAction(ghost, mapInfo);
			map.put(ghost.toString() + "checkmate", checkmate);
			
			Action protectAlly = new ProtectAlliesAction(ghost, mapInfo);
			map.put(ghost.toString() + "protects", protectAlly);

			Action securePPill = new SecurePPillAction(ghost, mapInfo);
			map.put(ghost.toString() + "secure", securePPill);

			Action chase = new ChaseAction(ghost, mapInfo);
			map.put(ghost.toString() + "chase", chase);

			Action seekProtection = new GoToActiveGhostAction(ghost, mapInfo);
			map.put(ghost.toString() + "seeksProtection", seekProtection);

			Action runAway = new RunAwayAction(ghost, mapInfo);
			map.put(ghost.toString() + "runsAway", runAway);			
		}
		
		ghostRuleEngines = new EnumMap<GHOST,RuleEngine>(GHOST.class);
		for(GHOST ghost: GHOST.values())
		{
			String rulesFile = String.format("%s/%srules.clp", RULES_PATH, ghost.name().toLowerCase());
			RuleEngine engine  = new RuleEngine(ghost.name(), rulesFile, map);
			ghostRuleEngines.put(ghost, engine);
			
			//add observer to every Ghost
			//ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(ghost.name(), true);
			//engine.addObserver(observer);
		}
		
		//add observer only to BLINKY
		//ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(GHOST.BLINKY.name(), false);
		//ghostRuleEngines.get(GHOST.BLINKY).addObserver(observer);	
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		
		//Process input
		Input input = new GhostsInput(game, mapInfo);
		//load facts
		//reset the rule engines
		for(RuleEngine engine: ghostRuleEngines.values()) {
			engine.reset();
			engine.assertFacts(input.getFacts());
		}
		
		EnumMap<GHOST,MOVE> result = new EnumMap<GHOST,MOVE>(GHOST.class);		
		for(GHOST ghost: GHOST.values())
		{
			RuleEngine engine = ghostRuleEngines.get(ghost);
			MOVE move = engine.run(game);
			result.put(ghost, move);
		}
		
		return result;
	}

}
