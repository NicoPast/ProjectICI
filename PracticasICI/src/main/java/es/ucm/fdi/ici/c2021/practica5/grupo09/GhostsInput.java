package es.ucm.fdi.ici.c2021.practica5.grupo09;

import java.util.EnumMap;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.GhostsDescription;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts.MapaInfoGhost.interseccion;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput implements Input {

	MapaInfoGhost mapa;
	GHOST ghost;
	interseccion interseccion;
	EnumMap<MOVE,Integer> nearestGhosts;
	EnumMap<MOVE,Boolean> areGhostsedible;
	Boolean edible;
	MOVE lastMove;
	Double PacmanDistance;
	
	public GhostsInput(MapaInfoGhost map) {
		this.mapa=map;

	}
	public void setGhost(GHOST myGhost) {
		this.ghost=myGhost;
	}
	@Override
	public void parseInput(Game game) {
		mapa.update(game);
		if(game.doesGhostRequireAction(ghost)) {
			interseccion=mapa.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
			for(MOVE m:interseccion.destinos.keySet())
				computeNearestGhostAndEdible(game, m);
			edible=game.isGhostEdible(ghost);
			lastMove=game.getGhostLastMoveMade(ghost);
			PacmanDistance=game.getDistance(this.interseccion.identificador, 
					game.getPacmanCurrentNodeIndex(), this.lastMove,DM.PATH);
		}
		
		
	}

	@Override
	public CBRQuery getQuery() {
		
		GhostsDescription description = new GhostsDescription();
		description.setDistanceNearestGhostUp(this.nearestGhosts.get(MOVE.UP));
		description.setDistanceNearestGhostDown(this.nearestGhosts.get(MOVE.DOWN));
		description.setDistanceNearestGhostLeft(this.nearestGhosts.get(MOVE.LEFT));
		description.setDistanceNearestGhostRight(this.nearestGhosts.get(MOVE.RIGHT));
		
		description.setDistanceNextIntersectionUp(this.interseccion.distancias.get(MOVE.UP));
		description.setDistanceNextIntersectionDown(this.interseccion.distancias.get(MOVE.DOWN));
		description.setDistanceNextIntersectionLeft(this.interseccion.distancias.get(MOVE.LEFT));
		description.setDistanceNextIntersectionRight(this.interseccion.distancias.get(MOVE.RIGHT));
		
		description.setGhostEdibleUp(this.areGhostsedible.get(MOVE.UP));
		description.setGhostEdibleDown(this.areGhostsedible.get(MOVE.DOWN));
		description.setGhostEdibleLeft(this.areGhostsedible.get(MOVE.LEFT));
		description.setGhostEdibleRight(this.areGhostsedible.get(MOVE.RIGHT));
		
		description.setEdible(edible);
		description.setLastMove(this.lastMove.ordinal());
		description.setDistanceToPacMan(this.PacmanDistance);
		
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}
	
	private void computeNearestGhostAndEdible(Game game,MOVE m) {
		this.nearestGhosts.clear();
		int []pos=new int[3];
		for(int i=0;i<3;i++) {
			if(GHOST.values()[i]==ghost)
				continue;
			pos[i]=game.getGhostCurrentNodeIndex(GHOST.values()[i]);
		}
		
		GHOST nearestP=GHOST.BLINKY;
		double nearestDist = Double.MAX_VALUE;
		for (int j=0;j<3;j++) {
			
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), pos[j], m, DM.PATH);
			if (aux < nearestDist) {
				nearestDist = aux;
				nearestP=GHOST.values()[j];
			}
		}
		this.nearestGhosts.put(m, (int)nearestDist);
		this.areGhostsedible.put(m,game.isGhostEdible(nearestP));
	}
	
	
	
}
