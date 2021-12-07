package org.unibl.etf.mdp.token;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;
import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.Controller;
import org.unibl.etf.mdp.model.Mapa;
import org.unibl.etf.mdp.model.Osoba;
import org.unibl.etf.mdp.util.Util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TokenController implements Controller {
	@FXML
	private ListView<String> listView;
	@FXML
	private TextField searchField;
	@FXML
	private CheckBox nijeZarazen;
	@FXML
	private CheckBox zarazen;
	@FXML
	private CheckBox potencijalnoZarazen;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private TableView<Osoba> tableView;
	@FXML
	private Button oznaciButton;
	@FXML
	private Button blokirajButton;
	private static String selectedUsername;
	private static final String URL = "http://localhost:8080/MDP_CentralniRegistar/api/tokeni/";

	public void init(BorderPane pane) {
		pane.setCenter(anchorPane);

		ObservableList<Osoba> data = FXCollections.observableArrayList();
		FilteredList<Osoba> flToken = new FilteredList<Osoba>(data, p -> true);
		tableView.setItems(flToken);
		try {
			JSONArray array = Util.readAllJSON(URL + "active");
			for (int i = 0; i < array.length(); i++) {
				JSONObject username = array.getJSONObject(i);
				data.add(new Osoba(username.get("subject").toString()));
			}
		} catch (MalformedURLException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}

		zarazen.setDisable(true);
		nijeZarazen.setDisable(true);
		potencijalnoZarazen.setDisable(true);
		oznaciButton.setDisable(true);

		TableViewSelectionModel<Osoba> selectionModel = tableView.getSelectionModel();
		ObservableList<Osoba> selectedItems = selectionModel.getSelectedItems();

		searchField.setOnMouseClicked(action -> {
			// resetovanje selection modela
			searchField.setText(".");
			searchField.clear();
		});
		selectedItems.addListener(new ListChangeListener<Osoba>() {
			@Override
			public void onChanged(Change<? extends Osoba> change) {
				enableOptions();
				change.getList().stream().forEach(e -> {
					selectedUsername = e.getToken();
				});
				change.next();
				if (change.wasAdded()) {
					Label lab = new Label("");
					Label naslov = new Label("Mapa selektovanog korisnika");
					naslov.setFont(new Font(15));
					Mapa map = new Mapa(Main.x, Main.y, false);
					Util.getMapa(map, selectedUsername);
					GridPane gP = new GridPane();

					gP.setAlignment(Pos.CENTER);
					VBox vBox = new VBox();
					vBox.getChildren().add(naslov);
					vBox.setAlignment(Pos.CENTER);
					vBox.getChildren().add(gP);
					vBox.getChildren().add(lab);
					Mapa.setLabel(lab);
					vBox.setSpacing(10);
					for (int i = 0; i < Main.x; i++) {
						for (int j = 0; j < Main.y; j++) {
							GridPane.setRowIndex(map.getPolja()[i][j], i);
							GridPane.setColumnIndex(map.getPolja()[i][j], j);
							gP.getChildren().addAll(map.getPolja()[i][j]);
						}
					}
					gP.setPadding(new Insets(5, 5, 5, 5));
					Scene scene = new Scene(vBox, 450, 450);
					Stage newWindow = new Stage();
					newWindow.setTitle("Mapa");
					newWindow.setScene(scene);
					newWindow.show();
				}
			}
		});

		searchField.textProperty().addListener((obs, oldValue, newValue) -> {
			flToken.setPredicate(p -> p.getToken().toLowerCase().startsWith(newValue.toLowerCase().trim()));
		});

		TableColumn<Osoba, String> column1 = new TableColumn<>("Aktivni tokeni");
		column1.setCellValueFactory(new PropertyValueFactory<Osoba, String>("token"));
		tableView.getColumns().add(column1);

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tableView.getSelectionModel().setCellSelectionEnabled(true);

		oznaciButton.setOnAction(action -> {
			try {
				if (zarazen.isSelected()) {
					Platform.runLater(() -> listView.getItems().add(selectedUsername));
					oznaciToken(selectedUsername + "/zarazen");
				} else if (potencijalnoZarazen.isSelected()) {
					Platform.runLater(() -> listView.getItems().add(selectedUsername));
					oznaciToken(selectedUsername + "/potencijalno");
				} else if (nijeZarazen.isSelected()) {
					oznaciToken(selectedUsername + "/nije");
					Platform.runLater(() -> listView.getItems().remove(selectedUsername));
				}
			} catch (IOException exception) {
				Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			}
		});
		blokirajButton.setOnAction(action -> {
			try {
				oznaciToken(selectedUsername + "/block");
			} catch (IOException exception) {
				Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			}
		});
		zarazen.setOnAction(action -> {
			if (zarazen.isSelected()) {
				nijeZarazen.setDisable(true);
				potencijalnoZarazen.setDisable(true);
			} else {
				nijeZarazen.setDisable(false);
				potencijalnoZarazen.setDisable(false);
			}
		});

		nijeZarazen.setOnAction(action -> {
			if (nijeZarazen.isSelected()) {
				zarazen.setDisable(true);
				potencijalnoZarazen.setDisable(true);
			} else {
				zarazen.setDisable(false);
				potencijalnoZarazen.setDisable(false);
			}
		});

		potencijalnoZarazen.setOnAction(action -> {
			if (potencijalnoZarazen.isSelected()) {
				zarazen.setDisable(true);
				nijeZarazen.setDisable(true);
			} else {
				zarazen.setDisable(false);
				nijeZarazen.setDisable(false);
			}
		});
		Util.checkStatus(listView);
	}

	private void oznaciToken(String sub) throws IOException {
		URL url = new URL(URL + sub);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Content-Type", "application/json");
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			Util.showAlert("Neuspjesno oznacen", false);
		} else {
			Util.showAlert("Uspjesno oznacen", false);
		}
		conn.disconnect();
	}

	private void enableOptions() {
		zarazen.setDisable(false);
		nijeZarazen.setDisable(false);
		potencijalnoZarazen.setDisable(false);
		oznaciButton.setDisable(false);
	}
}