package org.unibl.etf.mdp.model;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Mapa {
	private Polje[][] polja;
	private static TextField xF, yF;
	private static boolean isSelectedOne = false;
	private static Label pozicijaIVrijeme;

	public static void setLabel(Label l) {
		pozicijaIVrijeme = l;
	}

	public static Label getLabel() {
		return pozicijaIVrijeme;
	}

	public static void select() {
		if (isSelectedOne) {
			isSelectedOne = false;
		} else
			isSelectedOne = true;
	}

	public static boolean isSelected() {
		return isSelectedOne;
	}

	public Mapa(int height, int width, boolean editable) {
		polja = new Polje[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				polja[i][j] = new Polje(i, j, editable);
			}
	}

	public Mapa(int height, int width, TextField xF, TextField yF, boolean editable) {
		Mapa.xF = xF;
		Mapa.yF = yF;
		polja = new Polje[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				polja[i][j] = new Polje(i, j, editable);
			}
	}

	public Polje[][] getPolja() {
		return polja;
	}

	protected static void setText(String x, String y) {
		xF.setText(x);
		yF.setText(y);
	}

	public void parseMap(String positions) {
		String[] pos = positions.split("\\|");
		for (int i = 0; i < pos.length; i++) {
			polja[Integer.parseInt(pos[i].split(",")[0])][Integer.parseInt(pos[i].split(",")[1])]
					.oboji(pos[i].split(",")[2], pos[i].split(",")[3]);
		}
	}
}