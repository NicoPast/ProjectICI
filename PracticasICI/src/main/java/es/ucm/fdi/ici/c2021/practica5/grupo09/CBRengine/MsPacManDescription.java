package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

public class MsPacManDescription implements CaseComponent {

	Integer id;

	Integer distanciaUp;
	Integer distanciaRight;
	Integer distanciaDown;
	Integer distanciaLeft;

	Integer ghostUp;
	Integer ghostRight;
	Integer ghostDown;
	Integer ghostLeft;

	Boolean edibleUp;
	Boolean edibleRight;
	Boolean edibleDown;
	Boolean edibleLeft;
	
	Boolean vulnerable;
	
	Integer direction;

	Integer pillsUp;
	Integer pillsRight;
	Integer pillsDown;
	Integer pillsLeft;

	Integer powerPillUp;
	Integer powerPillRight;
	Integer powerPillDown;
	Integer powerPillLeft;
	
	
	
	
	Integer score;
	Integer time;
	Integer nearestPPill;
	Integer nearestGhost;
	Boolean edibleGhost;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getNearestPPill() {
		return nearestPPill;
	}

	public void setNearestPPill(Integer nearestPPill) {
		this.nearestPPill = nearestPPill;
	}

	public Integer getNearestGhost() {
		return nearestGhost;
	}

	public void setNearestGhost(Integer nearestGhost) {
		this.nearestGhost = nearestGhost;
	}

	public Boolean getEdibleGhost() {
		return edibleGhost;
	}

	public void setEdibleGhost(Boolean edibleGhost) {
		this.edibleGhost = edibleGhost;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + ", score=" + score + ", time=" + time + ", nearestPPill="
				+ nearestPPill + ", nearestGhost=" + nearestGhost + ", edibleGhost=" + edibleGhost + "]";
	}
	
	

}