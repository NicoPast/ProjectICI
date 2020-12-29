package es.ucm.fdi.ici.c2021.practica4.grupo09.ghosts;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost;
import es.ucm.fdi.ici.c2021.practica4.grupo09.MapaInfoGhost.interseccion;
import es.ucm.fdi.ici.fuzzy.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput implements Input {

	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String,Double> vars = new HashMap<String,Double>();

		return vars;
	}

	private GHOST ghost;
	public class UsefulData{
		public Vector<interseccion> GhostsPositions;
		public Vector<Double>GhostsPositionsAccuracy;
		public Vector<MOVE>GhostsLastMoveMade;
		
		public Vector<Double>GhostIsEdibleAccuracy;

		public interseccion proximaInterseccionPacMan;
		public double proximaInterseccionPacManAccuracy;
		public MOVE PacmanLastMoveMade;
		
		UsefulData(Vector<interseccion>Ghosts,Vector<Double>GhostFuzzy,Vector<Double>EdibleAccuracy,interseccion Pacman, double PacmanAccuracy,
				Vector<MOVE>GhostsMoves, MOVE PacmanMove)
		{
			GhostsPositions=Ghosts;
			GhostsPositionsAccuracy=GhostFuzzy;
			GhostIsEdibleAccuracy=EdibleAccuracy;
			proximaInterseccionPacMan=Pacman;
			proximaInterseccionPacManAccuracy=PacmanAccuracy;
			GhostsLastMoveMade=GhostsMoves;
			PacmanLastMoveMade=PacmanMove;
		}
	}
	
	public UsefulData getData(){return new UsefulData(this.GhostsPositions,this.GhostsPositionsAccuracy,this.GhostIsEdibleAccuracy,this.proximaInterseccionPacMan,
			this.proximaInterseccionPacManAccuracy,this.GhostsLastMoveMade,this.PacmanLastMoveMade);}
	
	
	private MapaInfoGhost mapa;

	
	private Vector<interseccion> GhostsPositions;
	private Vector<Double>GhostsPositionsAccuracy;
	private Vector<MOVE>GhostsLastMoveMade;
	
	
	private Vector<Double>GhostIsEdibleAccuracy;

	private interseccion proximaInterseccionPacMan;
	private double proximaInterseccionPacManAccuracy;
	private MOVE PacmanLastMoveMade;
	
	
	
	public void setGhost(GHOST g) {ghost=g;}
	public GhostsInput(MapaInfoGhost mapaInfo) {
		this.mapa = mapaInfo;
	}

	@Override
	public void parseInput(Game game) {
		
		mapa.update(game);
		for	(int i=0;i<4;i++)
		{
			interseccion aux=new interseccion(-1,)
			if(game.getGhostCurrentNodeIndex(GHOST.values()[i])>-1)
			this.mapa.getInterseccion()
			this.GhostsPositions.elementAt(i) = 
		}
			
		
		
	}

	

}
