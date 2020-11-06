package es.ucm.fdi.ici.c2021.practica2.grupo09;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Vector;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

class interseccion {
	public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest, EnumMap<MOVE, Integer> pi,
			EnumMap<MOVE, Integer> ppi) {
		identificador = iden;
		distancias = dir;
		destinos = dest;
		pills = pi;
		powerPill = ppi;
	}

	public int identificador; // node index
	public EnumMap<MOVE, Integer> distancias; // distancias
	public EnumMap<MOVE, Integer> destinos; // identificador del nodo en esa direccion
	public EnumMap<MOVE, Integer> pills; // pills en ese camino (entre nodo y nodo, las pills que hay en las
											// intersecciones no cuentan)
	public EnumMap<MOVE, Integer> powerPill; // powerPills en ese camino
}



public final class MsPacMan extends PacmanController {
	private List<interseccion> mapa = new ArrayList<interseccion>();
	boolean mapaHecho = false;
	int ultimoNodo = -1, proximoNodo = -1; // -1 es que aun no ha registrado nada
	MOVE ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
	MOVE movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
	interseccion interseccionActual;
	String mapaActual = "a";

	DM metrica = DM.MANHATTAN;
	double distanciaPeligro = 40;
	double distanciaPerseguir = 140;


	// provisional

	private int[] buscaCamino(Node nodoActual, MOVE dir, Node[] graph) {
		MOVE direccion = dir;
		int pills = 0;
		int powerPills = 0;

		Node proximoNodo = graph[nodoActual.neighbourhood.get(direccion)];
		int coste = 1;

		while ((proximoNodo.numNeighbouringNodes <= 2)) {
			if (proximoNodo.neighbourhood.get(direccion) == null) { // en que otra direccion nos podemos mover
				for (MOVE m : MOVE.values()) { // ya tenemos la nueva direccion
					if (m != direccion.opposite() && proximoNodo.neighbourhood.get(m) != null) {
						direccion = m;
						break;
					}
				}
			}
			if (proximoNodo.pillIndex != -1)
				pills++;
			else if (proximoNodo.powerPillIndex != -1)
				powerPills++;
			proximoNodo = graph[proximoNodo.neighbourhood.get(direccion)];
			coste++;
		}
		return new int[] { coste, proximoNodo.nodeIndex, pills, powerPills };
	}

	private void crearMapa(Game game) {
		Node[] graph = game.getCurrentMaze().graph;
		
		for (Node nodo : graph) { // recorre todos los nodos del mapa
			if (nodo.numNeighbouringNodes > 2) { // quita muro y pasillos

				// miramos todas las direcciones posibles
				EnumMap<MOVE, Integer> map = nodo.neighbourhood;

				EnumMap<MOVE, Integer> direcciones = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE, Integer> destinations = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE, Integer> pills = new EnumMap<MOVE, Integer>(MOVE.class);
				EnumMap<MOVE, Integer> powerPills = new EnumMap<MOVE, Integer>(MOVE.class);

				for (MOVE m : MOVE.values()) {
					if (map.get(m) != null) { // direccion existente
						int[] temp = buscaCamino(nodo, m, graph);
						direcciones.put(m, temp[0]);
						destinations.put(m, temp[1]);
						pills.put(m, temp[2]);
						powerPills.put(m, temp[3]);
					}
				}
				mapa.add(new interseccion(nodo.nodeIndex, direcciones, destinations, pills, powerPills));
			}
		}
	}

	private interseccion interseccion_rec(int ini, int fin, int iden) {
		if (fin - ini > 1) { // sigue habiendo varios nodos en el rango de búsqueda
			int mid = (fin - ini) / 2 + ini;
			if (mapa.get(mid).identificador <= iden)
				return interseccion_rec(mid, fin, iden); // esta en el lado derecho
			else
				return interseccion_rec(ini, mid, iden); // esta en el lado izquierdo
		} else if (mapa.get(ini).identificador == iden)
			return mapa.get(ini); // es de tamaño 1 por tanto devuelve el elemento inicial
		else
			return null;
	}

	private interseccion getInterseccion(int iden) { // usa divide y venceras para encontrar la interseccion
		return interseccion_rec(0, mapa.size(), iden);
	}

	private void updateMapa(Game game) {
		if (game.wasPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			int pills = interSalida.pills.get(ultimoMovimientoReal);
			interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1);
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1);
		} else if (game.wasPowerPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			int pills = interSalida.powerPill.get(ultimoMovimientoReal); // no haría falta esta variable ya que pasaria
																		// de 1 a 0,
			interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1); // pero si alguein nos quiere romper el
																				// programa poniendo mas de
			interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1); // una powerpill entre dos intersecciones
																				// le podemos callar la boca
		}
	}

	private boolean isInRisk(Game game) {
		double distanciaAux = distanciaPeligro; //para asegurarnos que si no hay fantasmas cerca, devuelva false
		for (GHOST g : GHOST.values()) {
			double distancia = game.getDistance(interseccionActual.identificador, game.getGhostCurrentNodeIndex(g),
					DM.PATH);
			//System.out.println(distancia);
			//si es -1 es que está en la caseta de inicio
			if (distancia != -1 && !game.isGhostEdible(g) && (distanciaAux == 0 || distancia < distanciaAux)) { // si tienes un fantasma cerca que te puedes comer
				distanciaAux = distancia;
			}
		}

		//return false;
		//System.out.println(distanciaAux);
		return distanciaAux < distanciaPeligro;
	}

	private MOVE getBestMove(Game game) {		
	
		Vector<MOVE> fantasmas = new Vector<MOVE>();
		Vector<MOVE> powerPills = new Vector<MOVE>();
		Vector<MOVE> noPills = new Vector<MOVE>();
		Vector<MOVE> pills = new Vector<MOVE>();
		
		for (MOVE m : MOVE.values()) { //no puedes volver para atras
			//mira si m no es de donde vienes, si no es neutral y si existe camino
			if (m != movimientoDeLlegada && m != MOVE.NEUTRAL && interseccionActual.distancias.get(m) != null) {
				
				boolean hasGhost = false;
				//mira para todos los fantasmas, si avanzando por ese camino me pillan
				for (GHOST g : GHOST.values()) {
					double distancia = game.getDistance(interseccionActual.destinos.get(m), game.getGhostCurrentNodeIndex(g),
							DM.PATH);
					if (distancia != -1 && distancia < interseccionActual.distancias.get(m)) { // no pillar el camino						
						hasGhost = true;
						fantasmas.add(m); //por aqui hay fantasma, meterlo a la lista de caminos con fantasmas						
						break; //hacemos el breake por que ya no nos interesa seguir buscando
					}
				}
				
				if(!hasGhost) {
						//mira si hay powerPills (en este momento no estamos en peligro por tanto no nos interesa comerlas)
					if(interseccionActual.powerPill.get(m) > 0)
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
		
		if(pills.size()>0) {
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
			MOVE auxMove = game.getNextMoveTowardsTarget(interseccionActual.identificador, getClosestPill(game), metrica);

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
							metrica);
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
            double aux = game.getDistance(pacmanPos, currentPill, metrica);
            if (aux < closestDistance) {
                closestPill = currentPill;
                closestDistance = aux;
            }
        }
        return closestPill;
    }
    
    private boolean fantasmaEnCamino(Game game, GHOST g) {
    	if(game.isGhostEdible(g)) return false;
    	
    	MOVE posibleMovimiento = MOVE.NEUTRAL;
    	
    	posibleMovimiento = game.getApproximateNextMoveTowardsTarget(interseccionActual.identificador,
    			game.getGhostCurrentNodeIndex(g), game.getPacmanLastMoveMade(), metrica);
    	
    	double distancia = interseccionActual.distancias.get(posibleMovimiento);
    	
    	for(GHOST fantasma:GHOST.values()) {
    		if(fantasma != g && game.getDistance(game.getGhostCurrentNodeIndex(fantasma),
    				interseccionActual.destinos.get(posibleMovimiento), metrica) < distancia) { //nos come un fantasma
    			return true;
    		}
    	}
    	
    	return false;
    }
    
	GHOST fantasmaComibleCerca(Game game) {
		GHOST fantasma =  null;
		double distancia = Double.MAX_VALUE;
		for (GHOST g : GHOST.values()) {
						
			if (game.isGhostEdible(g)) { //la distancia pacMan-ghost(comible) > ghost(comible)-ghost(!combible)
				double distAux = game.getDistance(interseccionActual.identificador,
						game.getGhostCurrentNodeIndex(g), metrica);
				
				
				if (distAux < distancia && distAux < distanciaPerseguir && !fantasmaEnCamino(game,g)) { //fantasma comible en rango
					fantasma = g;
					distancia = distAux;
				}
			}
		}

		if (distancia < distanciaPerseguir) { // hay fantasmas para comer y está cerca
			return fantasma;
		} 
		else return null;
	}
	
	
	private int getPowerPillCercana(Game game) {
        int closestPowerPill = -1;
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        double closestDistance = Double.MAX_VALUE;
        for (int currentPill : game.getActivePowerPillsIndices()) {
            double aux = game.getDistance(pacmanPos, currentPill, metrica);
            if (aux < closestDistance) {
                closestPowerPill = currentPill;
                closestDistance = aux;
            }
        }
        return closestPowerPill;
	}
	
	
	private MOVE getMoveHuir(Game game) {		
		int powerPillCercana = getPowerPillCercana(game);
		
		if(powerPillCercana == -1) return getBestMove(game);
		
		//existe una power pill por el mapa
		double distanciaPP = game.getDistance(interseccionActual.identificador, powerPillCercana, metrica);
		MOVE direccion = MOVE.NEUTRAL;
		for(MOVE m:MOVE.values()) {
			if (m != movimientoDeLlegada && m != MOVE.NEUTRAL && interseccionActual.distancias.get(m) != null) {
				//entre todos estos caminos, por cual te quedas mas cerca de la pill
				
				boolean fantasmaDetectado = false;
				for(GHOST g:GHOST.values()){ //recorremos todos los fantasmas
					double distanciaFantasma = game.getDistance(interseccionActual.destinos.get(m), 
							game.getGhostCurrentNodeIndex(g), DM.PATH);
					
					if(distanciaFantasma <= interseccionActual.distancias.get(m)) {//por este camino me pillan						
						fantasmaDetectado = true;
						break;
					}
				}								
				
				if(!fantasmaDetectado) {
					double distanciaProximoNodoPP = game.getDistance(interseccionActual.destinos.get(m),
							powerPillCercana, metrica);
					if(distanciaProximoNodoPP < distanciaPP) {
						direccion = m;
					}	
				
				}	
			}
		}
		
		//direccion sigue siento nuetral, estamos en la interseccion mas cercana a la pill
		//System.out.println("estoy en riesgo");
		if(direccion == MOVE.NEUTRAL) { //por todo hay fantasmas o por ningun lado nos hacercamos (estamos en la interseccion mas cercana)
			//System.out.println("estoy muy cerca");
			MOVE mAux = game.getApproximateNextMoveTowardsTarget(interseccionActual.identificador,
						powerPillCercana, game.getPacmanLastMoveMade(), metrica); //no deberia entrar aqui pero para asegurar
			
			boolean fantasma = false;
			for(GHOST g:GHOST.values()){ //recorremos todos los fantasmas
				double distanciaFantasma = game.getDistance(powerPillCercana, 
						game.getGhostCurrentNodeIndex(g), DM.PATH);
				
				if(distanciaFantasma <= game.getDistance(interseccionActual.identificador,
						powerPillCercana, game.getPacmanLastMoveMade(),DM.PATH)) {//por este camino me pillan						
					fantasma = true;
					break;
				}
			}				
			
			if(fantasma) return getBestMove(game);
			else return mAux;
		}
		else return direccion;
		//else return direccion;
	}

	private MOVE mejorDireccion(Game game) {
		GHOST fantasmaCerca = fantasmaComibleCerca(game); //si hay un fantasma comible voy a por el
		if(fantasmaCerca != null) return game.getApproximateNextMoveTowardsTarget(interseccionActual.identificador,
				game.getGhostCurrentNodeIndex(fantasmaCerca), game.getPacmanLastMoveMade(), DM.PATH);
						
		if(isInRisk(game)) //estoy en riesgo de que me coman			
			return getMoveHuir(game);
		else //no me van a comer
			return getBestMove(game);	
		
	}

	
	private MOVE proxMovimientoLlegada(int proxNodo,MOVE proxMove) {
		interseccion interLlegada = getInterseccion(proxNodo);
		for (MOVE m : MOVE.values()) {
			if (interLlegada.distancias.get(m) != null //existe
					&& interLlegada.destinos.get(m) == interseccionActual.identificador //es por el que venimos
					&& interLlegada.distancias.get(m) == interseccionActual.distancias.get(proxMove)) //
				return m;
		}

		return MOVE.NEUTRAL; // nunca deberia llegar
	}

	@Override
	public void preCompute(String opponent) {
		mapaHecho = false;
		mapa.clear();
		mapaActual = "";
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		if(game.getCurrentMaze().name != mapaActual){
			mapaActual = game.getCurrentMaze().name;
			mapa.clear();
			mapaHecho = false;
			
			//System.out.println(mapaActual);
		}
		if (!mapaHecho) { // solo entra aqui en el primer ciclo
			crearMapa(game);
			mapaHecho = true;
			ultimoNodo = -1; proximoNodo = -1; // -1 es que aun no ha registrado nada
			ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
			movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien

			return MOVE.NEUTRAL; // siempre la primera decision es izquierda abajo
		}
		
		if(game.wasPacManEaten()) { //tiene que resetear los valores predeterminados
			ultimoNodo = -1; proximoNodo = -1; // -1 es que aun no ha registrado nada
			ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
			movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
		}

		interseccionActual = getInterseccion(game.getPacmanCurrentNodeIndex());
		if (interseccionActual == null) { // si es null, no estas en una interseccion (AKA, estas en un pasillo)
			if (ultimoNodo != -1 && proximoNodo != -1)
				updateMapa(game); // solo hay que actualizarlo durante las rectas

			return MOVE.NEUTRAL;
		}

		// A PARTIR DE AQUI ESTAS EN UNA INTERSECCION
		MOVE proxMove = mejorDireccion(game);
		
		ultimoNodo = interseccionActual.identificador;
		proximoNodo = interseccionActual.destinos.get(proxMove);

		ultimoMovimientoReal = proxMove;
		movimientoDeLlegada = proxMovimientoLlegada(proximoNodo, proxMove);

		return proxMove; // es para que no salga en rojo
	}
}