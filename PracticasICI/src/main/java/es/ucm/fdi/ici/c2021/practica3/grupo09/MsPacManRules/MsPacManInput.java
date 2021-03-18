package es.ucm.fdi.ici.c2021.practica3.grupo09.MsPacManRules;

import java.util.Collection;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfo;
import es.ucm.fdi.ici.rules.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput extends Input {
	
	MapaInfo mapInfo;
	private double distanciaPeligro = 90;

	public MsPacManInput(Game game, MapaInfo map) {
		super(game);
		this.mapInfo = map;
		mapInfo.update(game);
	}

	@Override
	public void parseInput() {
		
	}
	
	private int getNumEadableGhost() {
		int numEadableGhost = 0;
		for(GHOST g : GHOST.values()) {
			if(game.isGhostEdible(g)) {
				numEadableGhost++;
			}
		}
		//System.out.println(numEadableGhost);
		return numEadableGhost;
	}
	
	private double distToNearestGhost() {
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
	
	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();
		//System.out.println(getNumEadableGhost());
		//System.out.println(game.getNumberOfActivePowerPills());
		//System.out.println(distToNearestGhost() < distanciaPeligro);
		facts.add(String.format("(MSPACMAN (numGhostAlive %d)(numPP %d)(danger %s))", getNumEadableGhost(), 
				game.getNumberOfActivePowerPills(), distToNearestGhost() < distanciaPeligro));
		return facts;
	}
}
