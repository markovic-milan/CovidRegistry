package org.unibl.etf.mdp.gui.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import org.unibl.etf.mdp.util.Controller;
import org.unibl.etf.mdp.util.Util;
import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.DataModel;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.soap.server.Token;
import org.unibl.etf.mdp.soap.server.TokenServer;
import org.unibl.etf.mdp.soap.server.TokenServerServiceLocator;

import java.rmi.RemoteException;
import java.util.logging.Level;

import javax.xml.rpc.ServiceException;

public class Login extends Controller {
	@FXML
	private Button prijavaBTN;
	@FXML
	private TextField imeTF;
	@FXML
	private TextField prezimeTF;
	@FXML
	private TextField jmbTF;
	@FXML
	private TextField usernameTF;

	public void initModel(DataModel model) {
		super.initModel(model);
		Platform.runLater(() -> prijavaBTN.requestFocus());
		prijavaBTN.setOnAction((action) -> {
			Token token = null;
			String ime, prezime, jmb,username;
			ime = imeTF.getText();
			prezime = prezimeTF.getText();
			jmb = jmbTF.getText();
			username = usernameTF.getText();
			if (Util.checkIsInputEmpty(ime, prezime, jmb)) {
				Util.showAlert("Unesite podatke u sva polja!", true);
			} else {
				model.setUser(new User(ime, prezime, jmb));
				TokenServerServiceLocator locator = new TokenServerServiceLocator();
				try {
					TokenServer ser = locator.getTokenServer();
					token = ser.getToken(ime + ":" + prezime + ":" + jmb + ":" + username);
					if (token == null) {
						Util.showAlert("Ne postoji korisnik sa datim kredencijalima", false);
					} else {
						model.getUser().setToken(token);
						model.getUser().setUsername(username);
						Util.initWindow(Util.getStage(), "TokenLogin", Util.getTokenLoginResource(), 350, 350);
					}
				} catch (ServiceException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				} catch (RemoteException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				}
			}
		});
	}
}