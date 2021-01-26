package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.io.File;
import java.util.Collection;
import java.util.EnumMap;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.connector.PlainTextConnector;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
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
	NNConfig simConfig;
	
	
	
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
		
		simConfig = new NNConfig();
		simConfig.setDescriptionSimFunction(new Average());
		

		Attribute att;

		
		
		// att = new Attribute("score",GhostsDescription.class);
		// simConfig.setWeight(att, 1.0);
		// simConfig.addMapping(att, new Interval(15000));
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
		}else {
			//Se filtran aquellas interseccion cuya solucion es imposible de realizar
			// Collection<CBRCase> filtered = new ArrayList<CBRCase>();
			// for(CBRCase c : cases){
			// 	if(((GhostsSolution)c.getSolution()).getMove() != MOVE.values()[((GhostsDescription)c.getDescription()).getLastMove()].opposite().ordinal())
			// 		filtered.add(c);
			// }

			// if(filtered.isEmpty()){
			// 	this.move = actionSelector.defaultAction();
			// }
			// else {

				//Compute NN
				Collection<RetrievalResult> eval = GhostsCustomNN.customNN(cases, query);
				
				Collection<RetrievalResult> similarCases = SelectCases.selectTopKRR(eval, 5);
				
				EnumMap<MOVE, Double> votacion = new EnumMap<>(MOVE.class);
				
				//Hacemos una votacion con el movimiento mas elegido por la similitud de la solucion
				for(RetrievalResult similarCase : similarCases){
					if(similarCase.getEval() > 0.7){ //Sumamos en la votacion: similitud^2 y en el score
						MOVE m = MOVE.values()[((GhostsSolution)similarCase.get_case().getSolution()).getMove()];
						votacion.put(m, votacion.getOrDefault(m, 0.0) + similarCase.getEval() * similarCase.getEval());
					}
				}
				//Pillamos el movimiento mas votado
				MOVE mostVotedMove = MOVE.NEUTRAL;
				Double mostVotes = 0.0;
				for(MOVE m : votacion.keySet()){
					if(votacion.get(m) > mostVotes){
						mostVotes = votacion.get(m);
						mostVotedMove = m;
					}
				}
				
				CBRCase mostSimilarCase = similarCases.iterator().next().get_case();
				double similarity = 0;
				
				for(RetrievalResult similarCase : similarCases){
					if(MOVE.values()[((GhostsSolution)similarCase.get_case().getSolution()).getMove()] == mostVotedMove && similarity < similarCase.getEval()){
						mostSimilarCase = similarCase.get_case();
						similarity = similarCase.getEval();
					}
				}
				
				GhostsResult result = (GhostsResult) mostSimilarCase.getResult();
				
				//Now compute a solution for the query
				this.move = mostVotedMove;
				
				if(similarity<0.7) //Sorry not enough similarity, ask actionSelector for an action
					this.move = actionSelector.defaultAction();
				
				else if(badCase((GhostsDescription)mostSimilarCase.getDescription(), result)) 
					this.move = actionSelector.findAnotherMove(mostVotedMove);
			}
		//}
		CBRCase newCase = createNewCase(query);
		this.storageManager.storeCase(newCase);
	}

	//Pacman gano demasiados puntos y no murio o se alejo o acerco cuando no debia
	boolean badCase(GhostsDescription description, GhostsResult result){
		return result.getScore() > 200 && result.getPacmanHealth() == 0 ||
			(description.getEdible() && result.deltaDistanceToPacMan > 40) 
			|| (!description.getEdible() && result.deltaDistanceToPacMan < -40);
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
