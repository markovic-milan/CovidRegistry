package org.unibl.etf.mdp.model;

import org.unibl.etf.mdp.util.Util;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Polje extends StackPane {
	private int positionX, positionY;
	private boolean clicked = false;
	private String vrijemeOd, vrijemeDo;
	private boolean flaged = false;

	public Polje(int x, int y, boolean editable) {
		positionX = x;
		positionY = y;
		setPrefWidth(25);
		setPrefHeight(25);
		setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		if (editable) {
			setOnMouseClicked(action -> {
				if (clicked) {
					Mapa.setText("", "");
					Mapa.select();
					setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
					clicked = false;
				} else {
					if (Mapa.isSelected()) {
						Util.showAlert("Prvo deselektuj prethodnu poziciju", false);
					} else {
						Mapa.setText(x + "", y + "");
						Mapa.select();
						setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
						clicked = true;
					}
				}
			});
		} else {
			setOnMouseClicked(action -> {
				if (flaged) {
					String datumOd = vrijemeOd.split("\\*")[0];
					String vrOd = vrijemeOd.split("\\*")[1];
					String datumDo = vrijemeDo.split("\\*")[0];
					String vrDo = vrijemeDo.split("\\*")[1];
					Mapa.getLabel().setText("Lociran na poziciji [" + x + "][" + y + "] u periodu\nOd " + datumOd + " " + vrOd
							+ "		do " + datumDo + " " + vrDo);
					Mapa.getLabel().setFont(new Font(14));
				}
			});
		}
	}

	public int getX() {
		return positionX;
	}

	public int getY() {
		return positionY;
	}

	public void oboji(String vrOd, String vrDo) {
		vrijemeOd = vrOd;
		vrijemeDo = vrDo;
		setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		this.flaged = true;
	}
}
