package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.util.ArrayList;
import java.util.Collection;

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
	private Collection<CBRCase> EdibleCases;
	private Collection<CBRCase> StrongCases;
	private Collection<CBRCase> casesToRemove;

	/**
	 * Closes the case base saving or deleting the cases of the persistence media
	 */
	public void close() {
		EdibleCases.removeAll(casesToRemove);
		StrongCases.removeAll(casesToRemove);

		//le meto los edibles y luego le a�ado los strong
		Collection<CBRCase> casesToStore = new ArrayList<>(EdibleCases);
		casesToStore.addAll(StrongCases);
		
		casesToStore.removeAll(originalCases);

		connector.storeCases(casesToStore);
		connector.close();
	}

	/**
	 * Forgets cases. It only removes the cases from the storage media when closing.
	 */
	public void forgetCases(Collection<CBRCase> cases) {
		this.EdibleCases.removeAll(cases);
		this.StrongCases.removeAll(cases);
	}

	/**
	 * Returns all cases.
	 */
	public Collection<CBRCase> getCases() {
		Collection<CBRCase> allTogether = new ArrayList<>(EdibleCases);
		allTogether.addAll(StrongCases);
		return allTogether;
	}

	/**
	 * Returns working cases.
	 */
	public Collection<CBRCase> getCases(boolean edible) {
		return edible ? EdibleCases : StrongCases;
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
		EdibleCases = new ArrayList<>();
		StrongCases = new ArrayList<>();
		for(CBRCase c : originalCases) {
            if(((GhostsDescription)c.getDescription()).getEdible())
				this.EdibleCases.add(c);
			else 
				this.StrongCases.add(c);
		}
		casesToRemove = new ArrayList<>();
	}

	/**
	 * Learns cases that are only saved when closing the Case Base.
	 */
	public void learnCases(Collection<CBRCase> cases) {	
		for(CBRCase c : cases) {
            if(((GhostsDescription)c.getDescription()).getEdible())
            	this.EdibleCases.add(c);
			else 
				this.StrongCases.add(c);
		}
	}
}

