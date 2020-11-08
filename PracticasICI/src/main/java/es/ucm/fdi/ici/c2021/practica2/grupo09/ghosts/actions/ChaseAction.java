package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseAction implements Action {

	DM CONSTANT_MEASURE_DISTANCE = DM.PATH;
	DM CONSTANT_MEASURE_DIRECTION = DM.EUCLID;

	private MapaInfo mapa;
	GHOST ghostType;
	
	public ChaseAction( GHOST ghost, MapaInfo mapa_ ) {
		this.ghostType = ghost;
		this.mapa= mapa_;
	}

	// Se dirige a la interseccion donde es mas probable que se dirija el PacMan y que yo este más cerca
	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghostType)) { //if it requires an action	
			int myPos = game.getGhostCurrentNodeIndex(ghostType);
			MOVE mylastMove = game.getGhostLastMoveMade(ghostType);
			int destino = 0;
			float valorMasAlto = -10, valor = 0;

			interseccion proximaInterseccionPacman, interseccionActual = mapa.getInterseccionActual();
				if(mapa.getCheckLastModeMade()) proximaInterseccionPacman = interseccionActual;
				else proximaInterseccionPacman = mapa.getInterseccion(interseccionActual.destinos.get(mapa.getUltimoMovReal()));

			for (MOVE m : proximaInterseccionPacman.destinos.keySet()) {
				if(proximaInterseccionPacman.destinos.get(m) == interseccionActual.identificador)
					continue;
				if(myPos == proximaInterseccionPacman.destinos.get(m)){
					destino = proximaInterseccionPacman.identificador;
					break;
				}
				//Valor = pills del camino / (distancia a la siguiente interseccion + distancia ghost a la interseccion)
				double distanceG = game.getDistance(myPos, proximaInterseccionPacman.identificador, mylastMove, CONSTANT_MEASURE_DISTANCE);
				valor = (float)proximaInterseccionPacman.pills.get(m) / 
						(float)(proximaInterseccionPacman.distancias.get(m) + distanceG);

				for (GHOST g : mapa.destinosGhosts.keySet()) { // Si hay un fantasma que se dirige hacia ahí y llega antes, no voy ahí
				 	if (mapa.destinosGhosts.get(g) != null && g != ghostType && mapa.destinosGhosts.get(g).identificador != destino
				 		&& distanceG < game.getDistance(myPos, proximaInterseccionPacman.destinos.get(m), mylastMove, CONSTANT_MEASURE_DISTANCE)) {
				 		valor--;
				 		break;
				 	}
				}
				if (valor > valorMasAlto) { // si el valor es mas alto o si ya hay un fantasma que llega antes ahí
					valorMasAlto = valor;
					destino = proximaInterseccionPacman.destinos.get(m);
				}
			}

			mapa.destinosGhosts.put(ghostType, mapa.getInterseccion(destino));

			return game.getNextMoveTowardsTarget(myPos, destino, mylastMove, CONSTANT_MEASURE_DIRECTION);
        }
		else 
			return MOVE.NEUTRAL;
	}
}