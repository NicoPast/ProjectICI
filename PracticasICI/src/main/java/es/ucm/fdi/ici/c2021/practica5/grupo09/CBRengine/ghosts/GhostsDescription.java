package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.pacman.MsPacManDescription;
import pacman.game.Constants.MOVE;

public class GhostsDescription implements CaseComponent {

	Integer id;
	
	//Inteseccion
		//Up
		Integer distanceNextIntersectionUp;
		Integer distanceNearestGhostUp;
		Boolean GhostEdibleUp;
		
		//Down
		Integer distanceNextIntersectionDown;
		Integer distanceNearestGhostDown;
		Boolean GhostEdibleDown;

		//Left
		Integer distanceNextIntersectionLeft;
		Integer distanceNearestGhostLeft;
		Boolean GhostEdibleLeft;

		//Right
		Integer distanceNextIntersectionRight;
		Integer distanceNearestGhostRight;
		Boolean GhostEdibleRight;

	Boolean edible;
	Integer lastMove;

	Double distanceToPacMan;
	

	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getDistanceNextIntersectionUp() {
		return distanceNextIntersectionUp;
	}


	public void setDistanceNextIntersectionUp(Integer distanceNextIntersectionUp) {
		this.distanceNextIntersectionUp = distanceNextIntersectionUp;
	}


	public Integer getDistanceNearestGhostUp() {
		return distanceNearestGhostUp;
	}


	public void setDistanceNearestGhostUp(Integer distanceNearestGhostUp) {
		this.distanceNearestGhostUp = distanceNearestGhostUp;
	}


	public Boolean getGhostEdibleUp() {
		return GhostEdibleUp;
	}


	public void setGhostEdibleUp(Boolean ghostEdibleUp) {
		GhostEdibleUp = ghostEdibleUp;
	}


	public Integer getDistanceNextIntersectionDown() {
		return distanceNextIntersectionDown;
	}


	public void setDistanceNextIntersectionDown(Integer distanceNextIntersectionDown) {
		this.distanceNextIntersectionDown = distanceNextIntersectionDown;
	}


	public Integer getDistanceNearestGhostDown() {
		return distanceNearestGhostDown;
	}


	public void setDistanceNearestGhostDown(Integer distanceNearestGhostDown) {
		this.distanceNearestGhostDown = distanceNearestGhostDown;
	}


	public Boolean getGhostEdibleDown() {
		return GhostEdibleDown;
	}


	public void setGhostEdibleDown(Boolean ghostEdibleDown) {
		GhostEdibleDown = ghostEdibleDown;
	}


	public Integer getDistanceNextIntersectionLeft() {
		return distanceNextIntersectionLeft;
	}


	public void setDistanceNextIntersectionLeft(Integer distanceNextIntersectionLeft) {
		this.distanceNextIntersectionLeft = distanceNextIntersectionLeft;
	}


	public Integer getDistanceNearestGhostLeft() {
		return distanceNearestGhostLeft;
	}


	public void setDistanceNearestGhostLeft(Integer distanceNearestGhostLeft) {
		this.distanceNearestGhostLeft = distanceNearestGhostLeft;
	}


	public Boolean getGhostEdibleLeft() {
		return GhostEdibleLeft;
	}


	public void setGhostEdibleLeft(Boolean ghostEdibleLeft) {
		GhostEdibleLeft = ghostEdibleLeft;
	}


	public Integer getDistanceNextIntersectionRight() {
		return distanceNextIntersectionRight;
	}


	public void setDistanceNextIntersectionRight(Integer distanceNextIntersectionRight) {
		this.distanceNextIntersectionRight = distanceNextIntersectionRight;
	}


	public Integer getDistanceNearestGhostRight() {
		return distanceNearestGhostRight;
	}


	public void setDistanceNearestGhostRight(Integer distanceNearestGhostRight) {
		this.distanceNearestGhostRight = distanceNearestGhostRight;
	}


	public Boolean getGhostEdibleRight() {
		return GhostEdibleRight;
	}


	public void setGhostEdibleRight(Boolean ghostEdibleRight) {
		GhostEdibleRight = ghostEdibleRight;
	}


	public Boolean getEdible() {
		return edible;
	}


	public void setEdible(Boolean edible) {
		this.edible = edible;
	}


	public Integer getLastMove() {
		return lastMove;
	}


	public void setLastMove(Integer lastMove) {
		this.lastMove = lastMove;
	}


	public Double getDistanceToPacMan() {
		return distanceToPacMan;
	}


	public void setDistanceToPacMan(Double distanceToPacMan) {
		this.distanceToPacMan = distanceToPacMan;
	}


	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}	
	
	@Override
	public String toString() {
		return "GhostsDescription [id=" + id + ", distanceNextIntersectionUp=" + distanceNextIntersectionUp
				+ ", distanceNearestGhostUp=" + distanceNearestGhostUp + ", GhostEdibleUp=" + GhostEdibleUp
				+ ", distanceNextIntersectionDown=" + distanceNextIntersectionDown + ", distanceNearestGhostDown="
				+ distanceNearestGhostDown + ", GhostEdibleDown=" + GhostEdibleDown
				+ ", distanceNextIntersectionLeft=" + distanceNextIntersectionLeft + ", distanceNearestGhostLeft="
				+ distanceNearestGhostLeft + ", GhostEdibleLeft=" + GhostEdibleLeft
				+ ", distanceNextIntersectionRight=" + distanceNextIntersectionRight
				+ ", distanceNearestGhostRight=" + distanceNearestGhostRight + ", GhostEdibleRight="
				+ GhostEdibleRight + ", edible=" + edible + ", lastMove=" + lastMove + ", distanceToPacMan="
				+ distanceToPacMan + "]";
	}


}
