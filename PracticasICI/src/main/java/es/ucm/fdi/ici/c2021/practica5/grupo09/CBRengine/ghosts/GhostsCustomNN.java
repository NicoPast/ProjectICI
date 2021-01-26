package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;

public class GhostsCustomNN {
	public static Collection<RetrievalResult> customNN(Collection<CBRCase> cases, CBRQuery query) {
		// Parallel stream
	    List<RetrievalResult> res = cases.parallelStream()
	                .map(c -> new RetrievalResult(c, computeSimilarity(query.getDescription(), c.getDescription())))
	                .collect(Collectors.toList());
	    // Sort the result
	        res.sort(RetrievalResult::compareTo);
	        return res;
	}

	private static Double computeSimilarity(CaseComponent description, CaseComponent description2) {
		GhostsDescription _query = (GhostsDescription)description;
		GhostsDescription _case = (GhostsDescription)description2;
		double simil = 0;
		simil += Math.abs(_query.getDistanceNextIntersectionUp()-_case.getDistanceNextIntersectionUp())/40;
		simil += Math.abs(_query.getDistanceNextIntersectionDown()-_case.getDistanceNextIntersectionDown())/40;
		simil += Math.abs(_query.getDistanceNextIntersectionLeft()-_case.getDistanceNextIntersectionLeft())/40;
		simil += Math.abs(_query.getDistanceNextIntersectionRight()-_case.getDistanceNextIntersectionRight())/40;
		simil += 0.5 * Math.abs(_query.getDistanceNearestGhostUp()-_case.getDistanceNearestGhostUp())/150;
		simil += 0.5 * Math.abs(_query.getDistanceNearestGhostDown()-_case.getDistanceNearestGhostDown())/150;
		simil += 0.5 * Math.abs(_query.getDistanceNearestGhostLeft()-_case.getDistanceNearestGhostLeft())/150;
		simil += 0.5 * Math.abs(_query.getDistanceNearestGhostRight()-_case.getDistanceNearestGhostRight())/150;
		simil += _query.getGhostEdibleUp().equals(_case.getGhostEdibleUp()) ? 2.0 : 0.0;
		simil += _query.getGhostEdibleDown().equals(_case.getGhostEdibleDown()) ? 2.0 : 0.0;
		simil += _query.getGhostEdibleLeft().equals(_case.getGhostEdibleLeft()) ? 2.0 : 0.0;
		simil += _query.getGhostEdibleRight().equals(_case.getGhostEdibleRight()) ? 2.0 : 0.0;
		simil += _query.getLastMove().equals(_case.getLastMove()) ? 10.0 : 0.0;
		simil += 3 * Math.abs(_query.getDistanceToPacMan()-_case.getDistanceToPacMan())/300;

		return simil/27.0;
	}
}