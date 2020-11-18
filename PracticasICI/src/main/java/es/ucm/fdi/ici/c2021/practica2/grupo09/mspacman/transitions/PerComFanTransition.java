package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class PerComFanTransition implements Transition {

	private double distanciaPeligro = 40;
	
	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput)in; //usaremos esto para ver si hay un fantasma cerca o no
		
		//quedan fantasmas vivos
		//buscar el mas cercano
		//mirar si en esa direccion hay otro fantasma que me pueda comer

		return (input.distToNearestGhostNonEadable() < distanciaPeligro);
	}

	@Override
	public String toString() {
		return String.format("Non eadable ghost near");
	}
}
