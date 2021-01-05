package es.ucm.fdi.ici.practica4.demofuzzy;

import java.util.HashMap;

import es.ucm.fdi.ici.fuzzy.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput implements Input {

	double[] distance = {50,50,50,50};
	double[] confidence = {100,100,100,100};
	double[] eadableConfidence = {0,0,0,0};
	
	
	@Override
	public void parseInput(Game game) {
		for(GHOST g: GHOST.values()) {
			int index = g.ordinal();
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1) {
				if(game.isGhostEdible(g)) {
					
				}
				else {
					distance[index] = game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
					confidence[index] = 100;
				}				
			} else if (confidence[index] > 0)
				confidence[index]-=0.9;
		}
	}

	@Override
	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String,Double> vars = new HashMap<String,Double>();
		for(GHOST g: GHOST.values()) {
			vars.put(g.name()+"distance",   distance[g.ordinal()]);
			vars.put(g.name()+"confidence", confidence[g.ordinal()]);		
			//vars.put(g.name()+"", value)
		}
		return vars;
	}

}
