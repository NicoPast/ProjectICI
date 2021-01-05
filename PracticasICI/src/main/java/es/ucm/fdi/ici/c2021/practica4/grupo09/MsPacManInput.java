package es.ucm.fdi.ici.c2021.practica4.grupo09;

import java.util.HashMap;

import es.ucm.fdi.ici.fuzzy.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput implements Input {

	double[] distance = {50,50,50,50};
	double[] confidence = {0,0,0,0};
	double[] eadableConfidence = {0,0,0,0};
	double posibleEadable = 0;
	
	
	@Override
	public void parseInput(Game game) {
		
		if(game.wasPowerPillEaten()) {
			posibleEadable = 100;
			
			// por que se hace esto?
			for(int i = 0; i<4;i++) { //no nos interesa
				confidence[i] = 100;
				distance[i] = 50; 
			}
		}
		else if(posibleEadable > 0) posibleEadable -= 0.5;
		
		if(game.wasPacManEaten()) {
			for(int i = 0; i < 4; i++) {
				distance[i] = 50;
				confidence[i] = 0;
				eadableConfidence[i] = 0;
			}
			posibleEadable = 0;
		}
		for(GHOST g: GHOST.values()) {
			int index = g.ordinal();
			int pos = game.getGhostCurrentNodeIndex(g);
			if(game.wasGhostEaten(g)) {
				distance[index] = 50;
				confidence[index] = 100;
				eadableConfidence[index] = 0;
			}
			else if(pos != -1) {
				if(game.isGhostEdible(g)) {
					// = posibleEadable no?
					eadableConfidence[index] = 100;
				}
				else {
					distance[index] = game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
					confidence[index] = 100;
				}				
			} 
			else{
				if (confidence[index] > 0) confidence[index]-=0.9;
				if (eadableConfidence[index] > 0)eadableConfidence[index] -= 0.5;
			}						
		}
		
	}

	@Override
	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String,Double> vars = new HashMap<String,Double>();
		for(GHOST g: GHOST.values()) {
			vars.put(g.name()+"distance",   distance[g.ordinal()]);
			vars.put(g.name()+"confidence", confidence[g.ordinal()]);		
			vars.put(g.name()+"edible", eadableConfidence[g.ordinal()]);
		}
		vars.put("PosibleEdible", posibleEadable);
		return vars;
	}

}
