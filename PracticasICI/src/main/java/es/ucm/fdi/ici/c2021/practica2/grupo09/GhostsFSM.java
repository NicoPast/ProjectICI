package es.ucm.fdi.ici.c2021.practica2.grupo09;

import java.awt.BorderLayout;
import java.util.EnumMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.CheckMateAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.GoToActiveGhostAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.PrisonerAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.ProtectAlliesAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions.SecurePPillAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostCanBeProtectedTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostCanProtectAllyTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostCanSecurePPillTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostCannotProtectAlly;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostDiedTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostFarFromActiveGhostTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostNotSecuringPPillTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostRespawnedTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostsEdibleTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.GhostsNotEdibleAndPacManFarPPill;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions.IsCheckMateTransition;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.observers.ConsoleFSMObserver;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsFSM extends GhostController {

	EnumMap<GHOST, FSM> fsms;
	MapaInfo mapInfo;

	public GhostsFSM() {
		mapInfo = new MapaInfo();
		fsms = new EnumMap<GHOST, FSM>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			FSM fsm = new FSM(ghost.name());
//			fsm.addObserver(new ConsoleFSMObserver(ghost.name()));
			GraphFSMObserver gweak = new GraphFSMObserver(ghost.name() + "weak");
			GraphFSMObserver gchase = new GraphFSMObserver(ghost.name()+"chase");
//			GraphFSMObserver graphObserver = new GraphFSMObserver(ghost.name());
//			fsm.addObserver(graphObserver);

			SimpleState checkMate = new SimpleState("checkMate", new CheckMateAction(ghost, mapInfo));
			SimpleState goToActive = new SimpleState("goToActive", new GoToActiveGhostAction(ghost, mapInfo));
			SimpleState prisoner = new SimpleState("prisoner", new PrisonerAction());
			SimpleState protectAllies = new SimpleState("protect", new ProtectAlliesAction(ghost, mapInfo));
			SimpleState securePPill = new SimpleState("secure", new SecurePPillAction(ghost, mapInfo));
			SimpleState chase = new SimpleState("chase", new ChaseAction(ghost, mapInfo));
			SimpleState runAway = new SimpleState("runAway", new RunAwayAction(ghost, mapInfo));

			GhostsEdibleTransition edible = new GhostsEdibleTransition(ghost);
			// PacManNearPPillTransition near = new PacManNearPPillTransition(); ¿no se
			// usaba?
			GhostsNotEdibleAndPacManFarPPill toChaseTransition = new GhostsNotEdibleAndPacManFarPPill(ghost);
			GhostCanBeProtectedTransition cbp = new GhostCanBeProtectedTransition(ghost, mapInfo);
			GhostCannotProtectAlly cannotProtect = new GhostCannotProtectAlly(ghost);
			GhostCanProtectAllyTransition canProtect = new GhostCanProtectAllyTransition(ghost);
			GhostCanSecurePPillTransition canSecure = new GhostCanSecurePPillTransition(ghost);
			GhostDiedTransition died = new GhostDiedTransition(ghost);
			GhostFarFromActiveGhostTransition far = new GhostFarFromActiveGhostTransition(ghost, mapInfo);
			GhostNotSecuringPPillTransition notsec = new GhostNotSecuringPPillTransition(ghost);
			GhostRespawnedTransition respawn = new GhostRespawnedTransition(ghost);
			IsCheckMateTransition checkmate = new IsCheckMateTransition(mapInfo);

			FSM fsmChase = new FSM(ghost.name()+"chase");
			fsmChase.addObserver(gchase);
			fsmChase.add(chase, checkmate, checkMate);
			fsmChase.add(protectAllies, checkmate, checkMate);
			fsmChase.add(securePPill, checkmate, checkMate);
			fsmChase.add(chase, canProtect, protectAllies);
			fsmChase.add(protectAllies, cannotProtect, chase);
//			fsmChase.add(protectAllies, canSecure, securePPill);
			fsmChase.add(securePPill, canProtect, protectAllies);
			fsmChase.add(chase, canSecure, securePPill);
			fsmChase.add(securePPill, notsec, chase);
			fsmChase.ready(chase);

			FSM fsmWeak = new FSM(ghost.name()+"weak");
			fsmWeak.addObserver(gweak);
			fsmWeak.add(runAway, cbp, goToActive);
			fsmWeak.add(goToActive, far, runAway);
			fsmWeak.ready(runAway);


			CompoundState weak = new CompoundState("weak", fsmWeak);
			CompoundState intouchable = new CompoundState("intouchable", fsmChase);

//			graphObserver.showInFrame(null);

			fsm.add(intouchable, edible, weak);
			fsm.add(weak, toChaseTransition, intouchable);

			fsm.add(prisoner, respawn, intouchable);
			fsm.add(weak, died, prisoner);
			
//			gweak.showInFrame(null);
			fsm.ready(prisoner);
			fsms.put(ghost, fsm);
			
			JFrame frame = new JFrame();
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			main.add(gchase.getAsPanel(true, null), BorderLayout.CENTER);
			main.add(gweak.getAsPanel(true, null), BorderLayout.SOUTH);
			frame.getContentPane().add(main);
			frame.pack();
			frame.setVisible(true);
		}
	}

	public void preCompute(String opponent) {
		for (FSM fsm : fsms.values())
			fsm.reset();
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		GhostsInput in = new GhostsInput(game, mapInfo);

		for (GHOST ghost : GHOST.values()) {
			FSM fsm = fsms.get(ghost);
			MOVE move = fsm.run(in);
			result.put(ghost, move);
		}

		return result;
	}
}
