package org.unibl.etf.mdp.gui.chat;

import org.unibl.etf.mdp.model.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ChatController implements Controller {
	@FXML
	private TextArea chatArea;
	@FXML
	private TextField messageField;
	@FXML
	private AnchorPane anchorPane;

	@Override
	public void init(BorderPane pane) {
		ReadMes.setGroupArea(chatArea);
		pane.setLeft(anchorPane);
		chatArea.setEditable(false);
		messageField.setOnAction(action -> {
			chatArea.appendText(messageField.getText() + "\n");
			synchronized (SendMes.lock) {
				SendMes.set(messageField, Protocol.MED.getMessage());
				SendMes.lock.notify();
			}
		});
	}
}