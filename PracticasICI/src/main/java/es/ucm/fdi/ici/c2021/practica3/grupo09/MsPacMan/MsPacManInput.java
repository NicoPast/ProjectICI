package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacMan;

import java.util.Collection;
import java.util.Vector;

import es.ucm.fdi.ici.rules.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput extends Input {
	
	int numEadableGhost = 0;

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		numEadableGhost = 0;
		for(GHOST g : GHOST.values()) {
			if(game.isGhostEdible(g)) numEadableGhost++;
		}
	}
	
	
	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();
		facts.add(String.format("(MSPACMAN (numGhostAlive %s))", numEadableGhost));
		facts.add(String.format("(MSPACMAN (numPowerPills %s))", game.getNumberOfActivePowerPills()));
		
		return facts;
	}
}
