package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

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
import es.ucm.fdi.ici.c2021.practica5.grupo09.GhostsAction;
import pacman.game.Constants.MOVE;

public class GhostsCBRengine implements StandardCBRApplication {

	private String casebaseFile;
	private MOVE move;
	private GhostsAction actionSelector;
	private GhostsStorageManager storageManager;

	CustomPlainTextConnector connector;
	CBRCaseBase caseBase;
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

		att = new Attribute("distanceNextIntersectionUp",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		att = new Attribute("distanceNextIntersectionDown",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		att = new Attribute("distanceNextIntersectionLeft",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		att = new Attribute("distanceNextIntersectionRight",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));

		att = new Attribute("distanceNearestGhostUp",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		att = new Attribute("distanceNearestGhostDown",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		att = new Attribute("distanceNearestGhostLeft",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		att = new Attribute("distanceNearestGhostRight",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		
		att = new Attribute("GhostEdibleUp",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());
		att = new Attribute("GhostEdibleDown",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());
		att = new Attribute("GhostEdibleLeft",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());
		att = new Attribute("GhostEdibleRight",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());

		att = new Attribute("edible",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());
		att = new Attribute("lastMove",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());
		att = new Attribute("distanceToPacMan",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
		
		att = new Attribute("pacmanLife",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Equal());
		att = new Attribute("score",GhostsDescription.class);
		simConfig.setWeight(att, 1.0);
		simConfig.addMapping(att, new Interval(650));
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		if(caseBase.getCases().isEmpty()) {
			this.move = actionSelector.defaultAction();
		}else {
			//Compute NN
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query, simConfig);
			
			// This simple implementation only uses 1NN
			// Consider using kNNs with majority voting
			RetrievalResult first = SelectCases.selectTopKRR(eval, 1).iterator().next();
			CBRCase mostSimilarCase = first.get_case();
			double similarity = first.getEval();
	
			GhostsResult result = (GhostsResult) mostSimilarCase.getResult();
			GhostsSolution solution = (GhostsSolution) mostSimilarCase.getSolution();
			
			MOVE solMove = MOVE.values()[solution.getMove()];

			//Now compute a solution for the query
			this.move = solMove;
			
			if(similarity<0.7) //Sorry not enough similarity, ask actionSelector for an action
				this.move = actionSelector.defaultAction();
			
			else if(result.getScore()<0) //This was a bad case, ask actionSelector for another one.
				this.move = actionSelector.findAnotherMove(solMove);
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
