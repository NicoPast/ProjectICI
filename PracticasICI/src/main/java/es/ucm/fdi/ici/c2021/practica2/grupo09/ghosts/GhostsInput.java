package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Input;
import pacman.game.Constants.DM;
import pacman.game.Game;

public class GhostsInput extends Input {

	private MapaInfo mapa;

	private double minPacmanDistancePPill;
	private interseccion proximaInterseccionPacMan;
	
	public GhostsInput(Game game, MapaInfo mapaInfo) {
		super(game);
		mapa = mapaInfo;
	}

	@Override
	public void parseInput() {
		mapa.update(game);

		int pacman = game.getPacmanCurrentNodeIndex();
		this.minPacmanDistancePPill = Double.MAX_VALUE;
		for(int ppill: game.getPowerPillIndices()) {
			double distance = game.getDistance(pacman, ppill, DM.PATH);
			this.minPacmanDistancePPill = Math.min(distance, this.minPacmanDistancePPill);
		}

		if(mapa.getCheckLastModeMade()) proximaInterseccionPacMan = mapa.getInterseccionActual();
		else proximaInterseccionPacMan = mapa.getInterseccion(mapa.getInterseccionActual().destinos.get(mapa.getUltimoMovReal()));
	}

	public double getMinPacmanDistancePPill() {
		return minPacmanDistancePPill;
	}

	public interseccion getProximaInterseccionPacMan() { 
		return proximaInterseccionPacMan;
	}

	public MapaInfo getMapaInfo() {
		return mapa;
	}
}
