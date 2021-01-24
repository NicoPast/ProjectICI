package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import java.io.File;
import java.util.Collection;
import java.util.Random;

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
import es.ucm.fdi.ici.c2021.practica5.grupo09.MsPacManActionSelector;
import pacman.game.Constants.MOVE;

public class MsPacManCBRengine implements StandardCBRApplication {

	private String casebaseFile;
	private MOVE move = MOVE.NEUTRAL;
	private MsPacManActionSelector actionSelector;
	private MsPacManStorageManager storageManager;

	CustomPlainTextConnector connector;
	CachedLinearCaseBase caseBase;
	NNConfig simConfig;
	CBRCase newCase = null; //el caso que vamos a ir guardando
	
	
	//PROVISIONAL (para definitivo, no te enfades Juan porfa, que es muy tarde)
	private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
	
	
	final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2021/practica5/grupo09/CBRengine/plaintextconfig.xml"; //Cuidado!! poner el grupo aqu√≠

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

		simConfig.addMapping(new Attribute("distanciaUp",MsPacManDescription.class), new Interval(150));
		simConfig.addMapping(new Attribute("distanciaRight",MsPacManDescription.class), new Interval(150));
		simConfig.addMapping(new Attribute("distanciaDown",MsPacManDescription.class), new Interval(150));
		simConfig.addMapping(new Attribute("distanciaLeft",MsPacManDescription.class), new Interval(150));

		simConfig.addMapping(new Attribute("ghostUp",MsPacManDescription.class), new Interval(150));
		simConfig.addMapping(new Attribute("ghostRight",MsPacManDescription.class), new Interval(150));
		simConfig.addMapping(new Attribute("ghostDown",MsPacManDescription.class), new Interval(150));
		simConfig.addMapping(new Attribute("ghostLeft",MsPacManDescription.class), new Interval(150));
		
		simConfig.addMapping(new Attribute("edibleUp",MsPacManDescription.class), new Equal());
		simConfig.addMapping(new Attribute("edibleRight",MsPacManDescription.class), new Equal());
		simConfig.addMapping(new Attribute("edibleDown",MsPacManDescription.class), new Equal());
		simConfig.addMapping(new Attribute("edibleLeft",MsPacManDescription.class), new Equal());

		simConfig.addMapping(new Attribute("vulnerable",MsPacManDescription.class), new Equal());

		simConfig.addMapping(new Attribute("lastMove",MsPacManDescription.class), new Interval(3)); //0 up, 1 right, 2 down, 3 left

		simConfig.addMapping(new Attribute("pillsUp",MsPacManDescription.class), new Interval(25));
		simConfig.addMapping(new Attribute("pillsRight",MsPacManDescription.class), new Interval(25));
		simConfig.addMapping(new Attribute("pillsDown",MsPacManDescription.class), new Interval(25));
		simConfig.addMapping(new Attribute("pillsLeft",MsPacManDescription.class), new Interval(25));

		simConfig.addMapping(new Attribute("powerPillUp",MsPacManDescription.class), new Interval(1));
		simConfig.addMapping(new Attribute("powerPillRight",MsPacManDescription.class), new Interval(1));
		simConfig.addMapping(new Attribute("powerPillDown",MsPacManDescription.class), new Interval(1));
		simConfig.addMapping(new Attribute("powerPillRight",MsPacManDescription.class), new Interval(1));

		simConfig.addMapping(new Attribute("score",MsPacManDescription.class), new Interval(20000));
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException { //se llama en cada interseccion

		//se guarda el caso en memoria
		if(newCase != null) this.storageManager.storeCase(newCase);			
		

		this.move = allMoves[rnd.nextInt(allMoves.length)];
		
		
		//pedir segun que lista
		MsPacManDescription descripcion = (MsPacManDescription)query.getDescription();
		Boolean vulnerable = descripcion.getVulnerable(); //para saber de que lista sacar
				
		if(caseBase.getCases(vulnerable).isEmpty()) {
			
			//de momento hace un random move
			this.move = allMoves[rnd.nextInt(allMoves.length)];
		}
		else { //ya tenemos algun caso guardado
			
			//Cargamos todos los casos
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(vulnerable), query, simConfig);
			//elegimos el top 5
			Collection<RetrievalResult> colaMejores = SelectCases.selectTopKRR(eval, 5);
			
			//nunca deberia de acabar siendo null por que entonces no habria ni entrado en el else
			CBRCase mostSimilarCase = null; //el caso mas similar que veremos en las proximas 5 pos
			double similarity = 0; //la similaridad del mejor caso respecto del actual
			//elegimos cual es el mejor caso de los dados			
			for(int i=0;i<5;i++) {
				//itera por todos los casosø?
				RetrievalResult caso = colaMejores.iterator().next();
				
				double similitudCaso;
				
				
				//similitudCaso += caso.
				
				
				mostSimilarCase = caso.get_case();
;				//cambiar el mostSimilarCase con la ponderacion
			}
			
	
			//la puntuacion que nos dice si ha sido bueno o no ø?
			MsPacManResult result = (MsPacManResult) mostSimilarCase.getResult();
						
			//el movimiento solucion
			MsPacManSolution solution = (MsPacManSolution) mostSimilarCase.getSolution();
			
			//Procesamos la colas
			
			
			if(similarity<0.7) //no es lo suficientemente parecido, tenemos que crear otro
				; //llamar al bestMove xd pero que de momento sea random
			
			else if(result.getScore()<0) //el mejor caso ha dado resultados maloes
				; //guess who's back: BESTMOVE
			
			
			else this.move = MOVE.valueOf(solution.getMove()); //cogemos el caso
		}
		
		newCase = createNewCase(query);
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
		newSolution.setMove(this.move.toString());
		newCase.setDescription(newDescription);
		newCase.setResult(newResult);
		newCase.setSolution(newSolution);
		return newCase;
	}
	
	public MOVE getSolution() {
		return move;
	}

	@Override
	public void postCycle() throws ExecutionException {
		this.storageManager.close();
		this.caseBase.close();
	}

}
