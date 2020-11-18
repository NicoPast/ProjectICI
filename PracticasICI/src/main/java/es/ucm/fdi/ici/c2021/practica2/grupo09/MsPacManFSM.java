package es.ucm.fdi.ici.c2021.practica2.grupo09;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions.ChaseAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions.ChillAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions.EatGhostDangerAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions.EatPowerPillAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions.RunAwayAction;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.ComFanPerTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.ComHuirTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.ComPerTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.HuirComTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.HuirTranTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.PerComFanTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.PerTranTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.TranComTransition;
import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions.TranPerTransition;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class NearestPillPacMan.
 */
public class MsPacManFSM extends PacmanController {

	FSM fsm;
	MapaInfo mapInfo;
	
	public MsPacManFSM() {
    	fsm = new FSM("MsPacMan");
    	
    	mapInfo = new MapaInfo();
    	
    	GraphFSMObserver observer = new GraphFSMObserver(fsm.toString());
    	fsm.addObserver(observer);
    	
    	SimpleState chillState = new SimpleState("chillState", new ChillAction(mapInfo));
    	
    	Transition tranCom = new TranComTransition();
    	Transition tranPer = new TranPerTransition();
    	Transition perTran = new PerTranTransition();
    	Transition huirTran = new HuirTranTransition();
    	Transition comPerTran = new ComPerTransition();
    	
    	//Creacion de maquina de estados para usar en el CompoundState
    	FSM cfsm1 = new FSM("Danger");
    	GraphFSMObserver c1observer = new GraphFSMObserver(cfsm1.toString());
    	cfsm1.addObserver(c1observer);
    	
    	SimpleState eatPowerPillState = new SimpleState("eatPowerPillState", new EatPowerPillAction(mapInfo));
    	SimpleState runAwayState = new SimpleState("runAwayState", new RunAwayAction(mapInfo));
    	Transition comHuirTran = new ComHuirTransition();
    	Transition huirComerTran = new HuirComTransition();
    	cfsm1.add(eatPowerPillState, comHuirTran, runAwayState);
    	cfsm1.add(runAwayState, huirComerTran, eatPowerPillState);
    	cfsm1.ready(eatPowerPillState);
    	
    	CompoundState peligroCompoundState = new CompoundState("danger", cfsm1);
    	
    	
    	//Creacion de maquina de estados para usar en el CompoundState
    	FSM cfsm2 = new FSM("Chase");
    	GraphFSMObserver c2observer = new GraphFSMObserver(cfsm2.toString());
    	cfsm2.addObserver(c2observer);
    	
    	SimpleState chaseState = new SimpleState("chaseState", new ChaseAction(mapInfo));
    	SimpleState eatGhostShaseState = new SimpleState("chaseStateDanger", new EatGhostDangerAction(mapInfo));
    	Transition perComFan = new PerComFanTransition();
    	//Transition comFanPer = new ComFanPerTransition();
    	cfsm2.add(chaseState, perComFan, eatGhostShaseState);
    	//cfsm2.add(eatGhostShaseState, comFanPer, chaseState);
    	cfsm2.ready(chaseState);
    	
    	CompoundState perseguirCompoundState = new CompoundState("chase", cfsm2);
    	
    	fsm.add(chillState, tranCom, peligroCompoundState);
    	
    	//cambiar
    	fsm.add(chillState, tranPer, perseguirCompoundState);
    	fsm.add(perseguirCompoundState, perTran, chillState);
    	fsm.add(peligroCompoundState,comPerTran , perseguirCompoundState);    	
    	fsm.add(peligroCompoundState, huirTran, chillState);
    	
    	fsm.ready(chillState);
    	
    	JFrame frame = new JFrame();
    	JPanel main = new JPanel();
    	main.setLayout(new BorderLayout());
    	main.add(observer.getAsPanel(true, null), BorderLayout.CENTER);
    	main.add(c1observer.getAsPanel(true, null), BorderLayout.SOUTH);
    	main.add(c2observer.getAsPanel(true, null), BorderLayout.WEST);
    	frame.getContentPane().add(main);
    	frame.pack();
    	frame.setVisible(true);
	}
	
	
	public void preCompute(String opponent) {
    		fsm.reset();
    }
	
	
	
    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    @Override
    public MOVE getMove(Game game, long timeDue) {
    	Input in = new MsPacManInput(game, mapInfo); 
    	return fsm.run(in);
    } 
}
