package es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.transitions;

import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2021.practica2.grupo09.ghosts.GhostsInput.ClosestPowerPillAndDistance;
import es.ucm.fdi.ici.fsm.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostNotSecuringPPillTransition implements Transition {

	GHOST ghost;
	public GhostNotSecuringPPillTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}
	
	//Si o bien ya no puedo asegurar la powerpill o no es rentable
	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
					
		if(input.getNumberOfActivePPilsLeft() == 0){
			return true;
		}
		
		Game game = input.getGame();
		ClosestPowerPillAndDistance my_cpad = input.getClosestPowerPillAndDistance(ghost);
		double dist_pacman = game.getDistance(game.getPacmanCurrentNodeIndex(), my_cpad.powerpill, game.getPacmanLastMoveMade(), DM.PATH);

		if(dist_pacman <= my_cpad.distance) //Si el pacman llega antes que yo a mi powerpill, no puedo asegurarla
			return true;

		for(ClosestPowerPillAndDistance cpad : input.getClosestPowerPillAndDistances().values()){
			if(cpad.distance == my_cpad.distance && cpad.powerpill == my_cpad.powerpill) //No me valoro a mi mismo
				continue;
			//Si alguien está más cerca de la powerpill, que la asegure el otro, no yo
			if(cpad.powerpill == my_cpad.powerpill && cpad.distance < my_cpad.distance){ 
				return true;	
			}
		}
		//Si soy el más cercano a la powerpill más cercana a mi, sigo asegurandola
		return false;
	}
	@Override
	public String toString() {
		return ghost.name()+" is now attacking";
	}
}
