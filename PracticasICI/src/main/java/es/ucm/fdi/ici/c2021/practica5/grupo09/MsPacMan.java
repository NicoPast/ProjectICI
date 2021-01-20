package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.MsPacManCBRengine;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.MsPacManStorageManager;
import es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions.GoToPPillAction;
import es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions.RunAwayAction;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	MsPacManInput input;
	MsPacManCBRengine cbrEngine;
	MsPacManActionSelector actionSelector;
	MsPacManStorageManager storageManager;
	
	final static String FILE_PATH = "cbrdata/grupo09/Ghosts.csv";

	MapaInfo mapInfo;
	//RunAwayAction runAwayAction;
	
	public MsPacMan()
	{
		mapInfo = new MapaInfo();
		
		this.input = new MsPacManInput();

		//runAwayAction = new RunAwayAction(mapInfo);
		
		List<Action> actions = new ArrayList<Action>();
		actions.add(new GoToPPillAction());
		actions.add(new RunAwayAction(mapInfo));
		this.actionSelector = new MsPacManActionSelector(actions);

		this.storageManager = new MsPacManStorageManager();
		
		cbrEngine = new MsPacManCBRengine(actionSelector, storageManager);
	}
	
	@Override
	public void preCompute(String opponent) {
		mapInfo = new MapaInfo(); //esto deberia resetearlo en todos los estados
		
		cbrEngine.setCaseBaseFile(String.format(FILE_PATH, opponent));
		try {
			cbrEngine.configure();
			cbrEngine.preCycle();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void postCompute() {
		try {
			cbrEngine.postCycle();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		mapInfo.update(game);
		
		//This implementation only computes a new action when MsPacMan is in a junction. 
		//This is relevant for the case storage policy
		if(!game.isJunction(game.getPacmanCurrentNodeIndex()))
			return MOVE.NEUTRAL;		
		try {
			input.parseInput(game);
			actionSelector.setGame(game);
			storageManager.setGame(game);
			cbrEngine.cycle(input.getQuery());
			Action action = cbrEngine.getSolution();
			return action.execute(game);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MOVE.NEUTRAL;
	}

}
