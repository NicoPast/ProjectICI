package es.ucm.fdi.ici.c2021.practica5.grupo09.CBRengine;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import pacman.game.Constants.MOVE;

public class MsPacManDescription implements CaseComponent {

	Integer id;

	Integer distClosestEdibleGhost;
	Integer distClosestGhost;
	Integer distToPowerPill;
	
	Boolean vulnerable;
		
	Integer score;
	Integer tipoInterseccion;

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

	public Integer getTipoInterseccion() {
		return tipoInterseccion;
	}

	public void setTipoInterseccion(Integer tipoInterseccion) {
		this.tipoInterseccion = tipoInterseccion;
	}
	
	

	public Integer getDistClosestEdibleGhost() {
		return distClosestEdibleGhost;
	}

	public void setDistClosestEdibleGhost(Integer distClosestEdibleGhost) {
		this.distClosestEdibleGhost = distClosestEdibleGhost;
	}

	public Integer getDistClosestGhost() {
		return distClosestGhost;
	}

	public void setDistClosestGhost(Integer distClosestGhost) {
		this.distClosestGhost = distClosestGhost;
	}

	public Integer getDistToPowerPill() {
		return distToPowerPill;
	}

	public void setDistToPowerPill(Integer distToPowerPill) {
		this.distToPowerPill = distToPowerPill;
	}

	public Boolean getVulnerable() {
		return vulnerable;
	}

	public void setVulnerable(Boolean vulnerable) {
		this.vulnerable = vulnerable;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + ", distClosestEdibleGhost=" + distClosestEdibleGhost
				+ ", distClosestGhost=" + distClosestGhost + ", distToPowerPill=" + distToPowerPill + ", vulnerable="
				+ vulnerable + ", score=" + score + ", tipoInterseccion=" + tipoInterseccion + "]";
	}

	
}
