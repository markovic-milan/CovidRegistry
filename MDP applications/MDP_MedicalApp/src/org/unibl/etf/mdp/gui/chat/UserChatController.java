package org.unibl.etf.mdp.gui.chat;

import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class UserChatController implements Controller {
	@FXML
	private TextArea userChatArea;
	@FXML
	private TextField userTextField;
	@FXML
	private AnchorPane userAnchor;
	@FXML
	private Button endButton;

	@Override
	public void init(BorderPane pane) {
		ReadMes.setUserArea(userChatArea);
		pane.setRight(userAnchor);
		userChatArea.setEditable(false);
		userTextField.setOnAction(action -> {
			userChatArea.appendText(userTextField.getText() + "\n");
			synchronized (SendMes.lock) {
				SendMes.set(userTextField, Protocol.USR.getMessage());
				SendMes.lock.notify();
			}
		});
		endButton.setOnAction(action -> {
			synchronized (SendMes.lock) {
				SendMes.set(userTextField, Protocol.INFO.getMessage());
				SendMes.lock.notify();
			}
			endButton.getScene().getWindow().hide();
			Main.isOpen=false;
		});
		pane.getScene().getWindow().setOnCloseRequest(action -> {
			endButton.fire();
			Main.isOpen=false;
		});
	}
}