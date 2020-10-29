import es.ucm.fdi.ici.c2021.practica1.grupo09.PacManEvaluator;
import es.ucm.fdi.ici.c2021.practica1.grupo09.Scores;

public class Evaluate {
	public static void main(String[] args) {
		PacManEvaluator evaluator = new PacManEvaluator();
		Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();
	}
}
