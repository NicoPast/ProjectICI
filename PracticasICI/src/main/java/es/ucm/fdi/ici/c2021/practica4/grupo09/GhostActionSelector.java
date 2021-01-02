package es.ucm.fdi.ici.c2021.practica4.grupo09;

import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.GhostsInput.UsefulData;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.FindPacMan;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.GoToActiveGhostAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import pacman.game.Constants.GHOST;

public class GhostActionSelector implements ActionSelector {

	private final float CHASE = 0.4f;
	private final float SEEKHELP = 0.7f;

	private GhostsInput input;
	MapaInfoGhost map;
	private GHOST ghost;
	
	public GhostActionSelector(GhostsInput input, MapaInfoGhost mapInfo){
		this.input = input;
		this.map = mapInfo;
	}

	public void setGhost(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public Action selectAction(HashMap<String, Double> fuzzyOutput) {
		UsefulData data = input.getData();

		Double chase = fuzzyOutput.get("chase");
		Double seekHelp = fuzzyOutput.get("seekHelp");

		//todo falta protect allies

		if(data.GhostIsEdibleAccuracy.elementAt(ghost.ordinal()) >= 1) { //Edible
			if(seekHelp > SEEKHELP)
				return new GoToActiveGhostAction(ghost, map, Actives, edibles, LastPos, PacmanPos, PacmanAccur, lastMoves);
			else
				return new RunAwayAction(ghost, map);		
		}
		else { //Not Edible
			if(chase > CHASE)
				return new ChaseAction(ghost, map, data.proximaInterseccionPacMan, data.PacmanLastMoveMade, data.proximaInterseccionPacManAccuracy);
			else 
			 	return new FindPacMan(ghost, map);
		}
		return null;
	}
}
