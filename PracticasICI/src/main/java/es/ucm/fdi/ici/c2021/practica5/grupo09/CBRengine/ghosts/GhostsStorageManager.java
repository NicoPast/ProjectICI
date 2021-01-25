package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ParallelNNScoringMethod;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsStorageManager {

	Game game;
	GHOST ghostType;
	CBRCaseBase caseBase;
	EnumMap<GHOST, Vector<CBRCase>> buffer;

	private final static int TIME_WINDOW = 1;
	
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
	
	public void storeCase(CBRCase newCase, NNConfig simConfig)
	{			
		this.buffer.get(ghostType).add(newCase);
		
		//Check buffer for old cases to store
		if(this.buffer.get(ghostType).size() >TIME_WINDOW)
		{
			CBRCase bCase = this.buffer.get(ghostType).remove(0);
			reviseCase(bCase, simConfig);
		}
	}
	
	private void reviseCase(CBRCase bCase, NNConfig simConfig) {
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
		if(resultValue > 200 || lifesValue < 0 ||
			(description.getEdible() && game.isGhostEdible(ghostType) && resultDistance > 40) 
			|| (!description.getEdible() && !game.isGhostEdible(ghostType) && resultDistance < -40) ){	
				if(simConfig != null){
					Collection<RetrievalResult> eval = ParallelNNScoringMethod.evaluateSimilarityParallel(
							((GhostsCachedLinearCaseBase)caseBase).getCases(description.getEdible(), description.getIntersectionType()), bCase, simConfig);
					if(!eval.isEmpty()){		
						RetrievalResult first = SelectCases.selectTopKRR(eval, 1).iterator().next();
						
						GhostsSolution newCaseSolution = (GhostsSolution)bCase.getSolution();
						GhostsResult oldCaseResult = (GhostsResult) first.get_case().getResult();
						GhostsSolution oldCaseSolution = (GhostsSolution) first.get_case().getSolution();
						
						//Si es muy parecido, se aplasta si el anterior fue malo y yo bueno
						if(first.getEval() > 0.95 && oldCaseSolution.getMove() != newCaseSolution.getMove() && 
						oldCaseResult.getScore() > result.getScore() + 50 || (lifesValue < 0 && oldCaseResult.getPacmanHealth() == 0)){
							List<CBRCase> cases = new ArrayList<CBRCase>();
							cases.add(first.get_case());
							((GhostsCachedLinearCaseBase)caseBase).forgetCases(cases);
						}
					}
				}
				
				StoreCasesMethod.storeCase(this.caseBase, bCase);
		}
	}

	public void close() {
		for(GHOST g : GHOST.values()){
			for(CBRCase oldCase: this.buffer.get(ghostType))
				reviseCase(oldCase, null);
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
