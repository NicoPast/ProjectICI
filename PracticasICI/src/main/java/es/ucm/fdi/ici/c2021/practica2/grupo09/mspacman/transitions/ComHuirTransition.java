package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class ComHuirTransition implements Transition{

	double maxDistanceToPowerPill = 400;
	
	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput)in; //usaremos esto para ver si hay un fantasma cerca o no
		
		double dist = input.distanceToNearestPowerPill();
		
		if(dist == Double.MAX_VALUE) return true;
		else return dist > maxDistanceToPowerPill;
	}

	@Override
	public String toString() {
		return String.format("No power pills/power pill far away transition");
	}
}
