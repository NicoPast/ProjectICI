package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import java.util.Collection;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;

public class NNThread implements Runnable {

	Collection<CBRCase> cases;
	CBRQuery query;
	NNConfig nnConfig;
	Collection<RetrievalResult> result;

	/**
	 * @param cases
	 * @param query
	 * @param nnConfig
	 */
	public NNThread(Collection<CBRCase> cases, CBRQuery query, NNConfig nnConfig) {
		super();
		this.cases = cases;
		this.query = query;
		this.nnConfig = nnConfig;
	}



	public void run() {
		result = NNScoringMethod.evaluateSimilarity(cases, query, nnConfig);
	}
	
	public Collection<RetrievalResult> getResult()
	{
		return result;
	}

}
