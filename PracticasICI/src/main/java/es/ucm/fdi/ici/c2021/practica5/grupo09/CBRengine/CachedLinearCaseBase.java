package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

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
public class CachedLinearCaseBase implements CBRCaseBase {

	private Connector connector;
	private Collection<CBRCase> originalCases;
	
	//las dos distintas listas para mejorar rendimiento
	private Collection<CBRCase> casesNotVulnerable;
	private Collection<CBRCase> casesVulnerable;
	
	private Collection<CBRCase> casesToRemove;
	/**
	 * Closes the case base saving or deleting the cases of the persistence media
	 */
	public void close() {
		casesNotVulnerable.removeAll(casesToRemove);
		casesVulnerable.removeAll(casesToRemove);
		
		Collection<CBRCase> casesToStore = new ArrayList<>(casesNotVulnerable);
		casesToStore.addAll(casesVulnerable);
		casesToStore.removeAll(originalCases);

		connector.storeCases(casesToStore);
		connector.close();
	}

	/**
	 * Forgets cases. It only removes the cases from the storage media when closing.
	 */
	public void forgetCases(Collection<CBRCase> cases) {
		casesNotVulnerable.removeAll(cases);
		casesVulnerable.removeAll(cases);
	}

	/**
	 * Returns working cases.
	 */
	public Collection<CBRCase> getCases() {
		//nunca se deberia llamar a esto
		Collection<CBRCase> aux = new ArrayList<>(casesNotVulnerable);
		aux.addAll(casesVulnerable);
		return aux;
	}
	
	public Collection<CBRCase> getCases(Boolean vulnerable){
		return vulnerable ? casesNotVulnerable : casesVulnerable;
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
		
		
		casesNotVulnerable = new ArrayList<CBRCase>();
		casesVulnerable = new ArrayList<CBRCase>();		
		
		//hay que leer todos los casos y clasificarlos en su lista correspondiente
		for(CBRCase caso : originalCases) {
			if(((MsPacManDescription)caso.getDescription()).getVulnerable()) 
				casesVulnerable.add(caso);
			else 
				casesNotVulnerable.add(caso);
		}
		
		
		casesToRemove = new ArrayList<>();
	}

	/**
	 * Learns cases that are only saved when closing the Case Base.
	 */
	public void learnCases(Boolean vulnerable , Collection<CBRCase> cases) {
		if(vulnerable) casesNotVulnerable.addAll(cases);
		else casesNotVulnerable.addAll(cases);
	}
	
	public void learnCases(Collection<CBRCase> cases) {
		casesNotVulnerable.addAll(cases);
	}

}

