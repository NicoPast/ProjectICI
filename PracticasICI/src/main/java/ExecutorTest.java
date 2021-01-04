import es.ucm.fdi.ici.practica4.demofuzzy.MsPacManFuzzy;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;
import pacman.game.internal.POType;

public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setGhostPO(true)
                .setPacmanPO(true)
                .setVisual(true)
                .setScaleFactor(3.0)
                .setPacmanPOvisual(true)
                .build();

        PacmanController pacMan = new MsPacManFuzzy();
        GhostController ghosts = new GhostsRandom();
        
        System.out.println( 
        		executor.runGame(pacMan, ghosts, 40)
        );
        
    }
}
