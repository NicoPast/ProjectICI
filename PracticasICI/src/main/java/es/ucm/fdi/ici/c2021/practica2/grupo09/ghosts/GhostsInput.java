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

	private class ClosestPowerPillAndDistance{
		int powerpill = 0;
		double distance =  Double.MAX_VALUE;
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
	private interseccion proximaInterseccionPacMan;
	private boolean isPacManCloserToAnyPowerPill;

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

		int pacman = game.getPacmanCurrentNodeIndex();
		this.cppad_PacMan.distance = Double.MAX_VALUE;
		for(int ppill: game.getPowerPillIndices()) {
			double distance = game.getDistance(pacman, ppill, game.getPacmanLastMoveMade(), DM.PATH);
			this.cppad_PacMan.distance = Math.min(distance, this.cppad_PacMan.distance);
			if(this.cppad_PacMan.distance == distance) 
				this.cppad_PacMan.powerpill = ppill;
		}

		if(mapa.getCheckLastModeMade()) proximaInterseccionPacMan = mapa.getInterseccionActual();
		else proximaInterseccionPacMan = mapa.getInterseccion(mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()));

		isPacManCloserToAnyPowerPill = isPacManCloserToPowerPill(game, 99999);
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

	private ClosestPowerPillAndDistance getClosestPowerPillAndDistance(Game game, int pos, MOVE lastMoveMade){
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

	private boolean isPacManCloserToPowerPill(Game game, int limit) {
		ClosestPowerPillAndDistance cpad = getClosestPowerPillAndDistance(game, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());

		// Solo miro si esta en el rango del fairplay
		double distMin = Double.MAX_VALUE; 
		if (cpad.distance < limit) {
			for (GHOST ghostType : getActiveGhosts()) {
				double dist = game.getDistance(game.getGhostCurrentNodeIndex(ghostType), cpad.powerpill, game.getGhostLastMoveMade(ghostType), DM.PATH);
				distMin = Math.min(dist, distMin);
			}
			return distMin > cpad.distance && cpad.distance < limit * 1.55;
		}
		return false;
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
}
