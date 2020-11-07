package es.ucm.fdi.ici.c2021.practica2.grupo09;

import java.util.EnumMap;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostsEdibleTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostsNotEdibleAndPacManFarPPill;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.PacManNearPPillTransition;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.observers.ConsoleFSMObserver;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsFSM extends GhostController {

	EnumMap<GHOST,FSM> fsms;
	MapaInfo mapInfo;

	public GhostsFSM() {	
		mapInfo = new MapaInfo();
		fsms = new EnumMap<GHOST,FSM>(GHOST.class);
		for(GHOST ghost: GHOST.values()) {
			FSM fsm = new FSM(ghost.name());
			fsm.addObserver(new ConsoleFSMObserver(ghost.name()));
			GraphFSMObserver graphObserver = new GraphFSMObserver(ghost.name());
			fsm.addObserver(graphObserver);

			
			SimpleState chase = new SimpleState("chase", new ChaseAction(ghost, mapInfo));
			SimpleState runAway = new SimpleState("runAway", new RunAwayAction(ghost, mapInfo));
			
			GhostsEdibleTransition edible = new GhostsEdibleTransition(ghost);
			PacManNearPPillTransition near = new PacManNearPPillTransition();
			GhostsNotEdibleAndPacManFarPPill toChaseTransition = new GhostsNotEdibleAndPacManFarPPill(ghost);
			
			fsm.add(chase, edible, runAway);
			fsm.add(chase, near, runAway);
			fsm.add(runAway, toChaseTransition, chase);
			
			fsm.ready(chase);
			
			graphObserver.showInFrame(null);
			
			fsms.put(ghost, fsm);			
		}
	}
	
	public void preCompute(String opponent) {
    	for(FSM fsm: fsms.values())
    		fsm.reset();
    }
	
	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST,MOVE> result = new EnumMap<GHOST,MOVE>(GHOST.class);

		GhostsInput in = new GhostsInput(game, mapInfo);	

		for(GHOST ghost: GHOST.values()) {
			FSM fsm = fsms.get(ghost);
			MOVE move = fsm.run(in);
			result.put(ghost, move);
		}
		
		return result;	
	}
}
