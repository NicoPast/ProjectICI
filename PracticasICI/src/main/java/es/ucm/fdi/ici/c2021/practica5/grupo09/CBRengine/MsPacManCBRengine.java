package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import java.io.File;
import java.util.Collection;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.connector.PlainTextConnector;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.gaia.jcolibri.util.FileIO;
import es.ucm.fdi.ici.c2021.practica5.grupo09.Action;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MsPacManActionSelector;

public class MsPacManCBRengine implements StandardCBRApplication {

	private String casebaseFile;
	private Action action;
	private MsPacManActionSelector actionSelector;
	private MsPacManStorageManager storageManager;

	CustomPlainTextConnector connector;
	CBRCaseBase caseBase;
	NNConfig simConfig;
	
	
	
	final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2021/practica5/grupo09/CBRengine/plaintextconfig.xml"; //Cuidado!! poner el grupo aquí

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
	
	
	public MsPacManCBRengine(MsPacManActionSelector actionSelector, MsPacManStorageManager storageManager)
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
		caseBase = new CachedLinearCaseBase();
		
		connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));
		connector.setCaseBaseFile(this.casebaseFile);
		this.storageManager.setCaseBase(caseBase);
		
		simConfig = new NNConfig();
		simConfig.setDescriptionSimFunction(new Average());	

		simConfig.addMapping(new Attribute("distanciaUp",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("distanciaRight",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("distanciaDown",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("distanciaLeft",MsPacManDescription.class), new Interval(650));

		simConfig.addMapping(new Attribute("ghostUp",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("ghostRight",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("ghostDown",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("ghostLeft",MsPacManDescription.class), new Interval(650));
		
		simConfig.addMapping(new Attribute("edibleUp",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("edibleRight",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("edibleDown",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("edibleLeft",MsPacManDescription.class), new Interval(650));

		simConfig.addMapping(new Attribute("vulnerable",MsPacManDescription.class), new Equal());

		simConfig.addMapping(new Attribute("direction",MsPacManDescription.class), new Interval(4));

		simConfig.addMapping(new Attribute("pillsUp",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("pillsRight",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("pillsDown",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("pillsLeft",MsPacManDescription.class), new Interval(650));

		simConfig.addMapping(new Attribute("powerPillUp",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("powerPillRight",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("powerPillDown",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("powerPillRight",MsPacManDescription.class), new Interval(650));

		simConfig.addMapping(new Attribute("score",MsPacManDescription.class), new Interval(15000));
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		if(caseBase.getCases().isEmpty()) {
			this.action = actionSelector.findAction();
		}else {
			//Compute NN
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query, simConfig);
			
			// This simple implementation only uses 1NN
			// Consider using kNNs with majority voting
			RetrievalResult first = SelectCases.selectTopKRR(eval, 1).iterator().next();
			CBRCase mostSimilarCase = first.get_case();
			double similarity = first.getEval();
	
			MsPacManResult result = (MsPacManResult) mostSimilarCase.getResult();
			MsPacManSolution solution = (MsPacManSolution) mostSimilarCase.getSolution();
			
			//Now compute a solution for the query
			this.action = actionSelector.getAction(solution.getAction());
			
			if(similarity<0.7) //Sorry not enough similarity, ask actionSelector for an action
				this.action = actionSelector.findAction();
			
			else if(result.getScore()<0) //This was a bad case, ask actionSelector for another one.
				this.action = actionSelector.findAnotherAction(solution.getAction());
		}
		CBRCase newCase = createNewCase(query);
		this.storageManager.storeCase(newCase);
		
	}

	/**
	 * Creates a new case using the query as description, 
	 * storing the action into the solution and 
	 * setting the proper id number
	 */
	private CBRCase createNewCase(CBRQuery query) {
		CBRCase newCase = new CBRCase();
		MsPacManDescription newDescription = (MsPacManDescription) query.getDescription();
		MsPacManResult newResult = new MsPacManResult();
		MsPacManSolution newSolution = new MsPacManSolution();
		int newId = this.caseBase.getCases().size();
		newId+= storageManager.getPendingCases();
		newDescription.setId(newId);
		newResult.setId(newId);
		newSolution.setId(newId);
		newSolution.setAction(this.action.getActionId());
		newCase.setDescription(newDescription);
		newCase.setResult(newResult);
		newCase.setSolution(newSolution);
		return newCase;
	}
	
	public Action getSolution() {
		return this.action;
	}

	@Override
	public void postCycle() throws ExecutionException {
		this.storageManager.close();
		this.caseBase.close();
	}

}
