package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
public class IsCheckMateTransition implements Transition {

	MapaInfoGhost mapa;
	int id;
	public IsCheckMateTransition(MapaInfoGhost mapa, int id) {
		super();
		this.mapa = mapa;
		this.id = id;
	}
	
	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;

		return input.getIsCheckMate();
	}

	@Override
	public String toString() {
		return "Check Mate!!"+id;
	}
}