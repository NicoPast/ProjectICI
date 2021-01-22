package es.ucm.fdi.ici.c2021.practica5.grupo09;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.MsPacManDescription;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput implements Input {

	Integer distanciaUp;
	Integer distanciaRight;
	Integer distanciaDown;
	Integer distanciaLeft;

	Integer ghostUp;
	Integer ghostRight;
	Integer ghostDown;
	Integer ghostLeft;

	Boolean edibleUp;
	Boolean edibleRight;
	Boolean edibleDown;
	Boolean edibleLeft;
	
	Boolean vulnerable;
	
	Integer direction;

	Integer pillsUp;
	Integer pillsRight;
	Integer pillsDown;
	Integer pillsLeft;

	Integer powerPillUp;
	Integer powerPillRight;
	Integer powerPillDown;
	Integer powerPillLeft;

	Integer score;
	
	@Override
	public void parseInput(Game game) {
		
		
		//AQUI SE ACTUALIZAN LOS DATOS PARA GUARDARLOS
		
		
		//computeNearestGhost(game);
		//computeNearestPPill(game);
		score = game.getScore();
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();
		
		
		//Hacer todos los sets
		description.setDistanciaUp(distanciaUp);
		description.setDistanciaRight(distanciaRight);
		description.setDistanciaDown(distanciaDown);
		description.setDistanciaLeft(distanciaLeft);
		description.setGhostUp(ghostUp);
		description.setGhostRight(ghostRight);
		description.setGhostDown(ghostDown);
		description.setGhostLeft(ghostLeft);
		description.setEdibleUp(edibleUp);
		description.setEdibleRight(edibleRight);
		description.setEdibleDown(edibleDown);
		description.setEdibleLeft(edibleLeft);
		description.setVulnerable(vulnerable);
		description.setDirection(direction);
		description.setPillsUp(pillsUp);
		description.setPillsRight(pillsRight);
		description.setPillsDown(pillsDown);
		description.setPillsLeft(pillsLeft);
		description.setPowerPillUp(powerPillUp);
		description.setPowerPillRight(powerPillRight);
		description.setPowerPillDown(powerPillDown);
		description.setPowerPillLeft(powerPillLeft);
		description.setScore(score);
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}
	

}
