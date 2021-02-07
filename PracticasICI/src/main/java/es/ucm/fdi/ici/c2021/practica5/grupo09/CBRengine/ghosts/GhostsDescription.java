package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.ghosts;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine.pacman.MsPacManDescription;
import pacman.game.Constants.MOVE;

public class GhostsDescription implements CaseComponent {

	Integer id;
	
	//Inteseccion
		Integer intersectionType;
		//Up
		Integer distanceNextIntersectionUp;
		Boolean GhostEdibleUp;
		
		//Down
		Integer distanceNextIntersectionDown;
		Boolean GhostEdibleDown;

		//Left
		Integer distanceNextIntersectionLeft;
		Boolean GhostEdibleLeft;

		//Right
		Integer distanceNextIntersectionRight;
		Boolean GhostEdibleRight;

	Boolean edible;
	Integer lastMove;

	Double distanceToPacMan;
	
	//Relacionadas con hacer el resultado
	Integer score;
	Integer pacmanLife;
	
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getPacmanLife() {
		return pacmanLife;
	}

	public void setPacmanLife(Integer pacmanLife) {
		this.pacmanLife = pacmanLife;
	}

	public Integer getIntersectionType() {
		return intersectionType;
	}

	public void setIntersectionType(Integer intersectionType) {
		this.intersectionType = intersectionType;
	}
	
	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", GhostsDescription.class);
	}


	@Override
	public String toString() {
		return "GhostsDescription [id=" + id + ", intersectionType=" + intersectionType
				+ ", distanceNextIntersectionUp=" + distanceNextIntersectionUp + ", GhostEdibleUp=" + GhostEdibleUp
				+ ", distanceNextIntersectionDown=" + distanceNextIntersectionDown + ", GhostEdibleDown="
				+ GhostEdibleDown + ", distanceNextIntersectionLeft=" + distanceNextIntersectionLeft
				+ ", GhostEdibleLeft=" + GhostEdibleLeft + ", distanceNextIntersectionRight="
				+ distanceNextIntersectionRight + ", GhostEdibleRight=" + GhostEdibleRight + ", edible=" + edible
				+ ", lastMove=" + lastMove + ", distanceToPacMan=" + distanceToPacMan + ", score=" + score
				+ ", pacmanLife=" + pacmanLife + "]";
	}

	

	
}
