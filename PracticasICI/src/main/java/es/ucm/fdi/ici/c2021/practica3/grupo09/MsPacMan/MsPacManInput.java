package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan;

import java.util.Collection;
import java.util.Vector;

import es.ucm.fdi.ici.rules.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput extends Input {

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		
	}
	
	private int getNumEadableGhost() {
		int numEadableGhost = 0;
		for(GHOST g : GHOST.values()) {
			if(game.isGhostEdible(g)) {
				numEadableGhost++;
			}
		}
		
		return numEadableGhost;
	}
	
	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();
		facts.add(String.format("(MSPACMAN (numPP %d)(numGhostAlive %d) )",  game.getNumberOfActivePowerPills(), 
				getNumEadableGhost()));
		return facts;
	}
}
