package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.MsPacManDescription;
import javassist.compiler.ast.Pair;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput implements Input {

	MapaInfo mapInfo;
	
	int[] distancias = {-1,-1,-1,-1}; //UP, RIGHT, DOWN, LEFT
	int[] ghost = {-1,-1,-1,-1}; //UP, RIGHT, DOWN, LEFT
	
	//el estado del fantasma mas cercana en esa direccion 
	Boolean[] edible = {false, false, false, false}; //ejemplo: (edible[0] es el estado del fantasma que se llega por ghost[0])
	
	Boolean vulnerable = false;
	
	MOVE lastMove = MOVE.NEUTRAL;

	int[] pills = {-1,-1,-1,-1}; //UP, RIGHT, DOWN, LEFT
	int[] powerPills = {-1,-1,-1,-1}; //UP, RIGHT, DOWN, LEFT

	Integer score = -1;	

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
		
		//AQUI SE ACTUALIZAN LOS DATOS PARA GUARDARLOS
		interseccion interseccionActual = mapInfo.getInterseccionActual();

		for(MOVE m: MOVE.values()) {
			if(interseccionActual.distancias.get(m) != null) {
				distancias[m.ordinal()] = interseccionActual.distancias.get(m);
				GhostPair pair = proxGhost(game, interseccionActual, m);
				ghost[m.ordinal()] = pair.dist.intValue();
				edible[m.ordinal()] = game.isGhostEdible(pair.ghost);
				pills[m.ordinal()] = interseccionActual.pills.get(m);
				powerPills[m.ordinal()] = interseccionActual.powerPill.get(m);
			}
		}
		lastMove = game.getPacmanLastMoveMade();
		score = game.getScore();
		
		vulnerable = isVulnerable(game);		
		
		score = game.getScore();
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();
				
		//Hacer todos los sets
		description.setDistanciaUp(distancias[0]);
		description.setDistanciaRight(distancias[1]);
		description.setDistanciaDown(distancias[2]);
		description.setDistanciaLeft(distancias[3]);
		description.setGhostUp(ghost[0]);
		description.setGhostRight(ghost[1]);
		description.setGhostDown(ghost[2]);
		description.setGhostLeft(ghost[3]);
		description.setEdibleUp(edible[0]);
		description.setEdibleRight(edible[1]);
		description.setEdibleDown(edible[2]);
		description.setEdibleLeft(edible[3]);
		description.setVulnerable(vulnerable);
		description.setLastMove(lastMove.ordinal());
		description.setPillsUp(pills[0]);
		description.setPillsRight(pills[1]);
		description.setPillsDown(pills[2]);
		description.setPillsLeft(pills[3]);
		description.setPowerPillUp(powerPills[0]);
		description.setPowerPillRight(powerPills[1]);
		description.setPowerPillDown(powerPills[2]);
		description.setPowerPillLeft(powerPills[3]);
		description.setScore(score);
		
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
	
}
