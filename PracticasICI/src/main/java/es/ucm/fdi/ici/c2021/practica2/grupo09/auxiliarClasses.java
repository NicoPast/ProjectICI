package es.ucm.fdi.ici.c2021.practica2.grupo09;

import pacman.game.Constants.GHOST;

public class auxiliarClasses {

	public class GHOSTANDDISTANCE {
		public GHOST g;
		public double d;
		public GHOSTANDDISTANCE(GHOST go, double di) {
			g=go;
			d=di;	
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
