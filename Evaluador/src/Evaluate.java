import es.ucm.fdi.ici.PacManParallelEvaluator;
import es.ucm.fdi.ici.c2021.practica2.grupo09.Scores;

public class Evaluate {
	public static void main(String[] args) {
		PacManParallelEvaluator evaluator = new PacManParallelEvaluator();
		es.ucm.fdi.ici.Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();
	}
}
