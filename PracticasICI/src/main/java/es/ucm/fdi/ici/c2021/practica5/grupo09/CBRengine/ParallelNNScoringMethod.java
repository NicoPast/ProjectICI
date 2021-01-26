package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;

public class ParallelNNScoringMethod {

	public static final int MIN_CASES_TO_PARALLELIZE = 80;
	
	public static Collection<RetrievalResult> evaluateSimilarityParallel(Collection<CBRCase> cases, CBRQuery query, NNConfig nnConfig)
	{
		int numCores = Runtime.getRuntime().availableProcessors();
		
		if(numCores == 1)
		{
			//org.apache.commons.logging.LogFactory.getLog(ParallelNNScoringMethod.class).info("Only 1 core detected. Using normal NNScoringMethod");
			return NNScoringMethod.evaluateSimilarity(cases, query, nnConfig);
		}
		
		if(cases.size()<MIN_CASES_TO_PARALLELIZE)
		{
			//org.apache.commons.logging.LogFactory.getLog(ParallelNNScoringMethod.class).info("Number of cases too low. Using normal NNScoringMethod");
			return NNScoringMethod.evaluateSimilarity(cases, query, nnConfig);
		}
		
		
		Collection<Collection<CBRCase>> setOfCases = new ArrayList<Collection<CBRCase>>();
		
		split(cases, setOfCases, numCores);
		
		return evaluateSimilarity(setOfCases, query, nnConfig);
	}
	
	
	private static void split(Collection<CBRCase> cases, Collection<Collection<CBRCase>> setOfCases, int numCores) {
		
		int totalCases = cases.size();
		int casesPerSet = totalCases/numCores;
		casesPerSet++;
		
		Iterator<CBRCase> cIter = cases.iterator();
		for(int i=0; i<numCores; i++)
		{
			ArrayList<CBRCase> set = new ArrayList<CBRCase>(casesPerSet);
			for(int c=0; (c<casesPerSet) && cIter.hasNext(); c++)
				set.add(cIter.next());
			setOfCases.add(set);
		}
		
	}



	public static Collection<RetrievalResult> evaluateSimilarity(Collection<Collection<CBRCase>> setOfCases, CBRQuery query, NNConfig nnConfig)
	{
		ArrayList<NNThread> threads = new ArrayList<NNThread>();
		
		int numCores = Runtime.getRuntime().availableProcessors();
		//org.apache.commons.logging.LogFactory.getLog(ParallelNNScoringMethod.class).info("Using "+numCores+" threads/processors");
		
        ExecutorService execSvc = Executors.newFixedThreadPool( numCores );
		
		for(Collection<CBRCase> caseSet: setOfCases)
		{
			NNThread thread = new NNThread(caseSet,query,nnConfig);
			threads.add(thread);
			execSvc.execute(thread);
		}
		
        execSvc.shutdown();

        try {
			execSvc.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		ArrayList<ArrayList<RetrievalResult>> result = new ArrayList<ArrayList<RetrievalResult>>();
		for(NNThread t: threads)
		{
			if(!t.getResult().isEmpty())
				result.add((ArrayList<RetrievalResult>)t.getResult());
		}
		
		
		return merge(result);
		
	}

	public static Collection<RetrievalResult> merge(ArrayList<ArrayList<RetrievalResult>> results)
	{
		ArrayList<RetrievalResult> merged = new ArrayList<RetrievalResult>();
		
		
		int remaining = results.size();
		boolean finish = false;
		
		while(!finish)
		{
			RetrievalResult max = null;
			ArrayList<RetrievalResult> maxList = null;
			
			if(remaining==1)
			{
				for(ArrayList<RetrievalResult> list : results)
					if(!list.isEmpty())
						merged.addAll(list);
				finish = true;
				continue;
			}
			
			
			for(ArrayList<RetrievalResult> list : results)
			{
				if(list.isEmpty())
					continue;
				
				if(max == null)
				{
					maxList = list;
					max = list.get(0);
				}
				else if(max.getEval()<list.get(0).getEval())
				{
					maxList = list;
					max = list.get(0);
				}
			}
			

			merged.add(maxList.remove(0));
			if(maxList.isEmpty())
				remaining--;

		}
		
		return merged;
		
	}	

}
