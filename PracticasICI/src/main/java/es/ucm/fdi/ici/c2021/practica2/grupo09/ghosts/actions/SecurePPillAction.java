package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.actions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica2.grupo09.MapaInfo.interseccion;
import es.ucm.fdi.ici.fsm.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class SecurePPillAction implements Action {

	private MapaInfo mapa;
    GHOST ghost;
	public SecurePPillAction( GHOST ghost,MapaInfo map ) {
		this.ghost = ghost;
		this.mapa = map;
	}

	@Override
	public MOVE execute(Game game) {
		if(!game.doesGhostRequireAction(ghost))
			return null;

		ClosestPowerPillAndDistance cppadToGhost = getClosestPowerPillAndDistance(game, game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
		ClosestPowerPillAndDistance cppadToPacMan = getClosestPowerPillAndDistance(game, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		interseccion inter = mapa.getInterseccion(game.getGhostCurrentNodeIndex(ghost));
		double greaterDistance = 0;

		double distanceToPowerPillPacMan = (cppadToGhost.powerpill == cppadToPacMan.powerpill) ? 
		cppadToPacMan.distance : game.getDistance(game.getPacmanCurrentNodeIndex(), cppadToGhost.powerpill, game.getPacmanLastMoveMade(), DM.PATH);
		
		MOVE bestMove = game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), cppadToGhost.powerpill, game.getGhostLastMoveMade(ghost), DM.EUCLID);
		if(inter != null) {		
			for (MOVE move : inter.distancias.keySet()) {
				if(move == game.getGhostLastMoveMade(ghost).opposite()) //No valoro el movimiento OPOSITE
					continue;
				double aux = getDistance(game, inter, move, cppadToGhost.powerpill);
				if(aux > greaterDistance && aux < distanceToPowerPillPacMan){
					greaterDistance = aux;
					bestMove = move;
				}
			}
			mapa.destinosGhosts.put(ghost, mapa.getInterseccion(inter.destinos.get(bestMove)));
		}
		return bestMove;
	}

	public class ClosestPowerPillAndDistance{
		public int powerpill = 0;
		public double distance =  Double.MAX_VALUE;
	}

	private double getDistance(Game game, interseccion actual, MOVE move, int ppil){
		double d = actual.distancias.get(move); //Distancia para llegar a la siguiente interseccion
		interseccion sig = mapa.getInterseccion(actual.destinos.get(move));
		//Conseguir el movimiento de llegada
		MOVE ultimo = null;
		for(MOVE m : sig.destinos.keySet()){
			//Si en la sig vuelvo a la de ahora con ese mov y la distancia es la misma, es que ese movimiento es ilegal
			if(sig.destinos.get(m) == actual.identificador && sig.distancias.get(m) == d){ 
				ultimo = m.opposite(); //El movimiento con el que llegue a la sig interseccion es la opposite de la ilegal
				break;
			}
		}
		d += game.getDistance(sig.identificador, ppil, ultimo, DM.PATH); //Distancia de llegada a la interseccion + distancia para ir a la ppil desde ahi 
		return d;
	}

	private ClosestPowerPillAndDistance getClosestPowerPillAndDistance(Game game, int pos, MOVE lastMoveMade){
		ClosestPowerPillAndDistance cpad = new ClosestPowerPillAndDistance();
		for (int currentPill : game.getActivePowerPillsIndices()) {
			double aux = game.getDistance(pos, currentPill, lastMoveMade, DM.PATH);
			if (aux < cpad.distance) {
				cpad.distance = aux;
				cpad.powerpill = currentPill;
			}
		}
		return cpad;
	}

}