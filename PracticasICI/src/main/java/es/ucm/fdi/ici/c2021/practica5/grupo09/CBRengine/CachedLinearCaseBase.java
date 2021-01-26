package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;

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
	private EnumMap<INTER, Collection<CBRCase>> listCasesNotVulnerable;
	private EnumMap<INTER, Collection<CBRCase>> listCasesVulnerable;
	private Collection<CBRCase> casesNotVulnerable;
	private Collection<CBRCase> casesVulnerable;
	
	Integer numCases = 0;
	

	INTER intersecciones[] = {INTER.CRUZ,INTER.T_HOR,INTER.L_INVER,INTER.T_INVER,INTER.L_VERT};
	
	private Collection<CBRCase> casesToRemove;
	/**
	 * Closes the case base saving or deleting the cases of the persistence media
	 */
	public void close() {
		
		Collection<CBRCase> casesToStore = new ArrayList<>();
		
		
		for(INTER i:INTER.values()) {
			listCasesNotVulnerable.get(i).removeAll(casesToRemove);
			listCasesVulnerable.get(i).removeAll(casesToRemove);

			casesToStore.addAll(listCasesNotVulnerable.get(i));
			casesToStore.addAll(listCasesVulnerable.get(i));
		}
		
		casesToStore.removeAll(originalCases);
		
		connector.storeCases(casesToStore);
		connector.close();
	}

	/**
	 * Forgets cases. It only removes the cases from the storage media when closing.
	 */
	public void forgetCases(Collection<CBRCase> cases) {
		for(INTER i:INTER.values()) {
			listCasesNotVulnerable.get(i).removeAll(cases);
			listCasesVulnerable.get(i).removeAll(cases);
		}
	}

	/**
	 * Returns working cases.
	 */
	public Collection<CBRCase> getCases() {
		//nunca se deberia llamar a esto
		return originalCases;
	}
	
	public int getNumCases() {
		return numCases;
	}
	
	public Collection<CBRCase> getCases(Boolean vulnerable, INTER interseccion){
		if(vulnerable) {
			return listCasesVulnerable.get(interseccion);
		}
		else {
			return listCasesNotVulnerable.get(interseccion);			
		}
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

		listCasesNotVulnerable = new EnumMap<INTER, Collection<CBRCase>>(INTER.class);
		listCasesVulnerable = new EnumMap<INTER, Collection<CBRCase>>(INTER.class);
		
		for(INTER i:INTER.values()) {
			listCasesNotVulnerable.put(i, new ArrayList<CBRCase>());
			listCasesVulnerable.put(i, new ArrayList<CBRCase>());
		}
		
		
		//hay que leer todos los casos y clasificarlos en su lista correspondiente
		for(CBRCase caso : originalCases) {
			numCases++;
			MsPacManDescription description = (MsPacManDescription)caso.getDescription();
			//descripcion.get
			if(description.getVulnerable()) 
				listCasesVulnerable.get(intersecciones[description.getTipoInterseccion()]).add(caso);
			else {
				
				listCasesNotVulnerable.get(intersecciones[description.getTipoInterseccion()]).add(caso);				
			}
		}
		
		
		casesToRemove = new ArrayList<>();
	}

	/**
	 * Learns cases that are only saved when closing the Case Base.
	 */
	public void learnCases(Boolean vulnerable, INTER interseccion, Collection<CBRCase> cases) {
		if(vulnerable) {
			listCasesVulnerable.get(interseccion).addAll(cases);
		}
		else {
			listCasesNotVulnerable.get(interseccion).addAll(cases);			
		}
		numCases++;
	}
	
	public void learnCases(Collection<CBRCase> cases) {
		//hay que dividirlos
		
		for(CBRCase caso: cases) {
			MsPacManSolution solucion = (MsPacManSolution)caso.getSolution();
			MsPacManDescription descripcion = (MsPacManDescription)caso.getDescription();
			
			if(descripcion.getVulnerable()) {
				listCasesVulnerable.get(intersecciones[descripcion.getTipoInterseccion()]).add(caso);
			}
			else {	
				listCasesNotVulnerable.get(intersecciones[descripcion.getTipoInterseccion()]).add(caso);		
			}
		}
	}

}

