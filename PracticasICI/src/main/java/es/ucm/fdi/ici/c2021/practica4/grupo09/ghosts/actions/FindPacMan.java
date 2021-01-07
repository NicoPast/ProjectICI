package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts.actions;

import java.util.Random;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.fuzzy.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class FindPacMan implements Action {
    GHOST ghostType;
	MapaInfoGhost mapa;
	
	public FindPacMan(GHOST ghost, MapaInfoGhost map_) {
		this.ghostType = ghost;
		this.mapa = map_;
	}

	@Override
	public MOVE execute(Game game) {
		if (!game.doesGhostRequireAction(ghostType))  //if does not require an action	
			return MOVE.NEUTRAL;
		
		int myPos = game.getGhostCurrentNodeIndex(ghostType);
		MOVE mylastMove = game.getGhostLastMoveMade(ghostType);

        interseccion inter = mapa.getInterseccion(myPos);
        if(inter == null)
            return MOVE.NEUTRAL;
        
        //Distancia del camino mas corto
        double minDistance = Double.MAX_VALUE;
        for(MOVE m : inter.distancias.keySet()){
            if(m == mylastMove.opposite())
                continue;
            if(inter.distancias.get(m) < minDistance){
                minDistance = inter.distancias.get(m);
            }
        }

        //Elige un random entre los movimientos mas cortos
        Vector<MOVE> bestMoves = new Vector<>();
        double delta = 5;
        for(MOVE m : inter.distancias.keySet()){
            if(m == mylastMove.opposite())
                continue;
            if(inter.distancias.get(m) - delta <= minDistance){
                bestMoves.add(m);
            }
        }
        Random rnd = new Random();
        return bestMoves.get(rnd.nextInt(bestMoves.size()));
	}
}
