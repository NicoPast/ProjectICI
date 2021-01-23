package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

public class GhostsResult implements CaseComponent, Cloneable {

	Integer id;
	Integer score;
	Integer pacmanHealth;

	Integer deltaDistanceToPacMan;

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", GhostsResult.class);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getPacmanHealth() {
		return pacmanHealth;
	}

	public void setPacmanHealth(Integer pacmanHealth) {
		this.pacmanHealth = pacmanHealth;
	}

	public Integer getDeltaDistanceToPacMan() {
		return deltaDistanceToPacMan;
	}

	public void setDeltaDistanceToPacMan(Integer deltaDistanceToPacMan) {
		this.deltaDistanceToPacMan = deltaDistanceToPacMan;
	}

	@Override
	public String toString() {
		return "GhostsResult [deltaDistanceToPacMan=" + deltaDistanceToPacMan + ", id=" + id + ", pacmanHealth="
				+ pacmanHealth + ", score=" + score + "]";
	}
}
