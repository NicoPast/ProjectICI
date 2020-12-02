package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan;

import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan.actions.EatGhostprueba;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan.actions.EatPPprueba;
import es.ucm.fdi.ici.c2021.practica3.grupo09.demorules.ghosts.GhostsInput;
import es.ucm.fdi.ici.rules.Action;
import es.ucm.fdi.ici.rules.Input;
import es.ucm.fdi.ici.rules.RuleEngine;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRules  extends PacmanController {
	
	private static final String RULES_PATH = "es/ucm/fdi/ici/practica3/grupo9/MsPacMan";
	
	HashMap<String,Action> map;
	
	RuleEngine msPacManRuleEngine;
	
	public MsPacManRules() {
		map = new HashMap<String,Action>();
		
		Action eatpp = new EatPPprueba();
		Action eatghost = new EatGhostprueba();
		
		map.put("EatPP", eatpp);
		map.put("EatGhost", eatghost);
		
		msPacManRuleEngine = new RuleEngine("MsPacManEngine","es/ucm/fdi/ici/c2021/practica3/grupo09/MsPacMan/mspacmanrules.clp", map);
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		//Process input
		Input input = new MsPacManInput(game);
		//load facts
		//reset the rule engines
		msPacManRuleEngine.reset();
		msPacManRuleEngine.assertFacts(input.getFacts());
				
				
		return msPacManRuleEngine.run(game);
				
	}

}
