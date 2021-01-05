package es.ucm.fdi.ici.c2021.practica4.grupo09;

import es.ucm.fdi.ici.fuzzy.ActionSelector;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import es.ucm.fdi.ici.fuzzy.observers.ConsoleFuzzyEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController{

	private static final String RULES_PATH = "src/main/java/es/ucm/fdi/ici/c2021/practica4/grupo09/";
	FuzzyEngine fuzzyEngine;
	MsPacManInput input ;
	MapaInfo mapInfo;
	
	public MsPacMan()
	{
		mapInfo = new MapaInfo();
		
		MsPacManActionSelector actionSelector = new MsPacManActionSelector(mapInfo);
		
		input = new MsPacManInput();
		 
		fuzzyEngine = new FuzzyEngine("MsPacMan",RULES_PATH+"mspacman.fcl","FuzzyMsPacMan",actionSelector);
		//ConsoleFuzzyEngineObserver observer = new ConsoleFuzzyEngineObserver("MsPacMan","MsPacManRules");
		//fuzzyEngine.addObserver(observer);
		
	}
	
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		mapInfo.update(game);	
		input.parseInput(game);
		return fuzzyEngine.run(input.getFuzzyValues(),game);
	}

}
