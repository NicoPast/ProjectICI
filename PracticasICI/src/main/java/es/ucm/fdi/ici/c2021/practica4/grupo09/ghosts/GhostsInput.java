package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.fuzzy.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput implements Input {

	private GHOST ghost;
	private int myPos;

	public class UsefulData {
		public Vector<interseccion> GhostsPositions;
		public Vector<Double> GhostsPositionsAccuracy;
		public Vector<MOVE> GhostsLastMoveMade;

		public Vector<Double> GhostIsEdibleAccuracy;

		public interseccion proximaInterseccionPacMan;
		public float proximaInterseccionPacManAccuracy;
		public MOVE PacmanLastMoveMade;

		UsefulData(Vector<interseccion> Ghosts, Vector<Double> GhostFuzzy, Vector<Double> EdibleAccuracy,
				interseccion Pacman, float PacmanAccuracy, Vector<MOVE> GhostsMoves, MOVE PacmanMove) {
			GhostsPositions = Ghosts;
			GhostsPositionsAccuracy = GhostFuzzy;
			GhostIsEdibleAccuracy = EdibleAccuracy;
			proximaInterseccionPacMan = Pacman;
			proximaInterseccionPacManAccuracy = PacmanAccuracy;
			GhostsLastMoveMade = GhostsMoves;
			PacmanLastMoveMade = PacmanMove;
		}
	}

	public UsefulData getData() {
		return new UsefulData(this.GhostsPositions, this.GhostsPositionsAccuracy, this.GhostIsEdibleAccuracy,
				this.PosPacMan, this.PosPacManAccuracy, this.GhostsLastMoveMade, this.PacmanLastMoveMade);
	}

	private MapaInfoGhost mapa;

	private Vector<interseccion> GhostsPositions;
	private Vector<Double> GhostsPositionsAccuracy;
	private Vector<MOVE> GhostsLastMoveMade;

	private Vector<Double> GhostIsEdibleAccuracy;

	private interseccion PosPacMan;
	private float PosPacManAccuracy;
	private MOVE PacmanLastMoveMade;

	public void setGhost(GHOST g) {
		ghost = g;
	}

	public GhostsInput(MapaInfoGhost mapaInfo) {
		this.mapa = mapaInfo;
		this.GhostsPositions = new Vector<interseccion>();
		this.GhostsPositionsAccuracy = new Vector<Double>();
		this.GhostsLastMoveMade = new Vector<MOVE>();
		this.GhostIsEdibleAccuracy = new Vector<Double>();
		this.PosPacMan = null;
		this.PosPacManAccuracy = 0.0f;
		this.PacmanLastMoveMade = MOVE.NEUTRAL;

		// las inicializamos a valores desconocidos
		for (int i = 0; i < 4; i++) {
			this.GhostsPositions.add(null);

			this.GhostsPositionsAccuracy.add(0.0);
			this.GhostsLastMoveMade.add(MOVE.NEUTRAL);
			this.GhostIsEdibleAccuracy.add(0.0);
		}

		this.distances = new EnumMap<>(GHOST.class);
	}

	private void getGhostsData(Game game){
		for (int i = 0; i < 4; i++) {
			int fantasmaNode = game.getGhostCurrentNodeIndex(GHOST.values()[i]);
			if (fantasmaNode > -1) {
				if(mapa.getInterseccion(fantasmaNode)!=null) {	
					GhostsPositions.set(i, mapa.getInterseccion(fantasmaNode));
					GhostsPositionsAccuracy.set(i, 1.0);
					this.GhostsLastMoveMade.set(i, game.getGhostLastMoveMade(GHOST.values()[i]));
				}
			} else{
				double ac = GhostsPositionsAccuracy.elementAt(i) - .02;
				if(ac < 0) ac = 0;
				GhostsPositionsAccuracy.set(i, ac);
			}
			Boolean edi = game.isGhostEdible(GHOST.values()[i]);
			if (edi != null) {
				if(edi)
					GhostIsEdibleAccuracy.set(i, 1.0);
				else
					GhostIsEdibleAccuracy.set(i, 0.0);
			} else{
				double ac = GhostIsEdibleAccuracy.elementAt(i) - .02;
				if(ac < 0) ac = 0;
				GhostIsEdibleAccuracy.set(i, ac);
			}
		}
	}

	private void getPacManData(Game game){
		int PacmanP = game.getPacmanCurrentNodeIndex();
		if (PacmanP > -1) {
			if (mapa.getInterseccion(PacmanP) != null) {
				interseccion aux = mapa.getInterseccion(PacmanP);
				this.PosPacMan = aux;
				this.PosPacManAccuracy = 1.0f;
				this.PacmanLastMoveMade = game.getPacmanLastMoveMade();
			} else {
				
				MOVE best = game.getNextMoveTowardsTarget(this.myPos, PacmanP, DM.EUCLID);
				double distanceToPacman = game.getDistance(this.myPos, PacmanP, DM.PATH);

				//si myInterseccion sale null es que estoy en un pasillo as� que cojo la �ltima del vector
				interseccion myInterseccion = mapa.getInterseccion(this.myPos);
				if(myInterseccion == null && GhostsPositions.elementAt(ghost.ordinal()) != null 
						&& GhostsPositions.elementAt(ghost.ordinal()).destinos.containsKey(best)){

					myInterseccion = GhostsPositions.elementAt(ghost.ordinal());
					
					
					float distanceToIntersection = myInterseccion.distancias.get(best) - (float)game.getDistance(myPos, myInterseccion.identificador, DM.PATH);
					
					while (myInterseccion.destinos.containsKey(best) && distanceToPacman > distanceToIntersection) {
						myInterseccion = mapa.getInterseccion(myInterseccion.destinos.get(best));
						distanceToPacman -= distanceToIntersection;
						if(myInterseccion.distancias.get(best) == null)
							break;
						distanceToIntersection = myInterseccion.distancias.get(best);
					}
					
					if(game.getPacmanLastMoveMade() == best.opposite())
						myInterseccion = mapa.getInterseccion(myInterseccion.destinos.get(best.opposite()));
					distanceToIntersection = (float)game.getDistance(PacmanP, myInterseccion.identificador, DM.PATH);
					
					this.PosPacMan = myInterseccion;
					this.PosPacManAccuracy = 1.0f ;
					this.PacmanLastMoveMade = game.getPacmanLastMoveMade();
				}
				else {
					
				}
			}
		} else{
			this.PosPacManAccuracy -= .1f;
			if(this.PosPacManAccuracy < 0) this.PosPacManAccuracy = 0;
		}
	}

	@Override
	public void parseInput(Game game) {
		mapa.update(game);
		
		this.myPos = game.getGhostCurrentNodeIndex(ghost);

		getGhostsData(game);

		getPacManData(game);
		
		getFuzzyData(game);
	}

	private void getFuzzyData(Game game){
		for(GHOST g: GHOST.values()) {
			//Distances
			if(this.GhostsPositions.elementAt(g.ordinal()) != null)
				distances.put(g, game.getDistance(game.getGhostCurrentNodeIndex(ghost), this.GhostsPositions.elementAt(g.ordinal()).identificador, 
								 game.getGhostLastMoveMade(ghost), DM.PATH));
			else 
				distances.put(g, 250.0);
		}

		if(this.PosPacMan != null){
			this.distanceToPacMan = game.getDistance(game.getGhostCurrentNodeIndex(ghost), this.PosPacMan.identificador, 
														game.getGhostLastMoveMade(ghost), DM.PATH);
		}
		else 
			this.distanceToPacMan = 250.0;
	}


	EnumMap<GHOST, Double> distances;
	double distanceToPacMan;

	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String, Double> vars = new HashMap<String, Double>();

		for(GHOST g: GHOST.values()) {
			vars.put(g.name()+"distance", this.distances.get(g));
			vars.put(g.name()+"confidence", this.GhostsPositionsAccuracy.elementAt(g.ordinal()) * 100);
			vars.put(g.name()+"edible", this.GhostIsEdibleAccuracy.elementAt(g.ordinal()) * 100);
		}
		vars.put("PACMANdistance", this.distanceToPacMan);
		vars.put("PACMANconfidence", (double)this.PosPacManAccuracy * 100);

		return vars;
	}

}
