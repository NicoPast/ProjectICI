package es.ucm.fdi.ici.c2021.practica2.grupo09;

import pacman.game.Constants.GHOST;

public class auxiliarClasses {

	public class GHOSTANDDISTANCE {
		public GHOST ghost;
		public double distance;
		
		public GHOSTANDDISTANCE(GHOST go, double di) {
			ghost=go;
			distance=di;	
		}
	}

	public class NODEANDDISTANCE {
		public int n;
		public double d;

		public NODEANDDISTANCE(int no, double di) {
			n = no;
			d = di;
		}
	}
}
