package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.fsm.Input;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class MsPacManInput extends Input {

	private MapaInfo mapInfo;
	
	public MsPacManInput(Game game) {
		super(game);
		
		
		
	}

	@Override
	public void parseInput() {
		
		mapInfo.update(game);	
	}

	
	public double distToNearestGhost() {
		double distanciaAux = Double.MAX_VALUE; //para asegurarnos que si no hay fantasmas cerca, devuelva false
		for (GHOST g : GHOST.values()) {
			double distancia = game.getDistance(mapInfo.getInterseccionActual().identificador,
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
}
