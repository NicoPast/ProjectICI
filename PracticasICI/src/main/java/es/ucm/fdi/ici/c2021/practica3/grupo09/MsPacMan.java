package es.ucm.fdi.ici.c2021.practica3.grupo09;

import java.util.HashMap;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.MsPacManInput;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.ChaseMsPacManAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.ChillAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.EatPowePillAction;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules.actions.RunAwayMsPacManAction;
import es.ucm.fdi.ici.rules.Action;
import es.ucm.fdi.ici.rules.Input;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

class interseccion {
	public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest, EnumMap<MOVE, Integer> pi,
			EnumMap<MOVE, Integer> ppi) {
		identificador = iden;
		distancias = dir;
		destinos = dest;
		pills = pi;
		powerPill = ppi;
	}

	public int identificador; // node index
	public EnumMap<MOVE, Integer> distancias; // distancias
	public EnumMap<MOVE, Integer> destinos; // identificador del nodo en esa direccion
	public EnumMap<MOVE, Integer> pills; // pills en ese camino (entre nodo y nodo, las pills que hay en las
											// intersecciones no cuentan)
	public EnumMap<MOVE, Integer> powerPill; // powerPills en ese camino
}



public final class MsPacMan extends PacmanController {
	private List<interseccion> mapa = new ArrayList<interseccion>();
	boolean mapaHecho = false;
	int ultimoNodo = -1, proximoNodo = -1; // -1 es que aun no ha registrado nada
	MOVE ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
	MOVE movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
	interseccion interseccionActual;
	String mapaActual = "a";

	DM metrica = DM.MANHATTAN;
	double distanciaPeligro = 40;
	double distanciaPerseguir = 140;


	// provisional

	private int[] buscaCamino(Node nodoActual, MOVE dir, Node[] graph) {
		MOVE direccion = dir;
		int pills = 0;
		int powerPills = 0;

		Node proximoNodo = graph[nodoActual.neighbourhood.get(direccion)];
		int coste = 1;

		while ((proximoNodo.numNeighbouringNodes <= 2)) {
			if (proximoNodo.neighbourhood.get(direccion) == null) { // en que otra direccion nos podemos mover
				for (MOVE m : MOVE.values()) { // ya tenemos la nueva direccion
					if (m != direccion.opposite() && proximoNodo.neighbourhood.get(m) != null) {
						direccion = m;
						break;
					}
				}
			}
			if (proximoNodo.pillIndex != -1)
				pills++;
			else if (proximoNodo.powerPillIndex != -1)
				powerPills++;
			proximoNodo = graph[proximoNodo.neighbourhood.get(direccion)];
			coste++;
		}
		return new int[] { coste, proximoNodo.nodeIndex, pills, powerPills };
	}

	private void crearMapa(Game game) {
		Node[] graph = game.getCurrentMaze().graph;
		
		for (Node nodo : graph) { // recorre todos los nodos del mapa
			if (nodo.numNeighbouringNodes > 2) { // quita muro y pasillos

public class MsPacMan  extends PacmanController {
	
	
	HashMap<String,Action> map;
	private MapaInfo mapInfo = null;
	RuleEngine msPacManRuleEngine;
	
	ChaseMsPacManAction chaseAction;
	ChillAction chillAction;
	EatPowePillAction eatPowerPillAction;
	RunAwayMsPacManAction runAwayAction;
	
	public MsPacMan() {
		map = new HashMap<String,Action>();
    	mapInfo = new MapaInfo();
		
    	chaseAction = new ChaseMsPacManAction(mapInfo);
    	chillAction = new ChillAction(mapInfo);
    	eatPowerPillAction = new EatPowePillAction(mapInfo);
    	runAwayAction =  new RunAwayMsPacManAction(mapInfo);
    	
		Action eatghost = chaseAction;
		Action chill = chillAction;
		Action eatPP = eatPowerPillAction;
		Action runaway = runAwayAction;
		
		map.put("EatGhost", eatghost);
		map.put("Chill", chill);
		map.put("EatPowerPill", eatPP);
		map.put("RunAway", runaway);
		
		
		msPacManRuleEngine = new RuleEngine("MsPacManEngine","es/ucm/fdi/ici/c2021/practica3/grupo09/MsPacManRules/mspacmanrules.clp", map);
	
	
		//ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver("MsPacMan", true);
		//msPacManRuleEngine.addObserver(observer);
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {		
		//Process input
		Input input = new MsPacManInput(game, mapInfo);
		//load facts
		//reset the rule engines
		msPacManRuleEngine.reset();
		msPacManRuleEngine.assertFacts(input.getFacts());
				
				
		return msPacManRuleEngine.run(game);				
	}

	@Override
	public  void postCompute() {
		mapInfo = null;
    	mapInfo = new MapaInfo();
    	
    	chillAction.setMap(mapInfo);
    	chaseAction.setMap(mapInfo);
    	eatPowerPillAction.setMap(mapInfo);
    	runAwayAction.setMap(mapInfo);		
    }

}
