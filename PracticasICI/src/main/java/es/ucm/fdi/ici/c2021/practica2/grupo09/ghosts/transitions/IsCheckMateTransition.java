package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class IsCheckMateTransition implements Transition {

	MapaInfo mapa;

	public IsCheckMateTransition(MapaInfo mapa) {
		super();
		this.mapa = mapa;
	}

	private class interseccion_plus{
		public interseccion intersection;
		public interseccion prohibida;
		public interseccion_plus(interseccion i, interseccion iProhibida) { intersection = i; prohibida = iProhibida;}
	};

	
	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;

		if(input.checkMateCalculated)
			return input.isCheckMate;
		if(input.isPacManCloserToPowerPill()) 
			return false;


		Game g = input.getGame();
		Set<interseccion_plus> visitadas = new HashSet<interseccion_plus>();
		// rellenamos un array con los nodos de los fantasmas
		Vector<GHOST> ghosts = input.getActiveGhosts();
		if(ghosts.isEmpty())
			return false;

		Vector<Integer> nodosFijos = new Vector<Integer>();

		interseccion_plus[] aux = new interseccion_plus[6];
		rellenarProxDestinosPacMan(visitadas, nodosFijos);
		
		if(!visitadas.isEmpty())
			visitadas.toArray(aux);

		int i = 0;
		while (ghosts.size() > 0 && visitadas.size() > 0 && ghosts.size() - visitadas.size() >= 0 && i < visitadas.size()) {
			GHOSTANDDISTANCE gyd = closestGhostToIntersection(g, aux[i].intersection.identificador, ghosts);
			if(gyd.distance <= 1){
				mapa.movesCheckMate.put(gyd.ghost, g.getNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(gyd.ghost), g.getPacmanCurrentNodeIndex(), g.getGhostLastMoveMade(gyd.ghost), DM.EUCLID));
				ghosts.remove(gyd.ghost);
				nodosFijos.add(g.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			}
			else if (gyd.distance < g.getDistance(g.getPacmanCurrentNodeIndex(), aux[i].intersection.identificador, g.getPacmanLastMoveMade(), DM.PATH)) {
				mapa.movesCheckMate.put(gyd.ghost, g.getNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(gyd.ghost), aux[i].intersection.identificador, g.getGhostLastMoveMade(gyd.ghost),  DM.EUCLID));
				ghosts.remove(gyd.ghost);
				nodosFijos.add(g.getGhostCurrentNodeIndex(gyd.ghost));
				i++;
			}
			else {
				rellenarProxDestinosPacMan(visitadas, nodosFijos);
				visitadas.toArray(aux);
				i = 0;
			}		
		}
		input.checkMateCalculated = true;
		input.isCheckMate = visitadas.size() - i == 0;
		return visitadas.size() - i == 0;
	}

	private class GHOSTANDDISTANCE {
		public GHOST ghost;
		public double distance = Double.MAX_VALUE;
	}

	private GHOSTANDDISTANCE closestGhostToIntersection(Game game, int inter, Vector<GHOST> libres) {
		GHOSTANDDISTANCE gyd = new GHOSTANDDISTANCE();
		for (GHOST ghost : libres) {
			double aux = game.getDistance(game.getGhostCurrentNodeIndex(ghost), inter, game.getGhostLastMoveMade(ghost), DM.PATH);
			if (aux < gyd.distance) {
				gyd.ghost = ghost;
				gyd.distance = aux;
			}
		}
		return gyd;
	}

	private void rellenarProxDestinosPacMan(Set<interseccion_plus> inters, Vector<Integer> nodosFijos) {
		interseccion intActual = mapa.getInterseccionActual();
		if (inters.isEmpty()){
			if(mapa.getCheckLastModeMade()) 
				inters.add(new interseccion_plus(intActual, intActual));
			else 
				inters.add(new interseccion_plus(mapa.getInterseccion(intActual.destinos.get(mapa.getUltimoMovReal())), intActual));
		}
		else {
			Set<interseccion_plus> aux = new HashSet<interseccion_plus>(inters);
			inters.clear();
			// Buscamos las intersecciones correspondientes a los movimientos posibles
			// y si ya está esa intersección fijada no expandimos su rama
			for (interseccion_plus a : aux) {
				if (!nodosFijos.contains(a.intersection.identificador)) {
					for (MOVE mo : a.intersection.destinos.keySet()) {
						if(mapa.getInterseccion(a.intersection.destinos.get(mo)) != a.prohibida){
							inters.add(new interseccion_plus(mapa.getInterseccion(a.intersection.destinos.get(mo)), a.intersection));
						}
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Check Mate!!";
	}
}
