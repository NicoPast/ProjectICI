import es.ucm.fdi.ici.PacManEvaluator;
import es.ucm.fdi.ici.c2021.practica2.grupo09.Scores;

public class Evaluate {
	public static void main(String[] args) {
		PacManEvaluator evaluator = new PacManEvaluator();
		es.ucm.fdi.ici.Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();
	}
}
