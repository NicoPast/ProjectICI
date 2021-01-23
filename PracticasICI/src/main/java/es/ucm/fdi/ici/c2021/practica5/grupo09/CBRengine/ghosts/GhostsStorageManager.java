package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class GhostsStorageManager {

	Game game;
	GHOST ghostType;
	CBRCaseBase caseBase;
	Vector<CBRCase> buffer;

	private final static int TIME_WINDOW = 1;
	
	public GhostsStorageManager()
	{
		this.buffer = new Vector<CBRCase>();
	}
	
	public void setGameAndGhost(Game game, GHOST ghost) {
		this.game = game;
		this.ghostType = ghost;
	}
	
	public void setCaseBase(CBRCaseBase caseBase)
	{
		this.caseBase = caseBase;
	}
	
	public void storeCase(CBRCase newCase)
	{			
		this.buffer.add(newCase);
		
		//Check buffer for old cases to store
		if(this.buffer.size()>TIME_WINDOW)
		{
			CBRCase bCase = this.buffer.remove(0);
			reviseCase(bCase);
		}
	}
	
	private void reviseCase(CBRCase bCase) {
		GhostsDescription description = (GhostsDescription)bCase.getDescription();
		int oldScore = description.getScore();
		int currentScore = game.getScore();
		int resultValue = currentScore - oldScore;

		int oldLifes = description.getPacmanLife();
		int currentLifes = game.getPacmanNumberOfLivesRemaining();
		int lifesValue = currentLifes - oldLifes;

		double oldDistance = description.getDistanceToPacMan();
		double currentDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghostType), game.getPacmanCurrentNodeIndex(),
			 game.getGhostLastMoveMade(ghostType), DM.PATH);
		double resultDistance = oldDistance - currentDistance;

		GhostsResult result = (GhostsResult)bCase.getResult();
		result.setScore(resultValue);
		result.setPacmanHealth(lifesValue);
		result.setDeltaDistanceToPacMan((int)resultDistance);

		//Se guarda si el pacMan ha ganado muchos puntos
		//Si ha muerto
		//Si el ghost se ha acercado demasiado siendo debil, o alejado siendo fuerte
		if(resultValue > 100 || lifesValue < 0 ||
			(description.getEdible() && game.isGhostEdible(ghostType) && resultDistance > 20) 
			|| (!description.getEdible() && !game.isGhostEdible(ghostType) && resultDistance < -20) ){

			StoreCasesMethod.storeCase(this.caseBase, bCase);
		}
	}

	public void close() {
		for(CBRCase oldCase: this.buffer)
			reviseCase(oldCase);
		this.buffer.removeAllElements();
	}

	public int getPendingCases() {
		return this.buffer.size();
	}
}
