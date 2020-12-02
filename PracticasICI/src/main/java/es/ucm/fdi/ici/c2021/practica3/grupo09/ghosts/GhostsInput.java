package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.rules.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends Input {

	public class ClosestPowerPillAndDistance {
		public int powerpill = 0;
		public double distance = Double.MAX_VALUE;
	}

	public class NODEANDDISTANCE {
		public int n;
		public double d;

		public NODEANDDISTANCE(int no, double di) {
			n = no;
			d = di;
		}
	}

	private class interseccion_plus {
		public interseccion intersection;
		public interseccion prohibida;

		public interseccion_plus(interseccion i, interseccion iProhibida) {
			intersection = i;
			prohibida = iProhibida;
		}
	};

	private class GHOSTANDDISTANCE {
		public GHOST ghost;
		public double distance = Double.MAX_VALUE;
	}

	private MapaInfoGhost mapa;

	private ClosestPowerPillAndDistance cppad_PacMan;
	private EnumMap<GHOST, ClosestPowerPillAndDistance> cppad_Ghosts;

	private EnumMap<GHOST, Double> distanceToPacMan;

	//private interseccion proximaInterseccionPacMan;

	private int ppillsLeft;

	// --------------------------------constants--------------------------------------------
	private int PacmanPPillTreshold = 25;
	private int GhostsClosePacmanTreshold = 25;
	private int CONST_LIMIT_DISTANCE_SEEK_PROTECTION = 25;
	private int CONST_PACMAN_MIN_DISTANCE_CAN_PROTECT = 25;
	private int MAX_DISTANCE_TO_WEAK_GHOST = 25;

	// --------------------------------bools weak-----------------------------------------

	private boolean isGhostWeak(GHOST ghost) {
		// es comestible o el pacman est� lo suficientemente cerca de la ppill y de �l
		return game.isGhostEdible(ghost)
				|| (isPacManCloserToAnyPowerPill && cppad_PacMan.distance < PacmanPPillTreshold && this.distanceToPacMan.get(ghost) < GhostsClosePacmanTreshold);
	}

	private boolean GhostCanSeekProtection(GHOST ghost) {
		if (this.isGhostWeak(ghost) && !activeGhosts.isEmpty()) {
			interseccion inter = this.mapa.getInterseccion(game.getGhostCurrentNodeIndex(ghost));

			if (inter != null) {
				int[] posGhosts = new int[3];
				int i = 0;
				i = 0;
				// rellenamos las posiciones de los fantasmas activos para ver a que distancia
				// esta el mas cercano
				for (GHOST gh : activeGhosts) {
					if (gh == ghost)
						continue;
					posGhosts[i] = game.getGhostCurrentNodeIndex(gh);
					i++;
				}

				MOVE prohibido = this.GetMoveToPacman(ghost);
				double nearest = 0;
				// elegimos el fantasma mas cercano buscando en todas direcciones excepto
				// en la prohibida
				for (MOVE move : inter.destinos.keySet()) {
					if (move == prohibido)
						continue;
					double aux = this.nearestGhostDistance(game.getGhostCurrentNodeIndex(ghost), posGhosts, move).d;
					if (aux < nearest) {
						nearest = aux;

					}
				}
				// si hay algun fantasma activo lo suficientemente cerca, hay que perseguirlo
				// para que si viene el pacman el fantasma lo mate
				return nearest < CONST_LIMIT_DISTANCE_SEEK_PROTECTION;

			}
			// si no hay interseccion estoy en un pasillo y se sigue con el movimiento
			// actual.
			else
				return false;
		} else
			return false;
	}

	private boolean isPacManCloserToAnyPowerPill;

	EnumMap<GHOST,Boolean> hasToRun;
	
	EnumMap<GHOST,Boolean> canSeekProtection;

	// --------------------------------bools strong----------------------------------------
	private boolean isGhostStrong(GHOST ghost) {
		return !game.isGhostEdible(ghost) && 
			!(isPacManCloserToAnyPowerPill && cppad_PacMan.distance < PacmanPPillTreshold && this.distanceToPacMan.get(ghost) < GhostsClosePacmanTreshold);
	}
	
	private boolean calculateCheckMate() {
		if (isPacManCloserToAnyPowerPill)
			return false;

		Set<interseccion_plus> visitadas = new HashSet<interseccion_plus>();
		// rellenamos un array con los nodos de los fantasmas
		if (activeGhosts.isEmpty())
			return false;

		Vector<Integer> nodosFijos = new Vector<Integer>();

		interseccion_plus[] aux = new interseccion_plus[6];
		rellenarProxDestinosPacMan(visitadas, nodosFijos);

		if (!visitadas.isEmpty())
			visitadas.toArray(aux);

		int i = 0;
		while (activeGhosts.size() > 0 && visitadas.size() > 0 && activeGhosts.size() - visitadas.size() >= 0
				&& i < visitadas.size()) {
			GHOSTANDDISTANCE gyd = closestGhostToIntersection(game, aux[i].intersection.identificador, activeGhosts);
			if (gyd.distance <= 1) {
				mapa.movesCheckMate.put(gyd.ghost, game.getPacmanCurrentNodeIndex());
				activeGhosts.remove(gyd.ghost);
				nodosFijos.add(game.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			} else if (gyd.distance <= game.getDistance(game.getPacmanCurrentNodeIndex(),
					aux[i].intersection.identificador, game.getPacmanLastMoveMade(), DM.PATH)) {
				mapa.movesCheckMate.put(gyd.ghost, aux[i].intersection.identificador);
				activeGhosts.remove(gyd.ghost);
				nodosFijos.add(game.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			} else {
				rellenarProxDestinosPacMan(visitadas, nodosFijos);
				visitadas.toArray(aux);
				i = 0;
			}
		}
		return visitadas.size() - i == 0;
	}

	private boolean canSecurePPill(GHOST ghost){
		if(ppillsLeft == 0){
			return false;
		}

		ClosestPowerPillAndDistance my_cpad = cppad_Ghosts.get(ghost);
		double dist_pacman = game.getDistance(game.getPacmanCurrentNodeIndex(), my_cpad.powerpill, game.getPacmanLastMoveMade(), DM.PATH);

		if(dist_pacman < my_cpad.distance) //Si el pacman llega antes que yo a mi powerpill, no puedo asegurarla
			return false;

		for(ClosestPowerPillAndDistance cpad : cppad_Ghosts.values()){
			if(cpad.distance == my_cpad.distance && cpad.powerpill == my_cpad.powerpill) //No me valoro a mi mismo
				continue;
			//Si alguien está más cerca de la powerpill, que la asegure el otro, no yo
			if(cpad.powerpill == my_cpad.powerpill && cpad.distance < my_cpad.distance){ 
				return false;	
			}
		}
		//Si soy el más cercano a la powerpill más cercana a mi, la aseguro
		return true;
	}

	private boolean GhostCanProtectAlly(GHOST ghost) {
		if (isGhostStrong(ghost) && this.isPacManCloserToAnyPowerPill) {
			if (game.doesGhostRequireAction(ghost) && !game.isGhostEdible(ghost)) {
								// si no hay fantasmas comestibles no hay que proteger nada
				if (edibleGhosts.isEmpty())
					return false;
				int[] ediblePos = new int[edibleGhosts.size()];
				// rellenamos un array con las posiciones los fantasmas comestibles
				for (int i = 0; i < edibleGhosts.size(); i++) {
					ediblePos[i] = game.getGhostCurrentNodeIndex(edibleGhosts.elementAt(i));
				}
				// vemos cual es la distancia al fantasma edible mas cercano del pacman para su
				// ultimo movimiento
				NODEANDDISTANCE nearestNodeAndDistance = nearestGhostDistance(game.getPacmanCurrentNodeIndex(),
						ediblePos, game.getPacmanLastMoveMade());
				double nearest = nearestNodeAndDistance.d;
				// No hace falta comprobar si yo estoy mas cerca de el que el pacman
				// porque el otro fantasma tratara de acercarse a mi pero conviene comprobar
				// que estoy lo suficientemente cerca

				return nearest < CONST_PACMAN_MIN_DISTANCE_CAN_PROTECT && game.getDistance(game.getGhostCurrentNodeIndex(ghost),
						nearestNodeAndDistance.n, DM.EUCLID) < MAX_DISTANCE_TO_WEAK_GHOST;

			} else
				return false;
		}
		else return false;
	}

	private boolean isCheckMate;
	private boolean pacManEaten;

	private EnumMap<GHOST, Boolean> strong, canProtect, canSecurePPill;

	
	// -------------------------------------------------------------------------------------
	private Vector<GHOST> activeGhosts;
	private Vector<GHOST> edibleGhosts;

	public GhostsInput(Game game, MapaInfoGhost mapaInfo) {
		super(game);
		this.mapa = mapaInfo;

		//STRONG
		this.strong = new EnumMap<>(GHOST.class);
		this.canSecurePPill = new EnumMap<>(GHOST.class);
		this.canProtect = new EnumMap<>(GHOST.class);

		//WEAK
		this.hasToRun=new EnumMap<>(GHOST.class);
		this.canSeekProtection=new EnumMap<>(GHOST.class);
		
		parseInput();
	}

	@Override
	public void parseInput() {
		if (mapa == null) // Hecho asi porque parseInput se llama en super(game) pero necesitamos el mapa
			return;

		mapa.update(game);

		getActiveGhosts_();
		getEdibleGhosts_();

		// En caso de haber jaqueMate, la solucion se guarda en mapaInfo
		isCheckMate = calculateCheckMate();

		initCppads();

		// proximaInterseccionPacMan = null;
		// if (mapa.getCheckLastModeMade())
		// 	proximaInterseccionPacMan = mapa.getInterseccionActual();
		// else if (mapa.getInterseccionActual() != null
		// 		&& mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()) != null)
		// 	proximaInterseccionPacMan = mapa
		// 			.getInterseccion(mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()));

		isPacManCloserToAnyPowerPill = isPacManCloserToPowerPill(99999);

		this.ppillsLeft = game.getNumberOfActivePowerPills();

		this.pacManEaten = game.wasPacManEaten();

		getDistancesToPacMan();

		for(GHOST ghost : GHOST.values())
		{			
			this.strong.put(ghost, isGhostStrong(ghost));
			this.canSecurePPill.put(ghost, canSecurePPill(ghost));
			this.canProtect.put(ghost, GhostCanProtectAlly(ghost));

			this.hasToRun.put(ghost, isGhostWeak(ghost));
			this.canSeekProtection.put(ghost, GhostCanSeekProtection(ghost));
		}
	}

	private MOVE GetMoveToPacman(GHOST ghost) { // Usado en GhostCanBeProtected & GhostFarFromActiveGhost
		return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
				game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.EUCLID);
	}

	private NODEANDDISTANCE nearestGhostDistance(int myPos, int[] pos, MOVE m) {
		int nearestP = -1;
		double nearestDist = Double.MAX_VALUE;
		for (int p : pos) {
			double aux = game.getDistance(myPos, p, m, DM.PATH);
			if (aux < nearestDist) {
				nearestDist = aux;
				nearestP = p;
			}
		}

		return new NODEANDDISTANCE(nearestP, nearestDist);
	}

	private void getActiveGhosts_() {
		activeGhosts = new Vector<>();
		for (GHOST ghostType : GHOST.values()) {
			if (!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType) <= 0)
				activeGhosts.add(ghostType);
		}
	}

	private void getEdibleGhosts_() {
		edibleGhosts = new Vector<GHOST>();
		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost))
				edibleGhosts.add(ghost);
		}
	}

	private void getDistancesToPacMan() {
		distanceToPacMan = new EnumMap<>(GHOST.class);

		for (GHOST g : GHOST.values()) {
			distanceToPacMan.put(g, game.getDistance(game.getGhostCurrentNodeIndex(g), game.getPacmanCurrentNodeIndex(),
					game.getGhostLastMoveMade(g), DM.PATH));
		}
	}

	private GHOSTANDDISTANCE closestGhostToIntersection(Game game, int inter, Vector<GHOST> libres) {
		GHOSTANDDISTANCE gyd = new GHOSTANDDISTANCE();
		for (GHOST ghost : libres) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), inter, game.getGhostLastMoveMade(ghost),
					DM.PATH);
			if (aux < gyd.distance) {
				gyd.ghost = ghost;
				gyd.distance = aux;
			}
		}
		return gyd;
	}

	private void rellenarProxDestinosPacMan(Set<interseccion_plus> inters, Vector<Integer> nodosFijos) {
		interseccion intActual = mapa.getInterseccionActual();
		if (inters.isEmpty()) {
			if (mapa.getCheckLastModeMade())
				inters.add(new interseccion_plus(intActual, intActual));
			else
				inters.add(new interseccion_plus(mapa.getInterseccion(intActual.destinos.get(mapa.getUltimoMovReal())), intActual));
		} else {
			Set<interseccion_plus> aux = new HashSet<interseccion_plus>(inters);
			inters.clear();
			// Buscamos las intersecciones correspondientes a los movimientos posibles
			// y si ya está esa intersección fijada no expandimos su rama
			for (interseccion_plus a : aux) {
				if (!nodosFijos.contains(a.intersection.identificador)) {
					for (MOVE mo : a.intersection.destinos.keySet()) {
						if (mapa.getInterseccion(a.intersection.destinos.get(mo)) != a.prohibida) {
							inters.add(new interseccion_plus(mapa.getInterseccion(a.intersection.destinos.get(mo)),
									a.intersection));
						}
					}
				}
			}
		}
	}

	private void initCppads() {
		this.cppad_PacMan = getClosestPowerPillAndDistance(game.getPacmanCurrentNodeIndex(),
				game.getPacmanLastMoveMade());
		cppad_Ghosts = new EnumMap<>(GHOST.class);
		for (GHOST g : GHOST.values()) {
			cppad_Ghosts.put(g,
					getClosestPowerPillAndDistance(game.getGhostCurrentNodeIndex(g), game.getGhostLastMoveMade(g)));
		}
	}

	private ClosestPowerPillAndDistance getClosestPowerPillAndDistance(int pos, MOVE lastMoveMade) {
		ClosestPowerPillAndDistance cpad = new ClosestPowerPillAndDistance();
		for (int currentPill : game.getActivePowerPillsIndices()) {
			double aux = game.getDistance(pos, currentPill, lastMoveMade, DM.PATH);
			if (aux < cpad.distance) {
				cpad.distance = aux;
				cpad.powerpill = currentPill;
			}
		}
		return cpad;
	}

	private boolean isPacManCloserToPowerPill(int limit) {
		// Solo miro si esta en el rango del fairplay
		double distMin = Double.MAX_VALUE;
		if (cppad_PacMan.distance < limit) {
			for (GHOST ghostType : activeGhosts) {
				double dist = game.getDistance(game.getGhostCurrentNodeIndex(ghostType), cppad_PacMan.powerpill,
						game.getGhostLastMoveMade(ghostType), DM.PATH);
				distMin = Math.min(dist, distMin);
			}
			return distMin > cppad_PacMan.distance && cppad_PacMan.distance < limit * 1.55;
		}
		return false;
	}

	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();
		// facts.add(String.format("(INKY (edible %s))", this.INKYedible));
		// facts.add(String.format("(PINKY (edible %s))", this.PINKYedible));
		// facts.add(String.format("(SUE (edible %s))", this.SUEedible));
		// facts.add(String.format("(MSPACMAN (mindistancePPill %d))",
		// (int)this.minPacmanDistancePPill));

		//Strongs
		facts.add(String.format("(BLINKY (strong %s))", this.strong.get(GHOST.BLINKY)));
		facts.add(String.format("(PINKY (strong %s))", this.strong.get(GHOST.PINKY)));
		facts.add(String.format("(INKY (strong %s))", this.strong.get(GHOST.INKY)));
		facts.add(String.format("(SUE (strong %s))", this.strong.get(GHOST.SUE)));

		//Can secure ppill
		facts.add(String.format("(BLINKY (canSecurePPill %s))", this.canSecurePPill.get(GHOST.BLINKY)));
		facts.add(String.format("(PINKY (canSecurePPill %s))", this.canSecurePPill.get(GHOST.PINKY)));
		facts.add(String.format("(INKY (canSecurePPill %s))", this.canSecurePPill.get(GHOST.INKY)));
		facts.add(String.format("(SUE (canSecurePPill %s))", this.canSecurePPill.get(GHOST.SUE)));

		//Can protect ally
		facts.add(String.format("(BLINKY (canProtectAlly %s))", this.canProtect.get(GHOST.BLINKY)));
		facts.add(String.format("(PINKY (canProtectAlly %s))", this.canProtect.get(GHOST.PINKY)));
		facts.add(String.format("(INKY (canProtectAlly %s))", this.canProtect.get(GHOST.INKY)));
		facts.add(String.format("(SUE (canProtectAlly %s))", this.canProtect.get(GHOST.SUE)));

		facts.add(String.format("(CHECKMATE (isCheckMate %s))", this.isCheckMate));
		facts.add(String.format("(MSPACMAN (wasMsPacManEaten %s))", this.pacManEaten));

		facts.add(String.format("(BLINKY (run %s))", this.hasToRun.get(GHOST.BLINKY)));
		facts.add(String.format("(INKY (run %s))", this.hasToRun.get(GHOST.INKY)));
		facts.add(String.format("(PINKY (run %s))", this.hasToRun.get(GHOST.PINKY)));
		facts.add(String.format("(SUE (run %s))", this.hasToRun.get(GHOST.SUE)));
		
		facts.add(String.format("(BLINKY (seekProtection %s))", this.canSeekProtection.get(GHOST.BLINKY)));
		facts.add(String.format("(INKY (seekProtection %s))", this.canSeekProtection.get(GHOST.INKY)));
		facts.add(String.format("(PINKY (seekProtection %s))", this.canSeekProtection.get(GHOST.PINKY)));
		facts.add(String.format("(SUE (seekProtection %s))", this.canSeekProtection.get(GHOST.SUE)));
		return facts;
	}
}
