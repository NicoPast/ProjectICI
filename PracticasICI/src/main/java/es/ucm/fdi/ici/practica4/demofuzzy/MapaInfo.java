package es.ucm.fdi.ici.practica4.demofuzzy;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Vector;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

public class MapaInfo {

    private List<interseccion> mapa = new ArrayList<interseccion>();
    private Vector<Integer> posPowerPills = new Vector<Integer>();
	private int ultimoNodo = -1, proximoNodo = -1; // -1 es que aun no ha registrado nada
	private MOVE ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
	private MOVE movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
	private boolean checkLastMoveMade = false;
	private boolean mapaHecho = false;
	private	String mapaActual = "a";
	private interseccion interseccionActual;
	private DM metrica = DM.PATH;
	private boolean necesitamosReset = false;
	
	public EnumMap<GHOST, interseccion> destinosGhosts;
	public EnumMap<GHOST, MOVE> movesCheckMate;
	
	

    public MapaInfo() {

    }

    public class interseccion {
		public interseccion(int iden, EnumMap<MOVE, Integer> dir, EnumMap<MOVE, Integer> dest,
				EnumMap<MOVE, Integer> pi, EnumMap<MOVE, Integer> ppi) {
			identificador = iden;
			distancias = dir;
			destinos = dest;
			pills = pi;
			powerPill = ppi;
		}

		public int identificador; // node index
		public EnumMap<MOVE, Integer> distancias; // distancias
		public EnumMap<MOVE, Integer> destinos; // identificador del nodo en esa direccion
		public EnumMap<MOVE, Integer> pills; // pills en ese camino (entre nodo y nodo, las pills que hay en las intersecciones no cuentan)
		public EnumMap<MOVE, Integer> powerPill; // powerPills en ese camino
	}

    public void update(Game game){    	
        if(game.getCurrentMaze().name != mapaActual){
			mapaActual = game.getCurrentMaze().name;
			mapa.clear();
			mapaHecho = false;
		}
        
		if (!mapaHecho) { // solo entra aqui en el primer ciclo
			crearMapa(game);
			destinosGhosts = new EnumMap<GHOST, interseccion>(GHOST.class);
			movesCheckMate = new EnumMap<GHOST, MOVE>(GHOST.class);
            mapaHecho = true;
		}

		// Primero actualizo el mapa usando la posicion del pacman
		interseccion aux = getInterseccion(game.getPacmanCurrentNodeIndex());
		
		if (aux == null) { // si es null, no estas en una interseccion (AKA, estas en un pasillo)
			if (ultimoNodo != -1 && proximoNodo != -1)
				updateMapa(game); // solo hay que actualizarlo durante las rectas
			if (checkLastMoveMade) {
				MOVE m = game.getPacmanLastMoveMade();
				checkLastMoveMade = false;
				ultimoMovimientoReal = m;
				if(interseccionActual != null) {
					ultimoNodo = interseccionActual.identificador;
					if(interseccionActual.destinos.get(m) != null) {
						proximoNodo = interseccionActual.destinos.get(m);
						movimientoDeLlegada = proxMovimientoLlegada(m);						
					}
				}
			}
		}
		else {
			interseccionActual = aux;
			checkLastMoveMade = true;
		}
    }

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
			else if (proximoNodo.powerPillIndex != -1) {
				powerPills++;
				if(!posPowerPills.contains(proximoNodo.nodeIndex))
					posPowerPills.add(proximoNodo.nodeIndex);
			}
				
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
		if (fin - ini > 1) { // sigue habiendo varios nodos en el rango de b�squeda
			int mid = (fin - ini) / 2 + ini;
			if (mapa.get(mid).identificador <= iden)
				return interseccion_rec(mid, fin, iden); // esta en el lado derecho
			else
				return interseccion_rec(ini, mid, iden); // esta en el lado izquierdo
		} else if (mapa.get(ini).identificador == iden)
			return mapa.get(ini); // es de tama�o 1 por tanto devuelve el elemento inicial
		else
			return null;
	}

	public interseccion getInterseccion(int iden) { // usa divide y venceras para encontrar la interseccion
		return interseccion_rec(0, mapa.size(), iden);
	}

	private void updateMapa(Game game) {
		if (game.wasPillEaten()) {
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			if(interSalida != null) {
					int pills = interSalida.pills.get(ultimoMovimientoReal);
					if(pills > 0) {
						interSalida.pills.replace(ultimoMovimientoReal, pills, pills - 1);
						interLlegada.pills.replace(movimientoDeLlegada, pills, pills - 1);
					}				
			}

		}
		else if (game.wasPowerPillEaten()) {
			posPowerPills.removeElement(game.getPacmanCurrentNodeIndex());
			interseccion interSalida = getInterseccion(ultimoNodo);
			interseccion interLlegada = getInterseccion(proximoNodo);
			int pills = interSalida.powerPill.get(ultimoMovimientoReal); // no har�a falta esta variable ya que pasaria de 1 a 0,
			interSalida.powerPill.replace(ultimoMovimientoReal, pills, pills - 1); // pero si alguein nos quiere romper el programa poniendo mas de
			interLlegada.powerPill.replace(movimientoDeLlegada, pills, pills - 1); // una powerpill entre dos intersecciones le podemos callar la boca
		}
	}

	private MOVE proxMovimientoLlegada(MOVE proxMove) {
		interseccion interLlegada = getInterseccion(proximoNodo);
		for (MOVE m : MOVE.values()) {
			if (interLlegada.distancias.get(m) != null
					&& interLlegada.destinos.get(m) == interseccionActual.identificador
					&& interLlegada.distancias.get(m) == interseccionActual.distancias.get(proxMove))
				return m;
		}
		return MOVE.NEUTRAL; // nunca deberia llegar
	}

	public interseccion getInterseccionActual() { return interseccionActual; }
	public boolean getCheckLastModeMade() {return checkLastMoveMade;}
	public MOVE getUltimoMovimientoDeLlegada() {return movimientoDeLlegada;}
	public DM getMetrica() {return metrica;}
	

	public MOVE getBestMove(Game game) {		

	
		Vector<MOVE> fantasmas = new Vector<MOVE>();
		Vector<MOVE> powerPills = new Vector<MOVE>();
		Vector<MOVE> noPills = new Vector<MOVE>();
		Vector<MOVE> pills = new Vector<MOVE>();
		Vector<GHOST> fantasmasComibles = new Vector<GHOST>();
				
		for (MOVE m : MOVE.values()) { 
			//mira si m no es de donde vienes, si no es neutral y si existe camino
			
			if (m != movimientoDeLlegada && m != MOVE.NEUTRAL && interseccionActual.distancias.get(m) != null) {				
				boolean hasGhost = false;
				GHOST eadableGhost = null;
				//mira para todos los fantasmas, si avanzando por ese camino me pillan
				for (GHOST g : GHOST.values()) {
					double distancia = game.getDistance( game.getGhostCurrentNodeIndex(g),interseccionActual.destinos.get(m),
							game.getGhostLastMoveMade(g),DM.PATH);
					if (distancia > 0 && (distancia <= interseccionActual.distancias.get(m) + 2 ||
							(game.getDistance(game.getGhostCurrentNodeIndex(g), game.getPacmanCurrentNodeIndex(),
									game.getGhostLastMoveMade(g),DM.PATH)  <=  interseccionActual.distancias.get(m) + 2 
									&& game.getDistance( game.getGhostCurrentNodeIndex(g),interseccionActual.destinos.get(m),DM.PATH)
									 <= interseccionActual.distancias.get(m) + 2)) ||
							lairDanger(game,interseccionActual.destinos.get(m),m))
					{ // no pillar el camino						
						
						if(!game.isGhostEdible(g)){
							hasGhost = true;
							fantasmas.add(m); //por aqui hay fantasma, meterlo a la lista de caminos con fantasmas		
							eadableGhost = null;	
							break; //hacemos el breake por que ya no nos interesa seguir buscando
						}
						else {							
							eadableGhost = g;						
						}

					}
				}
								
				
				if(!hasGhost) { //miramos si vamos a ir por el camino del spawn
					if(eadableGhost != null)
						fantasmasComibles.add(eadableGhost);
					else if(interseccionActual.powerPill.get(m) > 0)
						powerPills.add(m);
					else if(interseccionActual.pills.get(m) == 0)//mira si el camino no tiene pills
						noPills.add(m);
					else
						pills.add(m); //en el camino solo hay pills
					
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
						game.getPacmanLastMoveMade(),metrica);
				if(dis < auxDistGhost) {
					ghostAux = g;
					auxDistGhost = dis;
				}
			}
			//tenemos que mirar si en esta direccion no hay otros fantasmas
			
			actual = game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(ghostAux), game.getPacmanLastMoveMade(), metrica);
			
			if(!fantasmas.contains(actual)) return actual;
		}
		
		
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
			MOVE auxMove = game.getNextMoveTowardsTarget(interseccionActual.identificador, getClosestPill(game), 
					game.getPacmanLastMoveMade(), DM.PATH);
			
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
					if(distAux < distanciaMinima) {
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
				else if(maxPills == 0 && (aux == -1 || interseccionActual.distancias.get(fantasmas.get(i)) + 2 > aux)) {
					aux = interseccionActual.distancias.get(fantasmas.get(i)) + 2;
					actual = fantasmas.elementAt(i);
				}
			}
		}		
		
		if(actual == MOVE.NEUTRAL) {
			System.out.println("Se mamó");
		}
		return actual;
	}
	
	
    public int getClosestPill(Game game) {
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
    
    public void setReset() {
    	mapaHecho = false;
        mapaActual = "a";
		mapa.clear();
		
		ultimoNodo = -1; proximoNodo = -1; // -1 es que aun no ha registrado nada
		ultimoMovimientoReal = MOVE.LEFT; // es down por que este programa siempre devuelve down
		movimientoDeLlegada = MOVE.RIGHT; // PROVISIONAL tambien
		checkLastMoveMade = false;
    }
    
    //mira si vas a ir por el origen y si en ese instante te puede aparecer un fantasma
    public boolean lairDanger(Game game, int destino, MOVE direccion) { //destino = prox interseccion//direccion=movimiento tomado
    	
    	
    	int pacManNodeIndex = game.getPacmanCurrentNodeIndex();
    	//primero, mirar si el camino es el del inicio
    	if(game.getNextMoveTowardsTarget(pacManNodeIndex, game.getGhostInitialNodeIndex(),
    			game.getPacmanLastMoveMade(), DM.PATH) != direccion) return false; //no pasamos por el inicio

    	//podriamos estar en ese camino, mirar distancias
    	
    	//distancia hasta el lair
    	double distAC = game.getDistance(pacManNodeIndex, game.getGhostInitialNodeIndex(), 
    			game.getPacmanLastMoveMade(), DM.PATH);
    	//distancia hasta el proximo nodo
    	double distAB = game.getDistance(pacManNodeIndex, destino,game.getPacmanLastMoveMade(), DM.PATH);
    	
    	if(distAC < distAB) { //pasamos por el inicio
    		//miramos si nos puede aparecer un fantasma
    		for(GHOST g:GHOST.values()) {
    			int lairTime = game.getGhostLairTime(g);
    			//System.out.println(lairTime);
    			if(lairTime >0) { //hay alguien dentro que nos podria comer
    				return true;
    			}
    		}
    	}    	
    	return false;
    }	
    
    public Integer getClosestPP(Game game) {
    	int pp = -1;
    	double dist = Double.MAX_VALUE;
    	for(int index: posPowerPills) {
    		double aux = game.getDistance(game.getPacmanCurrentNodeIndex(), index,
    				game.getPacmanLastMoveMade(), DM.PATH);
    		if (aux < dist) {
    			dist = aux;
    			pp = index;
    		}
    	}
    	return pp;
    }
}
