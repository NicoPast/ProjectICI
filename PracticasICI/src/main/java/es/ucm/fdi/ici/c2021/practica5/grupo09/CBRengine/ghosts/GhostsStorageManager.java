package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.util.EnumMap;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsStorageManager {

	Game game;
	GHOST ghostType;
	CBRCaseBase caseBase;
	EnumMap<GHOST, Vector<CBRCase>> buffer;

	private final static int TIME_WINDOW = 2;
	
	public GhostsStorageManager()
	{
		this.buffer = new EnumMap<>(GHOST.class);
		for(GHOST g : GHOST.values()){
			this.buffer.put(g, new Vector<CBRCase>());
		}
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
		this.buffer.get(ghostType).add(newCase);
		
		//Check buffer for old cases to store
		if(this.buffer.get(ghostType).size() >TIME_WINDOW)
		{
			CBRCase bCase = this.buffer.get(ghostType).remove(0);
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
		for(GHOST g : GHOST.values()){
			for(CBRCase oldCase: this.buffer.get(ghostType))
				reviseCase(oldCase);
			this.buffer.get(ghostType).removeAllElements();
		}
	}

	public int getPendingCases() {
		int size = 0;
		for(GHOST g : GHOST.values()){
			size += this.buffer.get(g).size();
		}
		return size;
	}
}
