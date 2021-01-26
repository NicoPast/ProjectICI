package es.ucm.fdi.ici.c2021.practica5.grupo09;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.pacman.MsPacManDescription;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput implements Input {

	MapaInfo mapInfo;
		
	Boolean vulnerable = false;
	
	Double distGhost = Double.MAX_VALUE;
	Double distEdibleGhost = Double.MAX_VALUE;
	Double distToPowerPill = Double.MAX_VALUE;

	Integer score = -1;	
	Integer tipoInterseccion = 0;

	class GhostPair{
		GHOST ghost;
		Double dist;
	}
	
	double distAlerta = 93;
		
	public MsPacManInput(MapaInfo map){
		mapInfo = map;
	}
	
	@Override
	public void parseInput(Game game) { //se llama en cada interseccion	
		
		interseccion interseccionActual = mapInfo.getInterseccionActual();

		
		score = game.getScore();		
		vulnerable = isVulnerable(game);		

		distGhost =  distToGhost(game, false);
		distEdibleGhost =  distToGhost(game, true);
		distToPowerPill = distToPP(game);
		score = game.getScore();
		
		tipoInterseccion = getTipoInterseccion();
		//System.out.println(tipoInterseccion);
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();
				
		//Hacer todos los sets
		description.setVulnerable(vulnerable);
		description.setScore(score);
		description.setTipoInterseccion(tipoInterseccion);
		description.setDistClosestGhost(distGhost.intValue());
		description.setDistClosestEdibleGhost(distEdibleGhost.intValue());
		description.setDistToPowerPill(distToPowerPill.intValue());
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);	
		return query;
	}
	
	
	public void setMap(MapaInfo map) {
		mapInfo = map;
	}

	
	private GhostPair proxGhost(Game game, interseccion interseccionActual, MOVE m) {
		GhostPair pair = new GhostPair();
		pair.ghost = GHOST.BLINKY;
		pair.dist = Double.MAX_VALUE; 
				
		//recorrido en anchura oara buscar el fantasma
		for(GHOST g: GHOST.values()) {
			double dist = game.getDistance(interseccionActual.identificador, game.getGhostCurrentNodeIndex(g),
					DM.PATH);
			if(dist <= interseccionActual.distancias.get(m)) {
				pair.ghost = g;
				pair.dist = dist;
				return pair;				
			}
		}		
				
		//si llega a aqui es que el fantasma esta a mas de un nodo de distancia
		interseccion interseccionInicial = mapInfo.getInterseccion(interseccionActual.destinos.get(m));
		
		//calcular el movimiento de llegada
		MOVE moveLlegada = proxMovimientoLlegada(interseccionActual, m);
		for(GHOST g: GHOST.values()) {			
			double distAux = game.getDistance(interseccionInicial.identificador, game.getGhostCurrentNodeIndex(g),
					moveLlegada, DM.PATH) + interseccionActual.distancias.get(m) + 1; //+1 por que no se trata la interseccion
			if(distAux < pair.dist) {
				pair.dist = distAux;
				pair.ghost = g;
			}
		}	
		
		return pair;
	}
	
	private MOVE proxMovimientoLlegada(interseccion interseccionActual, MOVE proxMove) {
		interseccion interLlegada = mapInfo.getInterseccion(interseccionActual.destinos.get(proxMove));
		if(interLlegada != null)
			for (MOVE m : MOVE.values()) {
				if (interLlegada.distancias.get(m) != null
						&& interLlegada.destinos.get(m) == interseccionActual.identificador
						&& interLlegada.distancias.get(m) == interseccionActual.distancias.get(proxMove))
					return m;
			}
		return MOVE.NEUTRAL; // nunca deberia llegar
	}

	private Boolean isVulnerable(Game game) {
		
		double distMin = Double.MAX_VALUE;
		//buscar el fantasma no comible mas cercano
		for(GHOST g: GHOST.values()) {
			if(!game.isGhostEdible(g)) {
				double distAux = game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g),
						DM.PATH);
				if(distAux < distMin) {
					distMin = distAux;
				}
			}
		}		
		return distMin < distAlerta;
	}
	
	private int getTipoInterseccion() {
		//usamo el mapa para saber en que tipo de interseccion estamos
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		
		if(interseccionActual.destinos.get(MOVE.UP) == null) return 1;
		else if(interseccionActual.destinos.get(MOVE.RIGHT) == null) return 2;
		else if(interseccionActual.destinos.get(MOVE.DOWN) == null) return 3;
		else if(interseccionActual.destinos.get(MOVE.LEFT) == null) return 4;
		else return 0;
	}

	private Double distToGhost(Game game, Boolean edible) {
		Double dist = Double.MAX_VALUE;
		
		for(GHOST g:GHOST.values()) {
			if(game.isGhostEdible(g) == edible) {
				Double aux = game.getDistance(game.getPacmanCurrentNodeIndex(), 
						game.getGhostCurrentNodeIndex(g), DM.PATH);
				if(aux != -1 && aux < dist) dist = aux;
			}			
		}		
		return dist;		
	}
	
	
	private Double distToPP(Game game) {
		return game.getDistance(game.getPacmanCurrentNodeIndex(),mapInfo.getClosestPillAnchura(game) , DM.PATH);
	}

}


