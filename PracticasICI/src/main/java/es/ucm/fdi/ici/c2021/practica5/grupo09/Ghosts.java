package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.GhostsCBRengine;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.GhostsStorageManager;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.MapaInfoGhost;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
	
	GhostsInput input;
	GhostsCBRengine cbrEngine;
	GhostsAction actionSelector;
	GhostsStorageManager storageManager;
	
	final static String FILE_PATH = "cbrdata/grupo09/Ghosts.csv"; //Cuidado!! poner el grupo aquí
	
	public Ghosts()
	{
		MapaInfoGhost mapa=new MapaInfoGhost();
		this.input = new GhostsInput(mapa);

		this.actionSelector = new GhostsAction();

		this.storageManager = new GhostsStorageManager();
		
		cbrEngine = new GhostsCBRengine(actionSelector, storageManager);
	}
	
	@Override
	public void preCompute(String opponent) {
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
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		myMoves.clear();

		for (GHOST ghost : GHOST.values()) //for each ghost
        {
			if(!game.isJunction(game.getGhostCurrentNodeIndex(ghost))){
				myMoves.put(ghost, MOVE.NEUTRAL);
				continue;
			}
			try {
				input.setGhost(ghost);
				input.parseInput(game);
				actionSelector.setGameAndGhost(game, ghost);
				storageManager.setGameAndGhost(game, ghost);
				cbrEngine.cycle(input.getQuery());
				MOVE movimiento = cbrEngine.getSolution();
				myMoves.put(ghost, movimiento);
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			myMoves.put(ghost, MOVE.NEUTRAL);
		}

		return myMoves;
	}

}
