package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts;

import java.util.EnumMap;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
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
	public boolean isCheckMate, checkMateCalculated;
	
	public GhostsInput(Game game, MapaInfo mapaInfo) {
		super(game);
		mapa = mapaInfo;
	}

	@Override
	public void parseInput() {
		mapa.update(game);

		isCheckMate = false;
		checkMateCalculated = false;

		initCppads();

		if(mapa.getCheckLastModeMade()) proximaInterseccionPacMan = mapa.getInterseccionActual();
		else proximaInterseccionPacMan = mapa.getInterseccion(mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()));

		isPacManCloserToAnyPowerPill = isPacManCloserToPowerPill(99999);

		this.ppillsLeft = game.getNumberOfActivePowerPills();
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
	public NODEANDDISTANCE nearestGhostDistance(int myPos,int[] pos, MOVE m) {

		int nearestP=-1;
		double nearestDist = Double.MAX_VALUE;
		for (int p : pos) {
			double aux = game.getDistance(myPos, p, m, /* constant dm */DM.EUCLID);
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
