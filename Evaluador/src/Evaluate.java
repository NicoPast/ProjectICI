import es.ucm.fdi.ici.PacManParallelEvaluator;
import es.ucm.fdi.ici.Scores;

public class Evaluate {
	public static void main(String[] args) {
		/*PacManEvaluator evaluator = new PacManEvaluator();
		es.ucm.fdi.ici.Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();*/
		
		PacManParallelEvaluator evaluator = new PacManParallelEvaluator();
		Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();
	}
}
