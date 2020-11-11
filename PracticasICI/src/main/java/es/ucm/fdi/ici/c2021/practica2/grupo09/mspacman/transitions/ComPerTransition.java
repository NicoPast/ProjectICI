package es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class ComPerTransition implements Transition{

	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput)in; //usaremos esto para ver si hay un fantasma cerca o no
		
		return input.wasPowerPillEaten();
	}

	@Override
	public String toString() {
		return String.format("Eat powerPill transition");
	}
}
