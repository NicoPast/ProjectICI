package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts;

import java.util.EnumMap;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PositionAproximator {

    MapaInfoGhost mapa;
    Game game;

    int myPos;
    MOVE myLastMove;

    double CONSTANT_MULTIPLIER = 50;
    DM MOVE_CONSTANT = DM.EUCLID;

    EnumMap<MOVE, Integer> movimientos;

    public PositionAproximator(MapaInfoGhost mapa, Game game, int myPos, MOVE myLastMove, interseccion entityPos, MOVE entityLastMoveToPos, double fuzzyAccuracy){
        this.mapa = mapa;
        this.game = game;
        this.myPos = myPos;
        this.myLastMove = myLastMove;

        double distance = (fuzzyAccuracy <= 0) ? CONSTANT_MULTIPLIER : CONSTANT_MULTIPLIER * (1 - fuzzyAccuracy);

        movimientos = new EnumMap<>(MOVE.class);
        if(distance > 0)
            recursion(distance, entityPos, entityLastMoveToPos);
    }

    public MOVE getBestMoveTowardsEntityPos(){
        MOVE best = MOVE.NEUTRAL;
        int most = -1;
        for (MOVE m : movimientos.keySet()) {
            if(movimientos.get(m) > most){
                most = movimientos.get(m);
                best = m;
            }
        }
        return best;
    }

    private void recursion(double distanceLeft, interseccion actualPos, MOVE movellegada){
        for(MOVE m : actualPos.distancias.keySet()){
            int distanciaHaciaInterseccion = actualPos.distancias.get(m);
            int destino = actualPos.destinos.get(m);
            if(distanciaHaciaInterseccion >= distanceLeft){ 
                int finalista = destino;
                MOVE movimiento = game.getApproximateNextMoveTowardsTarget(myPos, finalista, myLastMove, MOVE_CONSTANT);
                movimientos.put(movimiento, movimientos.get(movimiento) + 1);
            }
            else {
                recursion(distanceLeft - distanciaHaciaInterseccion, 
                        mapa.getInterseccion(destino), 
                        getMoveLlegada(mapa.getInterseccion(destino), actualPos.identificador, distanciaHaciaInterseccion));
            }
        }
    }

    private MOVE getMoveLlegada(interseccion destino, int inicio, double distance){  
        for(MOVE m : destino.distancias.keySet()){
            if(destino.distancias.get(m) == distance && destino.destinos.get(m) == inicio)
                return m.opposite();
        }
        return null;
    }
}
