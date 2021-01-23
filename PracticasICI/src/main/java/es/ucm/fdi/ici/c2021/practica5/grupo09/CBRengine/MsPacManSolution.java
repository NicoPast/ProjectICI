package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import pacman.game.Constants.MOVE;

public class MsPacManSolution implements CaseComponent, Cloneable {
	Integer id;
	MOVE move;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public MOVE getMove() {
		return move;
	}
	public void setMove(MOVE move) {
		this.move = move;
	}
	
	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManSolution.class);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}
	@Override
	public String toString() {
		return "MsPacManSolution [id=" + id + ", action=" + action + "]";
	}  
	
	
	
}
