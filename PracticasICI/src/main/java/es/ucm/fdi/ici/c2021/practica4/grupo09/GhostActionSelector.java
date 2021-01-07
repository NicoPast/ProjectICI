package es.ucm.fdi.ici.c2021.practica4.grupo09;

import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.GhostsInput.UsefulData;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.FindPacMan;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.GoToActiveGhostAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.ProtectAlliesAction;
import es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.fuzzy.Action;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import pacman.game.Constants.GHOST;

public class GhostActionSelector implements ActionSelector {

	private final float FIND = 20;
	private final float PROTECT = 22;
	private final float SEEKHELP = 8;

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
		Double protect = fuzzyOutput.get("protect");
		Double seekHelp = fuzzyOutput.get("seekHelp");

		if(data.GhostIsEdibleAccuracy.elementAt(ghost.ordinal()) >= 1) { //Edible
			if(seekHelp > SEEKHELP)
				return new GoToActiveGhostAction(ghost, map,
					data.GhostsPositionsAccuracy, data.GhostIsEdibleAccuracy, 
					data.GhostsPositions, data.proximaInterseccionPacMan, 
					data.proximaInterseccionPacManAccuracy, data.GhostsLastMoveMade);
			else
				return new RunAwayAction(ghost, map, data.GhostsPositionsAccuracy, 
					data.GhostsPositions, data.proximaInterseccionPacMan, 
					data.proximaInterseccionPacManAccuracy);
		}
		else { //Not Edible
			if(protect > PROTECT)
				return new ProtectAlliesAction(ghost, map, data.GhostsPositionsAccuracy, data.GhostIsEdibleAccuracy, data.GhostsPositions, data.GhostsLastMoveMade);
			if(chase < FIND)
				return new FindPacMan(ghost, map);
			else 
				return new ChaseAction(ghost, map, data.proximaInterseccionPacMan, data.PacmanLastMoveMade, data.proximaInterseccionPacManAccuracy);
		}
	}
}
