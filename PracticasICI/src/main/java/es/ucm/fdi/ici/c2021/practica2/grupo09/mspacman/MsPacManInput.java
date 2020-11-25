package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.fsm.Input;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class MsPacManInput extends Input {

	private MapaInfo mapInfo;
	
	public MsPacManInput(Game game, MapaInfo map){
		super(game);
		this.mapInfo = map;
		parseInput();
	}

	@Override
	public void parseInput() {		
		if(mapInfo != null) mapInfo.update(game);	
	}

	
	public double distanceToNearestPowerPill() {
		double dist = Double.MAX_VALUE;
		
		for(int indice:game.getActivePowerPillsIndices()) {
			double aux= game.getDistance(game.getPacManInitialNodeIndex(), indice, mapInfo.getMetrica());
			if(aux<dist) dist = aux;
		}
		
		return dist;
	}
	
	public boolean wasPowerPillEaten() {
		return game.wasPowerPillEaten();
	}
	
	public double distToNearestGhost() {
		double distanciaAux = Double.MAX_VALUE; //para asegurarnos que si no hay fantasmas cerca, devuelva false
		if(mapInfo.getInterseccionActual() == null) return distanciaAux;
		
		for (GHOST g : GHOST.values()) {
			double distancia = game.getDistance(
					mapInfo.getInterseccionActual().identificador,
					game.getGhostCurrentNodeIndex(g), DM.PATH);
			//System.out.println(distancia);
			//si es -1 es que está en la caseta de inicio
			if (distancia != -1 && (distanciaAux == 0 || distancia < distanciaAux)) { // si tienes un fantasma cerca que te puedes comer
				distanciaAux = distancia;
			}
		}

		//return false;
		//System.out.println(distanciaAux);
		return distanciaAux;
	}
	
	public double distToNearestGhostNonEadable() {
		double distanciaAux = Double.MAX_VALUE; //para asegurarnos que si no hay fantasmas cerca, devuelva false
		if(mapInfo.getInterseccionActual() == null) return distanciaAux;
		
		for (GHOST g : GHOST.values()) {
			double distancia = game.getDistance(
					mapInfo.getInterseccionActual().identificador,
					game.getGhostCurrentNodeIndex(g), DM.PATH);
			//System.out.println(distancia);
			//si es -1 es que está en la caseta de inicio
			if (distancia != -1 && !game.isGhostEdible(g) && (distanciaAux == 0 || distancia < distanciaAux)) { // si tienes un fantasma cerca que te puedes comer
				distanciaAux = distancia;
			}
		}

		//return false;
		//System.out.println(distanciaAux);
		return distanciaAux;
	}

	public int numGhostEadable() {
		int num = 0;
		for(GHOST g: GHOST.values()) {
			if(game.isGhostEdible(g))num++;
		}
		return num;
	}
}
