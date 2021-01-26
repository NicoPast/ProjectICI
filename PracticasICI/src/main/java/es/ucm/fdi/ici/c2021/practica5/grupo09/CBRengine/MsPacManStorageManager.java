package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import pacman.game.Game;

public class MsPacManStorageManager {

	Game game;
	CBRCaseBase caseBase;
	Vector<CBRCase> buffer;
	Integer numCases = 0;

	private final static int TIME_WINDOW = 3;
	
	public MsPacManStorageManager()
	{
		this.buffer = new Vector<CBRCase>();
	}
	
	public void setGame(Game game) {
		this.game = game;
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
		else numCases++;
	}
	
	private void reviseCase(CBRCase bCase) {
		MsPacManDescription description = (MsPacManDescription)bCase.getDescription();
		int oldScore = description.getScore();
		int currentScore = game.getScore();
		int resultValue = currentScore - oldScore; //el incremento en puntuacion
		MsPacManResult result = (MsPacManResult)bCase.getResult();
		result.setScore(resultValue);
		
		//Store the old case right now into the case base
		//Alternatively we could store all them when game finishes in close() method
		StoreCasesMethod.storeCase(this.caseBase, bCase);
	}

	public void close() {
		for(CBRCase oldCase: this.buffer)
			reviseCase(oldCase);
		this.buffer.removeAllElements();
	}

	public int getPendingCases() {
		return numCases;
		//return this.buffer.size();
	}
}
