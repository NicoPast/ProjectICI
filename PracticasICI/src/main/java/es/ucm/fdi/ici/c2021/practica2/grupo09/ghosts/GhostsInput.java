package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.c2021.practica2.grupo09.auxiliarClasses.GHOSTANDDISTANCE;
import es.ucm.fdi.ici.fsm.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends Input {

	public class ClosestPowerPillAndDistance{
		public int powerpill = 0;
		public double distance =  Double.MAX_VALUE;
	}

	public class NODEANDDISTANCE{
		public int n;
		public double d;

		public NODEANDDISTANCE(int no, double di) {
			n = no;
			d = di;
		}
	}

	private MapaInfo mapa;

	private ClosestPowerPillAndDistance cppad_PacMan;
	private EnumMap<GHOST, ClosestPowerPillAndDistance> cppad_Ghosts;

	private interseccion proximaInterseccionPacMan;
	private boolean isPacManCloserToAnyPowerPill;

	private int ppillsLeft;

	//Usados con los metodos checkMates, porque la informacion ahi les sirve a todos
	public boolean isCheckMate;

	public boolean pacManEaten;
	
	public GhostsInput(Game game, MapaInfo mapaInfo) {
		super(game);
		this.mapa = mapaInfo;
		parseInput();
	}

	@Override
	public void parseInput() {
		if(mapa == null)
			return;

		mapa.update(game);

		isCheckMate = calculateCheckMate();

		initCppads();

		proximaInterseccionPacMan = null;
		if(mapa.getCheckLastModeMade())
			proximaInterseccionPacMan = mapa.getInterseccionActual();
		else if(mapa.getInterseccionActual() != null && mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()) != null) 
			proximaInterseccionPacMan = mapa.getInterseccion(mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()));

		isPacManCloserToAnyPowerPill = isPacManCloserToPowerPill(99999);

		this.ppillsLeft = game.getNumberOfActivePowerPills();

		this.pacManEaten = game.wasPacManEaten();
	}

	private class interseccion_plus{
		public interseccion intersection;
		public interseccion prohibida;
		public interseccion_plus(interseccion i, interseccion iProhibida) { intersection = i; prohibida = iProhibida;}
	};

	private boolean calculateCheckMate(){
		if(isPacManCloserToPowerPill()) 
			return false;

		Set<interseccion_plus> visitadas = new HashSet<interseccion_plus>();
		// rellenamos un array con los nodos de los fantasmas
		Vector<GHOST> ghosts = getActiveGhosts();
		if(ghosts.isEmpty())
			return false;

		Vector<Integer> nodosFijos = new Vector<Integer>();

		interseccion_plus[] aux = new interseccion_plus[6];
		rellenarProxDestinosPacMan(visitadas, nodosFijos);
		
		if(!visitadas.isEmpty())
			visitadas.toArray(aux);

		int i = 0;
		while (ghosts.size() > 0 && visitadas.size() > 0 && ghosts.size() - visitadas.size() >= 0 && i < visitadas.size()) {
			GHOSTANDDISTANCE gyd = closestGhostToIntersection(game, aux[i].intersection.identificador, ghosts);
			if(gyd.distance <= 1){
				mapa.movesCheckMate.put(gyd.ghost, game.getPacmanCurrentNodeIndex());
				ghosts.remove(gyd.ghost);
				nodosFijos.add(game.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			}
			else if (gyd.distance <= game.getDistance(game.getPacmanCurrentNodeIndex(), aux[i].intersection.identificador, game.getPacmanLastMoveMade(), DM.PATH)) {
				mapa.movesCheckMate.put(gyd.ghost, aux[i].intersection.identificador);
				ghosts.remove(gyd.ghost);
				nodosFijos.add(game.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			}
			else {
				rellenarProxDestinosPacMan(visitadas, nodosFijos);
				visitadas.toArray(aux);
				i = 0;
			}		
		}
		return visitadas.size() - i == 0;
	}

	private class GHOSTANDDISTANCE {
		public GHOST ghost;
		public double distance = Double.MAX_VALUE;
	}

	private GHOSTANDDISTANCE closestGhostToIntersection(Game game, int inter, Vector<GHOST> libres) {
		GHOSTANDDISTANCE gyd = new GHOSTANDDISTANCE();
		for (GHOST ghost : libres) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), inter, game.getGhostLastMoveMade(ghost), DM.PATH);
			if (aux < gyd.distance) {
				gyd.ghost = ghost;
				gyd.distance = aux;
			}
		}
		return gyd;
	}

	private void rellenarProxDestinosPacMan(Set<interseccion_plus> inters, Vector<Integer> nodosFijos) {
		interseccion intActual = mapa.getInterseccionActual();
		if (inters.isEmpty()){
			if(mapa.getCheckLastModeMade()) 
				inters.add(new interseccion_plus(intActual, intActual));
			else 
				inters.add(new interseccion_plus(mapa.getInterseccion(intActual.destinos.get(mapa.getUltimoMovReal())), intActual));
		}
		else {
			Set<interseccion_plus> aux = new HashSet<interseccion_plus>(inters);
			inters.clear();
			// Buscamos las intersecciones correspondientes a los movimientos posibles
			// y si ya está esa intersección fijada no expandimos su rama
			for (interseccion_plus a : aux) {
				if (!nodosFijos.contains(a.intersection.identificador)) {
					for (MOVE mo : a.intersection.destinos.keySet()) {
						if(mapa.getInterseccion(a.intersection.destinos.get(mo)) != a.prohibida){
							inters.add(new interseccion_plus(mapa.getInterseccion(a.intersection.destinos.get(mo)), a.intersection));
						}
					}
				}
			}
		}
	}

	private void initCppads(){
		this.cppad_PacMan = getClosestPowerPillAndDistance(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		cppad_Ghosts = new EnumMap<>(GHOST.class);
		for(GHOST g : GHOST.values()){
			cppad_Ghosts.put(g, getClosestPowerPillAndDistance(game.getGhostCurrentNodeIndex(g), game.getGhostLastMoveMade(g)));
		}
	}

	public Vector<GHOST> getActiveGhosts(){
		Vector<GHOST> actives = new Vector<>();
		for(GHOST ghostType : GHOST.values()){
			if(!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType) <= 0) 
				actives.add(ghostType);
		}

		return actives;
	}
	public Vector<GHOST> getEdibleGhosts() {
		Vector<GHOST> edibleGhosts = new Vector<GHOST>();
		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost))
				edibleGhosts.add(ghost);
		}
		return edibleGhosts;
	}
	public NODEANDDISTANCE nearestGhostDistance(int myPos,int[] pos, MOVE m) {

		int nearestP=-1;
		double nearestDist = Double.MAX_VALUE;
		for (int p : pos) {
			double aux = game.getDistance(myPos, p, m, /* constant dm */DM.PATH);
			if (aux < nearestDist) {
				nearestDist = aux;
				nearestP=p;
			}
		}

		return new NODEANDDISTANCE(nearestP,nearestDist);
	}

	private ClosestPowerPillAndDistance getClosestPowerPillAndDistance(int pos, MOVE lastMoveMade){
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
			for (GHOST ghostType : getActiveGhosts()) {
				double dist = game.getDistance(game.getGhostCurrentNodeIndex(ghostType), cppad_PacMan.powerpill, game.getGhostLastMoveMade(ghostType), DM.PATH);
				distMin = Math.min(dist, distMin);
			}
			return distMin > cppad_PacMan.distance && cppad_PacMan.distance < limit * 1.55;
		}
		return false;
	}

	public int getNumberOfActivePPilsLeft(){
		return ppillsLeft;
	}

	public boolean isPacManCloserToPowerPill(){
		return isPacManCloserToAnyPowerPill;
	}

	public double getMinPacmanDistancePPill() {
		return cppad_PacMan.distance;
	}

	public interseccion getProximaInterseccionPacMan() { 
		return proximaInterseccionPacMan;
	}
	public interseccion getProximaInterseccionGhost(int pos) { 
		return mapa.getInterseccion(pos);
	}
	public MOVE getPacmanRealMoveMade() {return mapa.getUltimoMovReal();}
	public ClosestPowerPillAndDistance getClosestPowerPillAndDistance(GHOST ghostType){
		return cppad_Ghosts.get(ghostType);
	}

	public EnumMap<GHOST, ClosestPowerPillAndDistance> getClosestPowerPillAndDistances(){
		return cppad_Ghosts;
	}

	public ClosestPowerPillAndDistance getClosestPowerPillAndDistancePacMan(){
		return cppad_PacMan;
	}
}
