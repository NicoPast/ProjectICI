package es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica3.grupo09.ghosts.GhostsInput.ClosestPowerPillAndDistance;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostCanSecurePPillTransition implements Transition {

	GHOST ghost;
	public GhostCanSecurePPillTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	//Devuelvo true si soy el unico ghosts entre el pacman y mi pill mas cercana
	//Devuelvo true si entre los ghosts que estan entre el pacman y mi pill, soy el mas alejado del pacman
	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;

		if(input.getNumberOfActivePPilsLeft() == 0){
			return false;
		}

		Game game = input.getGame();
		ClosestPowerPillAndDistance my_cpad = input.getClosestPowerPillAndDistance(ghost);
		double dist_pacman = game.getDistance(game.getPacmanCurrentNodeIndex(), my_cpad.powerpill, game.getPacmanLastMoveMade(), DM.PATH);

		if(dist_pacman < my_cpad.distance) //Si el pacman llega antes que yo a mi powerpill, no puedo asegurarla
			return false;

		for(ClosestPowerPillAndDistance cpad : input.getClosestPowerPillAndDistances().values()){
			if(cpad.distance == my_cpad.distance && cpad.powerpill == my_cpad.powerpill) //No me valoro a mi mismo
				continue;
			//Si alguien está más cerca de la powerpill, que la asegure el otro, no yo
			if(cpad.powerpill == my_cpad.powerpill && cpad.distance < my_cpad.distance){ 
				return false;	
			}
		}
		//Si soy el más cercano a la powerpill más cercana a mi, la aseguro
		return true;
	}

	@Override
	public String toString() {
		return ghost.name()+" can secure Power Pill";
	}
}
