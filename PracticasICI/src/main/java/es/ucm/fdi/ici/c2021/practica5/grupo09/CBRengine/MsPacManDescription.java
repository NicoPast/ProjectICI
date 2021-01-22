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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDistanciaUp() {
		return distanciaUp;
	}

	public void setDistanciaUp(Integer distanciaUp) {
		this.distanciaUp = distanciaUp;
	}

	public Integer getDistanciaRight() {
		return distanciaRight;
	}

	public void setDistanciaRight(Integer distanciaRight) {
		this.distanciaRight = distanciaRight;
	}

	public Integer getDistanciaDown() {
		return distanciaDown;
	}

	public void setDistanciaDown(Integer distanciaDown) {
		this.distanciaDown = distanciaDown;
	}

	public Integer getDistanciaLeft() {
		return distanciaLeft;
	}

	public void setDistanciaLeft(Integer distanciaLeft) {
		this.distanciaLeft = distanciaLeft;
	}

	public Integer getGhostUp() {
		return ghostUp;
	}

	public void setGhostUp(Integer ghostUp) {
		this.ghostUp = ghostUp;
	}

	public Integer getGhostRight() {
		return ghostRight;
	}

	public void setGhostRight(Integer ghostRight) {
		this.ghostRight = ghostRight;
	}

	public Integer getGhostDown() {
		return ghostDown;
	}

	public void setGhostDown(Integer ghostDown) {
		this.ghostDown = ghostDown;
	}

	public Integer getGhostLeft() {
		return ghostLeft;
	}

	public void setGhostLeft(Integer ghostLeft) {
		this.ghostLeft = ghostLeft;
	}

	public Boolean getEdibleUp() {
		return edibleUp;
	}

	public void setEdibleUp(Boolean edibleUp) {
		this.edibleUp = edibleUp;
	}

	public Boolean getEdibleRight() {
		return edibleRight;
	}

	public void setEdibleRight(Boolean edibleRight) {
		this.edibleRight = edibleRight;
	}

	public Boolean getEdibleDown() {
		return edibleDown;
	}

	public void setEdibleDown(Boolean edibleDown) {
		this.edibleDown = edibleDown;
	}

	public Boolean getEdibleLeft() {
		return edibleLeft;
	}

	public void setEdibleLeft(Boolean edibleLeft) {
		this.edibleLeft = edibleLeft;
	}

	public Boolean getVulnerable() {
		return vulnerable;
	}

	public void setVulnerable(Boolean vulnerable) {
		this.vulnerable = vulnerable;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public Integer getPillsUp() {
		return pillsUp;
	}

	public void setPillsUp(Integer pillsUp) {
		this.pillsUp = pillsUp;
	}

	public Integer getPillsRight() {
		return pillsRight;
	}

	public void setPillsRight(Integer pillsRight) {
		this.pillsRight = pillsRight;
	}

	public Integer getPillsDown() {
		return pillsDown;
	}

	public void setPillsDown(Integer pillsDown) {
		this.pillsDown = pillsDown;
	}

	public Integer getPillsLeft() {
		return pillsLeft;
	}

	public void setPillsLeft(Integer pillsLeft) {
		this.pillsLeft = pillsLeft;
	}

	public Integer getPowerPillUp() {
		return powerPillUp;
	}

	public void setPowerPillUp(Integer powerPillUp) {
		this.powerPillUp = powerPillUp;
	}

	public Integer getPowerPillRight() {
		return powerPillRight;
	}

	public void setPowerPillRight(Integer powerPillRight) {
		this.powerPillRight = powerPillRight;
	}

	public Integer getPowerPillDown() {
		return powerPillDown;
	}

	public void setPowerPillDown(Integer powerPillDown) {
		this.powerPillDown = powerPillDown;
	}

	public Integer getPowerPillLeft() {
		return powerPillLeft;
	}

	public void setPowerPillLeft(Integer powerPillLeft) {
		this.powerPillLeft = powerPillLeft;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + ", distanciaUp=" + distanciaUp + ", distanciaRight=" + distanciaRight
				+ ", distanciaDown=" + distanciaDown + ", distanciaLeft=" + distanciaLeft + ", ghostUp=" + ghostUp
				+ ", ghostRight=" + ghostRight + ", ghostDown=" + ghostDown + ", ghostLeft=" + ghostLeft + ", edibleUp="
				+ edibleUp + ", edibleRight=" + edibleRight + ", edibleDown=" + edibleDown + ", edibleLeft="
				+ edibleLeft + ", vulnerable=" + vulnerable + ", direction=" + direction + ", pillsUp=" + pillsUp
				+ ", pillsRight=" + pillsRight + ", pillsDown=" + pillsDown + ", pillsLeft=" + pillsLeft
				+ ", powerPillUp=" + powerPillUp + ", powerPillRight=" + powerPillRight + ", powerPillDown="
				+ powerPillDown + ", powerPillLeft=" + powerPillLeft + ", score=" + score + "]";
	}
	
}
