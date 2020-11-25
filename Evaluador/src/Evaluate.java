import es.ucm.fdi.ici.PacManParallelEvaluator;

public class Evaluate {
	public static void main(String[] args) {
		PacManParallelEvaluator evaluator = new PacManParallelEvaluator();
		es.ucm.fdi.ici.Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();
	}
}
