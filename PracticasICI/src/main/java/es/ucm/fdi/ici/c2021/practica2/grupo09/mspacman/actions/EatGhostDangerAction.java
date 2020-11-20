package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.actions;

import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class EatGhostDangerAction implements Action {

	MapaInfo mapInfo;
	interseccion interseccionActual;
	float distanciaMaximaPerseguir = 80;

	public EatGhostDangerAction(MapaInfo map) {
		mapInfo = map;
	}

	@Override
	public MOVE execute(Game game) {

		//filtra que estemos en una interseccion
		if(mapInfo.getInterseccion(game.getPacmanCurrentNodeIndex()) == null) return MOVE.NEUTRAL;		
		interseccionActual = mapInfo.getInterseccionActual();	
		
		
		//necesitamos el fantasma comible mas cercano
		GHOST proxGhost = nearestEadableGhpost(game);
		
		MOVE moveToGhost = game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(proxGhost), game.getPacmanLastMoveMade(), DM.PATH);

		if(ghostInWay(moveToGhost, game) || game.getDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(proxGhost),
				mapInfo.getUltimoMovReal(), DM.PATH) > distanciaMaximaPerseguir) {
			//usar get best move
			return getBestMove(game);
		}
		else {
			return moveToGhost;
		}		
	}
	
	private GHOST nearestEadableGhpost(Game game) {
		GHOST ghost = null;
		double distancia = Double.MAX_VALUE;
		
		for(GHOST g:GHOST.values()) {
			if(game.isGhostEdible(g)) {
				double distAux = game.getDistance(game.getGhostCurrentNodeIndex(g), 
						interseccionActual.identificador, DM.PATH);
				
				//System.out.println(g + " " + distAux);
				
				if(distAux < distancia) {
					distancia = distAux;
					ghost = g;
				}
			}
		}

		//System.out.println("Fantasma objetivo: " + ghost);
		return ghost;
	}

	// mira su hay un fantasma en el camino
	private boolean ghostInWay(MOVE m, Game game) {

		boolean hasGhost = false;
		// mira si m no es de donde vienes, si no es neutral y si existe camino

		// mira para todos los fantasmas, si avanzando por ese camino me pillan
		for (GHOST g : GHOST.values()) {
			if(!game.isGhostEdible(g)) {
				double distancia = game.getDistance(interseccionActual.destinos.get(m), game.getGhostCurrentNodeIndex(g),
					DM.PATH);
				if (distancia != -1 && distancia <= interseccionActual.distancias.get(m)) { // no pillar el camino
					hasGhost = true;
					break; // hacemos el breake por que ya no nos interesa seguir buscando
				}
			}	
		}

		return hasGhost;
	}

	

	private MOVE getBestMove(Game game) {		

	
		Vector<MOVE> fantasmas = new Vector<MOVE>();
		Vector<MOVE> powerPills = new Vector<MOVE>();
		Vector<MOVE> noPills = new Vector<MOVE>();
		Vector<MOVE> pills = new Vector<MOVE>();
		Vector<GHOST> fantasmasComibles = new Vector<GHOST>();
		
		interseccion interseccionActual = mapInfo.getInterseccionActual();
		
		for (MOVE m : MOVE.values()) { //no puedes volver para atras
			//mira si m no es de donde vienes, si no es neutral y si existe camino
			
			if (m != mapInfo.getUltimoMovimientoDeLlegada() && m != MOVE.NEUTRAL && interseccionActual.distancias.get(m) != null) {
				
				boolean hasGhost = false;
				GHOST eadableGhost = null;
				//mira para todos los fantasmas, si avanzando por ese camino me pillan
				for (GHOST g : GHOST.values()) {
					double distancia = game.getDistance(interseccionActual.destinos.get(m), game.getGhostCurrentNodeIndex(g),
							DM.PATH);
					if (distancia != -1 && distancia <= interseccionActual.distancias.get(m)) { // no pillar el camino						
						
						if(!game.isGhostEdible(g)){
							hasGhost = true;
							fantasmas.add(m); //por aqui hay fantasma, meterlo a la lista de caminos con fantasmas						
							break; //hacemos el breake por que ya no nos interesa seguir buscando
						}
						else {							
							eadableGhost = g;						
						}

					}
				}
								
				
				if(!hasGhost) {
						//mira si hay powerPills (en este momento no estamos en peligro por tanto no nos interesa comerlas)
					if(eadableGhost != null)
						fantasmasComibles.add(eadableGhost);
					else if(interseccionActual.powerPill.get(m) > 0)
						powerPills.add(m);
					else if(interseccionActual.pills.get(m)==0)//mira si el camino no tiene pills
						noPills.add(m);
					else if(interseccionActual.pills.get(m)>0) pills.add(m); //en el camino solo hay pills
					
				}
			}
		}	
		
		//Tenemos todas las direcciones almacenadas en los vectores
		int aux = 0;
		MOVE actual = MOVE.NEUTRAL;
		
		if(fantasmasComibles.size()>0) {
			GHOST ghostAux = null;
			double auxDistGhost = Double.MAX_VALUE;
			
			for(GHOST g : fantasmasComibles) {
				double dis = game.getDistance(interseccionActual.identificador, game.getGhostCurrentNodeIndex(g),
						mapInfo.getMetrica());
				if(dis < auxDistGhost) {
					ghostAux = g;
					auxDistGhost = dis;
				}
			}
			actual = game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(ghostAux), game.getPacmanLastMoveMade(), mapInfo.getMetrica());
		}
		else if(pills.size()>0) {
			for(int i=0;i<pills.size();i++) { //si hay pills
					//System.out.println(interseccionActual.pills.get(pills.get(i)));
				if(interseccionActual.pills.get(pills.get(i)) >= aux) {
					aux = interseccionActual.pills.get(pills.get(i));
					actual = pills.get(i);
				}
			}		
		}
		else if(noPills.size()>0) {			
			//MOVE auxMove = game.getNextMoveTowardsTarget(interseccionActual.identificador, getClosestPill(game),
			//game.getPacmanLastMoveMade(), metrica);	
			MOVE auxMove = game.getNextMoveTowardsTarget(interseccionActual.identificador, getClosestPill(game), 
					mapInfo.getMetrica());

			boolean encontrado = false;
			int i=0;
			while(!encontrado && i<noPills.size()) {
				
				if(noPills.get(i) == auxMove) {
					encontrado = true;
					actual = auxMove;
				}				
				i++;
			}
			
			if(!encontrado) { //buscamos el mas corto
				double distanciaMinima = Double.MAX_VALUE;
				for(MOVE m:noPills) {
					double distAux = game.getDistance(interseccionActual.identificador, interseccionActual.destinos.get(m),
							mapInfo.getMetrica());
					if(distAux < distanciaMinima ) {
						distanciaMinima = distAux;
						actual = m;
					}
				}
			}
		}
		else if(powerPills.size()>0) { //coge el camino con menos powerPills
			aux = 0; //ahora pasa a ser powerPills
			for(int i=0;i<powerPills.size();i++) { //si hay pills
				if(interseccionActual.powerPill.get(powerPills.get(i))>aux) {
					aux = interseccionActual.powerPill.get(powerPills.get(i));
					actual = powerPills.elementAt(i);
				}
			}
		}
		else if(fantasmas.size()>0) { //se sabe que morimos. Prioridades: 1- powerPills, 2- Pills, 3- Distancia Corta
			aux = -1; //ahora pasa a ser distancias
			int maxPills = 0;
			for(int i=0;i<fantasmas.size();i++) { //si hay pills
				if(interseccionActual.powerPill.get(fantasmas.get(i)) > 0) {
					actual = fantasmas.elementAt(i);
					break;
				}
				else if(interseccionActual.pills.get(fantasmas.get(i)) > maxPills) {
					maxPills = interseccionActual.pills.get(fantasmas.get(i));
					actual = fantasmas.elementAt(i);
				}
				else if(maxPills == 0 &&(aux == -1 || interseccionActual.distancias.get(fantasmas.get(i)) > aux)) {
					aux = interseccionActual.distancias.get(fantasmas.get(i));
					actual = fantasmas.elementAt(i);
				}
			}
		}		
		
		if(actual == MOVE.NEUTRAL) {
			System.out.println("Se mamó");
		}
		return actual;
	}
	
	
    private int getClosestPill(Game game) {
        int closestPill = -1;
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (int currentPill : game.getActivePillsIndices()) {
            double aux = game.getDistance(pacmanPos, currentPill, mapInfo.getMetrica());
            if (aux < closestDistance) {
                closestPill = currentPill;
                closestDistance = aux;
            }
        }
        return closestPill;
    }
	
}
