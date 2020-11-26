package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class HuirComTransition implements Transition{
	
	double minDistanceToPowerPill = 400;
	
	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput)in; //usaremos esto para ver si hay un fantasma cerca o no
		
		double dist = input.distanceToNearestPowerPill();
		
		if(dist == Double.MAX_VALUE) return false;
		else return dist < minDistanceToPowerPill;
	}

	@Override
	public String toString() {
		return String.format("Power pill in range transition");
	}
}
