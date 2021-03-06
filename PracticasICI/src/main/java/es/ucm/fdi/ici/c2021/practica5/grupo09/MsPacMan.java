package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.pacman.MsPacManCBRengine;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.pacman.MsPacManStorageManager;
import es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions.ChillAction;
import es.ucm.fdi.ici.c2021.practica5.grupo09.msPacMan.actions.RunAwayAction;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	MsPacManInput input;
	MsPacManCBRengine cbrEngine;
	MsPacManActionSelector actionSelector;
	MsPacManStorageManager storageManager;
	
	final static String FILE_PATH = "bin/es/ucm/fdi/ici/c2021/practica5/grupo09/data/MsPacMan.csv";

	MapaInfo mapInfo;
	
	public MsPacMan()
	{
		mapInfo = new MapaInfo();
		
		this.input = new MsPacManInput(mapInfo);

		//runAwayAction = new RunAwayAction(mapInfo);
		
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ChaseAction(mapInfo));
		actions.add(new ChillAction(mapInfo));
		actions.add(new RunAwayAction(mapInfo));
		this.actionSelector = new MsPacManActionSelector(actions);

		this.storageManager = new MsPacManStorageManager();
		
		cbrEngine = new MsPacManCBRengine(actionSelector, storageManager, mapInfo);
	}
	
	@Override
	public void preCompute(String opponent) {
		mapInfo = new MapaInfo(); //esto deberia resetearlo en todos los estados
		this.input.setMap(mapInfo); //evita errores en el mapa a la hora de usar el evaluador
		this.cbrEngine.setMap(mapInfo);
		
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
			cbrEngine.setGame(game);
			cbrEngine.cycle(input.getQuery());
			MOVE move = cbrEngine.getSolution().execute(game); //si no encuentra movimiento que mire en la accion (best move)
			return move;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MOVE.NEUTRAL;
	}

}
