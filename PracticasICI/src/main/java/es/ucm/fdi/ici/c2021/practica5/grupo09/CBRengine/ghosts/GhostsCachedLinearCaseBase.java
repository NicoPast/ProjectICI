package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseBaseFilter;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Connector;
import es.ucm.fdi.gaia.jcolibri.exception.InitializingException;

/**
 * Cached case base that only persists cases when closing.
 * learn() and forget() are not synchronized with the persistence until close() is invoked.
 * <p>
 * This class presents better performance that LinelCaseBase as only access to the persistence once.
 * This case base is used for evaluation.
 * 
 * @author Juan A. Recio-García
 */
public class GhostsCachedLinearCaseBase implements CBRCaseBase {

	private Connector connector;
	private Collection<CBRCase> originalCases;

	private Collection<CBRCase> allCasesTogether;

	private Map<Integer, Collection<CBRCase>> edibleCases, strongCases;

	private Collection<CBRCase> casesToRemove;

	/**
	 * Closes the case base saving or deleting the cases of the persistence media
	 */
	public void close() {
		for(Integer tipo : edibleCases.keySet()){
			this.edibleCases.get(tipo).removeAll(casesToRemove);
		}
		for(Integer tipo : strongCases.keySet()){
			this.strongCases.get(tipo).removeAll(casesToRemove);
		}

		//le meto los edibles y luego le a�ado los strong
		Collection<CBRCase> casesToStore = new ArrayList<>();
		for(Integer tipo : edibleCases.keySet()){
			casesToStore.addAll(edibleCases.get(tipo));
		}
		for(Integer tipo : strongCases.keySet()){
			casesToStore.addAll(strongCases.get(tipo));
		}
		
		casesToStore.removeAll(originalCases);

		connector.storeCases(casesToStore);
		connector.close();
	}

	/**
	 * Forgets cases. It only removes the cases from the storage media when closing.
	 */
	public void forgetCases(Collection<CBRCase> cases) {
		for(Integer tipo : edibleCases.keySet()){
			this.edibleCases.get(tipo).removeAll(cases);
		}
		for(Integer tipo : strongCases.keySet()){
			this.strongCases.get(tipo).removeAll(cases);
		}
		this.allCasesTogether.removeAll(cases);
	}

	/**
	 * Returns all cases.
	 */
	public Collection<CBRCase> getCases() {
		return allCasesTogether;
	}

	/**
	 * Returns working cases.
	 */
	public Collection<CBRCase> getCases(boolean edible, int tipoInterseccion) {
		return edible ? edibleCases.get(tipoInterseccion) : strongCases.get(tipoInterseccion);
	}

	/**
	 * TODO.
	 */
	public Collection<CBRCase> getCases(CaseBaseFilter filter) {
		// TODO
		return null;
	}

	/**
	 * Initializes the Case Base with the cases read from the given connector.
	 */
	public void init(Connector connector) throws InitializingException {
		this.connector = connector;
		originalCases = this.connector.retrieveAllCases();	

		this.edibleCases = new HashMap<Integer, Collection<CBRCase>>();
		this.strongCases = new HashMap<Integer, Collection<CBRCase>>();
		for(int i = 0; i < 5; ++i){
			this.edibleCases.put(i, new ArrayList<>());
			this.strongCases.put(i, new ArrayList<>());
		}
	
		allCasesTogether = new ArrayList<>();

		learnCases(originalCases);

		casesToRemove = new ArrayList<>();
	}

	/**
	 * Learns cases that are only saved when closing the Case Base.
	 */
	public void learnCases(Collection<CBRCase> cases) {	
		GhostsDescription description;
		for(CBRCase c : cases) {
			allCasesTogether.add(c);
			description = ((GhostsDescription)c.getDescription()); 
			int tipo = description.getIntersectionType();
            if(description.getEdible())
				this.edibleCases.get(tipo).add(c);
			else 
				this.strongCases.get(tipo).add(c);
		}
	}
}

