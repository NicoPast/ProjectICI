package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.EnumMap;

import com.hp.hpl.jena.sparql.function.library.min;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.GhostsDescription;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.MapaInfoGhost.interseccion;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput implements Input {

	//Descripcion
	EnumMap<MOVE, Integer> nearestGhosts;
	EnumMap<MOVE, Boolean> areGhostsEdible;
	EnumMap<MOVE, Integer> distanceNextInterseccion;
	Boolean edible;
	MOVE lastMove;
	Double PacmanDistance;

	//Relacionadas con el resultado
	Integer score;
	Integer pacManLife;

	//Auxiliares
	MapaInfoGhost mapa;
	GHOST ghostType;
	interseccion myInterseccion;

	DM DISTANCE_MEASURE = DM.PATH;
	
	public GhostsInput(MapaInfoGhost map) {
		this.mapa=map;
	}

	public void setGhost(GHOST myGhost) {
		this.ghostType = myGhost;
	}

	@Override
	public void parseInput(Game game) {
		mapa.update(game);
		if(game.doesGhostRequireAction(ghostType)) {
			myInterseccion = mapa.getInterseccion(game.getGhostCurrentNodeIndex(ghostType));
			edible = game.isGhostEdible(ghostType);
			lastMove = game.getGhostLastMoveMade(ghostType);
			PacmanDistance = game.getDistance(this.myInterseccion.identificador, game.getPacmanCurrentNodeIndex(), this.lastMove, DISTANCE_MEASURE);
			score = game.getScore();
			pacManLife = game.getPacmanNumberOfLivesRemaining();

			for(MOVE m : MOVE.values())
				if(m != MOVE.NEUTRAL) computeNearestGhostAndEdible(game, m);

			//Compute distances to intersection
			for(MOVE m : MOVE.values()){
				if(m == MOVE.NEUTRAL) 
					continue;
				if(myInterseccion.distancias.containsKey(m))
					distanceNextInterseccion.put(m, myInterseccion.distancias.get(m));
				else 
					distanceNextInterseccion.put(m, Integer.MAX_VALUE);			
			}
		}		
	}

	@Override
	public CBRQuery getQuery() {		
		GhostsDescription description = new GhostsDescription();
		description.setDistanceNearestGhostUp(this.nearestGhosts.get(MOVE.UP));
		description.setDistanceNearestGhostDown(this.nearestGhosts.get(MOVE.DOWN));
		description.setDistanceNearestGhostLeft(this.nearestGhosts.get(MOVE.LEFT));
		description.setDistanceNearestGhostRight(this.nearestGhosts.get(MOVE.RIGHT));
		
		description.setDistanceNextIntersectionUp(this.distanceNextInterseccion.get(MOVE.UP));
		description.setDistanceNextIntersectionDown(this.distanceNextInterseccion.get(MOVE.DOWN));
		description.setDistanceNextIntersectionLeft(this.distanceNextInterseccion.get(MOVE.LEFT));
		description.setDistanceNextIntersectionRight(this.distanceNextInterseccion.get(MOVE.RIGHT));
		
		description.setGhostEdibleUp(this.areGhostsEdible.get(MOVE.UP));
		description.setGhostEdibleDown(this.areGhostsEdible.get(MOVE.DOWN));
		description.setGhostEdibleLeft(this.areGhostsEdible.get(MOVE.LEFT));
		description.setGhostEdibleRight(this.areGhostsEdible.get(MOVE.RIGHT));
		
		description.setEdible(this.edible);
		description.setLastMove(this.lastMove.ordinal());
		description.setDistanceToPacMan(this.PacmanDistance);
		description.setScore(this.score);
		description.setPacmanLife(this.pacManLife);
		
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}
	
	private void computeNearestGhostAndEdible(Game game, MOVE m) {
		if(!myInterseccion.destinos.containsKey(m)){
			nearestGhosts.put(m, Integer.MAX_VALUE);
			areGhostsEdible.put(m, false);
			return;
		}	

		interseccion siguiente = mapa.getInterseccion(myInterseccion.destinos.get(m));
		double minDistance = Double.MAX_VALUE, ghostDistance = -1, distanceToIntersection = myInterseccion.distancias.get(m);
		MOVE prohibido = MOVE.NEUTRAL, towards = MOVE.NEUTRAL;
		for(MOVE moveInter : siguiente.destinos.keySet()){
			if(siguiente.destinos.get(moveInter) == myInterseccion.identificador){
				prohibido = moveInter;
				break;
			}
		}

		double distanceSigToGhost = 0;
		for(GHOST g : GHOST.values()){
			if(g == ghostType)
				continue;	
			distanceSigToGhost = game.getDistance(siguiente.identificador, game.getGhostCurrentNodeIndex(g), DISTANCE_MEASURE);
			towards = game.getNextMoveTowardsTarget(siguiente.identificador, game.getGhostCurrentNodeIndex(g), DISTANCE_MEASURE);
			ghostDistance = towards == prohibido ? distanceToIntersection - distanceSigToGhost : distanceToIntersection + distanceSigToGhost;
			if(ghostDistance < minDistance) {
				minDistance = ghostDistance;
				areGhostsEdible.put(m, game.isGhostEdible(g));
			}
		}
		nearestGhosts.put(m, (int)minDistance);
	}
}
