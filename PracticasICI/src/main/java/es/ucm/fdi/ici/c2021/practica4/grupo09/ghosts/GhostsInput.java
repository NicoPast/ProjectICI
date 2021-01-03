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

	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String, Double> vars = new HashMap<String, Double>();

		return vars;
	}

	private GHOST ghost;

	public class UsefulData {
		public Vector<interseccion> GhostsPositions;
		public Vector<Double> GhostsPositionsAccuracy;
		public Vector<MOVE> GhostsLastMoveMade;

		public Vector<Double> GhostIsEdibleAccuracy;

		public interseccion proximaInterseccionPacMan;
		public double proximaInterseccionPacManAccuracy;
		public MOVE PacmanLastMoveMade;

		UsefulData(Vector<interseccion> Ghosts, Vector<Double> GhostFuzzy, Vector<Double> EdibleAccuracy,
				interseccion Pacman, double PacmanAccuracy, Vector<MOVE> GhostsMoves, MOVE PacmanMove) {
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
	private double PosPacManAccuracy;
	private MOVE PacmanLastMoveMade;

	public void setGhost(GHOST g) {
		ghost = g;
	}

	public GhostsInput(MapaInfoGhost mapaInfo) {
		this.mapa = mapaInfo;
		this.GhostsPositions = new Vector<interseccion>(4);
		this.GhostsPositionsAccuracy = new Vector<Double>(4);
		this.GhostsLastMoveMade = new Vector<MOVE>(4);
		this.GhostIsEdibleAccuracy = new Vector<Double>(4);
		this.PosPacMan = null;
		this.PosPacManAccuracy = 0.0;
		this.PacmanLastMoveMade = MOVE.NEUTRAL;
		// las inicializamos a valores desconocidos
		for (int i = 0; i < 4; i++) {
			this.GhostsPositions.set(i, null);

			this.GhostsPositionsAccuracy.set(i, 0.0);
			this.GhostsLastMoveMade.set(i, MOVE.NEUTRAL);
			this.GhostIsEdibleAccuracy.set(i, 0.0);
		}
	}

	@Override
	public void parseInput(Game game) {

		mapa.update(game);
		for (int i = 0; i < 4; i++) {
			int fantasmaNode = game.getGhostCurrentNodeIndex(GHOST.values()[i]);
			if (fantasmaNode > -1) {
				GhostsPositions.set(i, mapa.getInterseccion(fantasmaNode));
				GhostsPositionsAccuracy.set(i, 1.0);
				this.GhostsLastMoveMade.set(i, game.getGhostLastMoveMade(GHOST.values()[i]));
			} else
				GhostsPositionsAccuracy.set(i, GhostsPositionsAccuracy.elementAt(i) - .1);
			Boolean edi = game.isGhostEdible(GHOST.values()[i]);
			if (edi != null) {
				GhostIsEdibleAccuracy.set(i, 1.0);
			} else
				GhostIsEdibleAccuracy.set(i, GhostIsEdibleAccuracy.elementAt(i) - .2);
		}
		int PacmanP = game.getPacmanCurrentNodeIndex();
		if (PacmanP > -1) {
			if (mapa.getInterseccion(PacmanP) != null) {
				this.PosPacMan = mapa
						.getInterseccion(mapa.getInterseccion(PacmanP).destinos.get(this.PacmanLastMoveMade));
				this.PosPacManAccuracy = 1.0
						+ (mapa.getInterseccion(PacmanP).distancias.get(this.PacmanLastMoveMade) / 50);
				this.PacmanLastMoveMade = game.getPacmanLastMoveMade();
			} else {
				MOVE best = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), PacmanP,
						game.getGhostLastMoveMade(ghost), DM.EUCLID);
				double distanceToPacman = game.getDistance(game.getGhostCurrentNodeIndex(ghost), PacmanP, DM.PATH);
				double distanceToIntersection = mapa.getInterseccion(game.getGhostCurrentNodeIndex(ghost)).distancias
						.get(best);
				interseccion myInterseccion = mapa.getInterseccion(PacmanP);
				while (distanceToPacman < distanceToIntersection) {
					distanceToPacman -= distanceToIntersection;
					distanceToIntersection += myInterseccion.distancias.get(best);
					myInterseccion = mapa.getInterseccion(myInterseccion.destinos.get(best));
				}
				this.PosPacMan = myInterseccion;
				this.PosPacManAccuracy = 1.0 + distanceToIntersection / 50;
				this.PacmanLastMoveMade = game.getPacmanLastMoveMade();

			}
		} else
			this.PosPacManAccuracy -= .1;

	}

}
