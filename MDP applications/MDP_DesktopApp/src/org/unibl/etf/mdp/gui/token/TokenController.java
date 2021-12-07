package org.unibl.etf.mdp.gui.token;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import org.unibl.etf.mdp.model.DataModel;
import org.unibl.etf.mdp.util.Controller;
import org.unibl.etf.mdp.util.Util;

public class TokenController extends Controller {
	@FXML
	private PasswordField password;
	@FXML
	private Button prijavaBTN;

	public void initModel(DataModel model) {
		prijavaBTN.setOnAction(event -> {
			String passText;
			passText = password.getText();
			if (Util.checkIsInputEmpty(passText)) {
				Util.showAlert("Password is empty!", true);
			} else {
				model.getUser().setPassword(passText);
				Util.initWindow(Util.getStage(), "DesktopApp", Util.getDesktopAppResource(), 1050, 605);
			}
		});
	}
}