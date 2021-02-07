package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.io.File;
import java.util.Collection;
import java.util.EnumMap;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.connector.PlainTextConnector;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.gaia.jcolibri.util.FileIO;
import es.ucm.fdi.ici.c2021.practica5.grupo09.GhostsAction;
import pacman.game.Constants.MOVE;

public class GhostsCBRengine implements StandardCBRApplication {

	private String casebaseFile;
	private MOVE move;
	private GhostsAction actionSelector;
	private GhostsStorageManager storageManager;

	CustomPlainTextConnector connector;
	GhostsCachedLinearCaseBase caseBase;	
	
	final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2021/practica5/grupo09/CBRengine/ghostsplaintextconfig.xml"; //Cuidado!! poner el grupo aquí

	/**
	 * Simple extension to allow custom case base files. It also creates a new empty file if it does not exist.
	 */
	public class CustomPlainTextConnector extends PlainTextConnector {
		public void setCaseBaseFile(String casebaseFile) {
			super.PROP_FILEPATH = casebaseFile;
			try {
		         File file = new File(casebaseFile);
		         System.out.println(file.getAbsolutePath());
		         if(!file.exists())
		        	 file.createNewFile();
		      } catch(Exception e) {
		         e.printStackTrace();
		      }
		}
	}
	
	
	public GhostsCBRengine(GhostsAction actionSelector, GhostsStorageManager storageManager)
	{
		this.actionSelector = actionSelector;
		this.storageManager = storageManager;
	}
	
	
	public void setCaseBaseFile(String casebaseFile) {
		this.casebaseFile = casebaseFile;
	}
	
	@Override
	public void configure() throws ExecutionException {
		connector = new CustomPlainTextConnector();
		caseBase = new GhostsCachedLinearCaseBase();
		
		connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));
		connector.setCaseBaseFile(this.casebaseFile);
		this.storageManager.setCaseBase(caseBase);
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}
	
	
	@Override
	public void cycle(CBRQuery query) throws ExecutionException {

		GhostsDescription description = (GhostsDescription)query.getDescription();
		Collection<CBRCase> cases = caseBase.getCases(description.getEdible(), description.getIntersectionType());
		if(cases.isEmpty()) {
			this.move = actionSelector.defaultAction();
		}
		else {
			//Compute NN
			
			Collection<RetrievalResult> eval = GhostsCustomNN.customNN(cases, query);		
			Collection<RetrievalResult> similarCases = SelectCases.selectTopKRR(eval, 5);
			
			EnumMap<MOVE, Double> votacionPositiva = new EnumMap<>(MOVE.class);
			EnumMap<MOVE, Double> votacionNegativa = new EnumMap<>(MOVE.class);
			//Hacemos una votacion con el movimiento mas elegido por la similitud de la solucion
			for(RetrievalResult similarCase : similarCases){
				if(similarCase.getEval() > 0.7){ //Sumamos en la votacion: similitud^2 y en el score
					MOVE m = MOVE.values()[((GhostsSolution)similarCase.get_case().getSolution()).getMove()];
					GhostsDescription des = (GhostsDescription)similarCase.get_case().getDescription();
					GhostsResult res = (GhostsResult)similarCase.get_case().getResult();
					if(badCase(des, res))
						votacionNegativa.put(m, votacionNegativa.getOrDefault(m, 0.0) + similarCase.getEval() * similarCase.getEval());
					else if(goodCase(des, res))
						votacionPositiva.put(m, votacionPositiva.getOrDefault(m, 0.0) + similarCase.getEval() * similarCase.getEval());
				}
			}
			//Pillamos el movimiento Positivo mas votado
			MOVE mostPositiveVotedMove = MOVE.NEUTRAL;
			Double mostPositiveVotes = 0.0;
			for(MOVE m : votacionPositiva.keySet()){
				if(votacionPositiva.get(m) > mostPositiveVotes){
					mostPositiveVotes = votacionPositiva.get(m);
					mostPositiveVotedMove = m;
				}
			}
			MOVE mostNegativeVotedMove = MOVE.NEUTRAL;
			Double mostNegativeVotes = 0.0;
			for(MOVE m : votacionPositiva.keySet()){
				if(votacionPositiva.get(m) > mostNegativeVotes){
					mostNegativeVotes = votacionPositiva.get(m);
					mostNegativeVotedMove = m;
				}
			}
			if(mostPositiveVotes < 1 && mostNegativeVotes < 1) //Si no destaca ningun movimiento
				this.move = actionSelector.defaultAction();
			else if(mostPositiveVotes > mostNegativeVotes) //Se asume que el movimiento mas votado es bueno
				this.move = mostPositiveVotedMove;		
			else 
				this.move = actionSelector.findAnotherMove(mostNegativeVotedMove);
		}
		CBRCase newCase = createNewCase(query);
		this.storageManager.storeCase(newCase);
	}

	//Pacman gano demasiados puntos y no murio o se alejo o acerco cuando no debia
	boolean badCase(GhostsDescription description, GhostsResult result){
		return result.getScore() > 200 && result.getPacmanHealth() == 0 ||
			(description.getEdible() && result.deltaDistanceToPacMan > 20) 
			|| (!description.getEdible() && result.deltaDistanceToPacMan < -20);
	}

	boolean goodCase(GhostsDescription description, GhostsResult result){
		return result.getScore() < 200 && result.getPacmanHealth() < 0 ||
			(description.getEdible() && result.deltaDistanceToPacMan < -20) 
			|| (!description.getEdible() && result.deltaDistanceToPacMan > 20);
	}

	/**
	 * Creates a new case using the query as description, 
	 * storing the action into the solution and 
	 * setting the proper id number
	 */
	private CBRCase createNewCase(CBRQuery query) {
		CBRCase newCase = new CBRCase();
		GhostsDescription newDescription = (GhostsDescription) query.getDescription();
		GhostsResult newResult = new GhostsResult();
		GhostsSolution newSolution = new GhostsSolution();
		int newId = this.caseBase.getCases().size();
		newId+= storageManager.getPendingCases();
		newDescription.setId(newId);
		newResult.setId(newId);
		newSolution.setId(newId);
		newSolution.setMove(this.move.ordinal());
		newCase.setDescription(newDescription);
		newCase.setResult(newResult);
		newCase.setSolution(newSolution);
		return newCase;
	}
	
	public MOVE getSolution() {
		return this.move;
	}

	@Override
	public void postCycle() throws ExecutionException {
		this.storageManager.close();
		this.caseBase.close();
	}

}
