package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.pacman;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
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
import es.ucm.fdi.ici.c2021.practica5.grupo09.MapaInfo;
import es.ucm.fdi.ici.c2021.practica5.grupo09.MsPacManActionSelector;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.INTER;
import pacman.game.Game;

public class MsPacManCBRengine implements StandardCBRApplication {

	private String casebaseFile;
	private MsPacManActionSelector actionSelector;
	private MsPacManStorageManager storageManager;
	private Action action;
	
	CustomPlainTextConnector connector;
	CachedLinearCaseBase caseBase;
	NNConfig simConfig;
	CBRCase newCase = null; //el caso que vamos a ir guardando
	MapaInfo mapInfo;
	Game game;
	
	Queue<CBRCase> cases_a_guardar = new LinkedList<CBRCase>();
	String actionsStrings[] = {"ChaseAction", "ChillAction", "RunAwayAction"};
	
	INTER intersecciones[] = {INTER.CRUZ,INTER.T_HOR,INTER.L_INVER,INTER.T_INVER,INTER.L_VERT};
	
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
	
	
	public MsPacManCBRengine(MsPacManActionSelector actionSelector, MsPacManStorageManager storageManager, MapaInfo map)
	{
		this.actionSelector = actionSelector;
		this.storageManager = storageManager;
		this.mapInfo = map;
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

		simConfig.addMapping(new Attribute("distClosestEdibleGhost",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("distClosestGhost",MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("distToPowerPill",MsPacManDescription.class), new Interval(650));
		Attribute att = new Attribute("numPills",MsPacManDescription.class);
		simConfig.addMapping(att, new Interval(100));
		simConfig.setWeight(att, 100.);

		simConfig.addMapping(new Attribute("vulnerable",MsPacManDescription.class), new Equal());

		simConfig.addMapping(new Attribute("score",MsPacManDescription.class), new Interval(20000));
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException { //se llama en cada interseccion

		//se guarda el caso en memoria, hay que esperar 3 ciclos
		if(cases_a_guardar.size() == 3) {
			CBRCase casAux = cases_a_guardar.poll();
			MsPacManDescription res = (MsPacManDescription)casAux.getDescription();
			res.setScore(game.getScore() - res.getScore());
			//System.out.println(res.getScore());
			if(res.getScore()>100 && res.getNumPills() > 0) this.storageManager.storeCase(casAux); //solo lo guardamos si es un caso bueno
		}
				
		//pedir segun que lista
		MsPacManDescription descripcion = (MsPacManDescription)query.getDescription();
		Boolean vulnerable = descripcion.getVulnerable(); //para saber de que lista sacar

		Double bestVote = 0.0;
		
		INTER tipoInter = intersecciones[descripcion.getTipoInterseccion()]; //se necesita el tipo de interseccion			
		
		Collection<CBRCase> casos =caseBase.getCases(vulnerable, tipoInter);
		if(casos.isEmpty()) {		
			this.action = actionSelector.findAction(); 
			}
		else { //ya tenemos algun caso guardado		
			//Cargamos todos los casos
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(casos,
					query, simConfig);
					//customNN(casos, query);
			//elegimos el top 5
			Collection<RetrievalResult> colaMejores = SelectCases.selectTopKRR(eval, 5);
			
			//elegimos cual es el mejor caso de los dado (votacion ponderada
			int votos[] = {0,0,0}; //votos para cada estadp 0=chase, 1=chill, 2=runaway
			double probabilidadesAcumuladas[] = {0,0,0};
			
			for(RetrievalResult caso : colaMejores) {			
				String actionRes = ((MsPacManSolution)(caso.get_case()).getSolution()).getAction();
				if(actionRes.equals("ChaseAction")) {
					votos[0]++;
					probabilidadesAcumuladas[0] += caso.getEval();
				}
				else if(actionRes.equals("ChillAction")){
					votos[1]++;
					probabilidadesAcumuladas[1] += caso.getEval();
				}
				else if(actionRes.equals("RunAwayAction")){
					votos[2]++;
					probabilidadesAcumuladas[2] += caso.getEval();
				}
			}
			
			//sacar la media ponderada
			int accionResultante = 1; //por defecto que sea chill
			
			for(int i=0;i<3;i++) {
				double media = probabilidadesAcumuladas[i] / votos[i];
				if(media > bestVote) {
					bestVote = media;
					accionResultante = i;
				}
			}
			
			this.action = actionSelector.getAction(actionsStrings[accionResultante]);
		}

		if(bestVote < 0.999) 
		{ //si el caso no es lo sufucientemente parecido
			this.action = actionSelector.findAction(); //saca una accion random			
		}
		
		
		newCase = createNewCase(query);
		cases_a_guardar.add(newCase);
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
		int newId = this.caseBase.getNumCases();
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
		return action;
	}

	@Override
	public void postCycle() throws ExecutionException {
		this.storageManager.close();
		this.caseBase.close();
	}
	
	public void setMap(MapaInfo map) {
		this.mapInfo = map;
	}
	
	public void setGame(Game _game) {
		this.game = _game;
	}
	
	private Collection<RetrievalResult> customNN(Collection<CBRCase> cases, CBRQuery query) {

		// Parallel stream

		List<RetrievalResult> res = cases.parallelStream()
		                .map(c -> new RetrievalResult(c, computeSimilarity(query.getDescription(), c.getDescription())))
		                .collect(Collectors.toList());

		    // Sort the result

		        res.sort(RetrievalResult::compareTo);

		 	return res;

		}
	
	private Double computeSimilarity(CaseComponent description, CaseComponent description2) {

		MsPacManDescription _query = (MsPacManDescription)description;

		MsPacManDescription _case = (MsPacManDescription)description2;

		double simil = 0;

		double aux;
		aux = Math.abs(_query.getScore()+1-_case.getScore())/20000;
		simil += (aux == 0.0)? 1.0 : aux;
		aux=Math.abs(_query.getDistClosestEdibleGhost()+1-_case.getDistClosestEdibleGhost())/650;
		simil += aux;//(aux == 0.0) ? 1.0 : aux;
		aux = Math.abs(_query.getDistClosestGhost()+1-_case.getDistClosestGhost())/650;
		simil += aux;//(aux == 0.0)? 1.0: aux;
		aux = Math.abs(_query.getDistToPowerPill()+1-_case.getDistToPowerPill())/650;
		simil += aux;//(aux == 0.0) ? 1.0 : aux;
		
		simil += _query.getVulnerable().equals(_case.getVulnerable()) ? 1.0 : 0.0;

		return simil/5.0;

		}

}
