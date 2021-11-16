package org.unibl.etf.mdp.gui.app;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONArray;
import org.unibl.etf.mdp.gui.chat.SendMes;
import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.DataModel;
import org.unibl.etf.mdp.model.LoginTimes;
import org.unibl.etf.mdp.model.Mapa;
import org.unibl.etf.mdp.rmi.FajlInterface;
import org.unibl.etf.mdp.rmi.FajlUtil;
import org.unibl.etf.mdp.util.Controller;
import org.unibl.etf.mdp.util.Util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DesktopApp extends Controller {
	private boolean open = false;
	@FXML
	private Label informacijeLabel;
	@FXML
	private MenuBar menuBar;
	@FXML
	private BorderPane borderPane;
	@FXML
	private Button sendButton;
	@FXML
	private Button detaljiButton;
	@FXML
	private Button slanjeFajlovaButton;
	@FXML
	private TextField xTF;
	@FXML
	private TextField yTF;
	@FXML
	private TextField odHH;
	@FXML
	private TextField odMIN;
	@FXML
	private TextField doHH;
	@FXML
	private TextField doMIN;
	@FXML
	private DatePicker odDate;
	@FXML
	private DatePicker doDate;
	@FXML
	private static GridPane gridPane = new GridPane();
	@FXML
	private VBox vBox;
	private Mapa mapa;
	@FXML
	private AnchorPane anchorPane;
	private static final String PATH = "resources";
	private static final String MED_FILES = "medicinskiFajlovi";
	private FajlInterface fileServer;

	private void popuniMapu(int height, int width) {
		mapa = new Mapa(height, width, xTF, yTF, true);
		vBox.getChildren().add(gridPane);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				GridPane.setRowIndex(mapa.getPolja()[i][j], i);
				GridPane.setColumnIndex(mapa.getPolja()[i][j], j);
				gridPane.getChildren().addAll(mapa.getPolja()[i][j]);
			}
		}
		gridPane.setPadding(new Insets(5, 5, 5, 5));
	}

	public void initModel(DataModel model) {
		Main.vrijemePrijave = System.currentTimeMillis();
		super.initModel(model);

		// RMI DIO
		System.setProperty("java.security.policy", PATH + File.separator + "client_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String name = "FileServer";
			Registry registry = LocateRegistry.getRegistry(1099);
			fileServer = (FajlInterface) registry.lookup(name);

			sendButton.setOnAction(action -> {
				if (Util.checkIsInputEmpty(odMIN.getText(), odHH.getText(), doMIN.getText(), doHH.getText(),
						xTF.getText(), yTF.getText())) {
					Util.showAlert("Unesite sve podatke", true);
				} else {
					Util.sendMap(xTF.getText(), yTF.getText(), odHH.getText(), odMIN.getText(), doHH.getText(),
							doMIN.getText(), odDate.getValue(), doDate.getValue());
				}
			});
			FileChooser chooser = new FileChooser();
			chooser.setInitialDirectory(new File("medicinskiFajlovi"));
			slanjeFajlovaButton.setOnMouseClicked(action -> {
				List<File> oznaceni = chooser.showOpenMultipleDialog(new Stage());
				try {
					if (oznaceni == null) {
						return;
					}
					if (oznaceni.size() <= 4) {
						for (File file : oznaceni) {
							String fileName = file.getName();
							if (Files.size(Paths.get(file.getAbsolutePath())) > 1000000) {
								FajlUtil.zipFiles(MED_FILES + File.separator + fileName);
								byte[] fileAsBytes = FajlUtil.fileToByte(MED_FILES + File.separator + "compressed.zip");
								fileServer.upload(model.getUser().getToken(), "compressed.zip", fileAsBytes);
							} else {
								byte[] fileAsBytes = FajlUtil.fileToByte(MED_FILES + File.separator + fileName);
								fileServer.upload(model.getUser().getToken(), fileName, fileAsBytes);
							}
						}
					} else {
						Util.showAlert("Selektujte manje od 5 fajlova", false);
					}
				} catch (RemoteException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				} catch (IOException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				}
			});

			menuBar.setFocusTraversable(true);
			Util.initMainDesktopAppWindow(borderPane);

			popuniMapu(Main.x, Main.y);
			initMenuBar(Util.getStage());
			detaljiButton.setOnAction(action -> {
				Util.getMapaDetalji(Main.x, Main.y);
			});
			Util.checkStatus(Main.x, Main.y, informacijeLabel, detaljiButton);
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (NotBoundException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	@SuppressWarnings("unchecked")
	public void initMenuBar(Stage primaryStage) {
		ObservableList<LoginTimes> times = FXCollections.observableArrayList();
		new Thread(() -> {
			try {
				JSONArray array = Util.readAllJSON(Main.model.getUser().getUsername() + "/time");
				for (int i = 0; i < array.length(); i++) {
					times.add(new LoginTimes(array.get(i).toString().split(":")[0],
							array.get(i).toString().split(":")[1]));
				}
			} catch (MalformedURLException exception) {
				Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			} catch (IOException exception) {
				Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			}
		}).start();

		Label labelZatvori = new Label("Zatvori");
		labelZatvori.setOnMouseClicked(action -> {
			Main.closeApp = true;
			if (Main.model.getUser().getToken() != null)
				Util.sendTime();
			synchronized (SendMes.lock) {
				SendMes.lock.notify();
			}
			primaryStage.hide();
		});
		Menu menuZatvori = new Menu("", labelZatvori);
		menuBar.getMenus().add(menuZatvori);

		Label labelOdjava = new Label("Odjava");
		labelOdjava.setOnMouseClicked(action -> {
			Util.odjavaIzRegistra();
		});
		Menu menuOdjava = new Menu("", labelOdjava);
		menuBar.getMenus().add(menuOdjava);

		Label labelPregled = new Label("Pregled");
		labelPregled.setOnMouseClicked(action -> {
			if (!Util.blocked) {
				VBox vBox = new VBox();
				TableView<LoginTimes> tv = new TableView<LoginTimes>();
				tv.setItems(times);
				TableColumn<LoginTimes, String> column1 = new TableColumn<>("Prijava");
				column1.setCellValueFactory(new PropertyValueFactory<LoginTimes, String>("prijava"));
				column1.setMaxWidth(200);
				TableColumn<LoginTimes, String> column2 = new TableColumn<>("Odjava");
				column2.setCellValueFactory(new PropertyValueFactory<LoginTimes, String>("odjava"));
				column2.setMaxWidth(200);
				TableColumn<LoginTimes, Integer> column3 = new TableColumn<>("Ukupno [s]");
				column3.setCellValueFactory(new PropertyValueFactory<LoginTimes, Integer>("razlika"));
				column3.setMaxWidth(100);
				tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

				tv.getColumns().addAll(column1, column2, column3);
				tv.setEditable(false);
				vBox.setAlignment(Pos.CENTER);
				vBox.getChildren().add(tv);

				vBox.setSpacing(20);
				Scene scene = new Scene(vBox, 500, 400);
				Stage newWindow = new Stage();
				newWindow.setTitle("Session stats");
				newWindow.setScene(scene);

				newWindow.setX(primaryStage.getX() + 200);
				newWindow.setY(primaryStage.getY() + 100);
				newWindow.setResizable(false);
				newWindow.show();
			} else {
				Platform.runLater(() -> Util.showAlert("Blokirani ste ili token nije vazeci", false));
			}
		});
		Menu menuPregled = new Menu("", labelPregled);
		menuBar.getMenus().add(menuPregled);

		Label labelLozinka = new Label("Promjena lozinke");
		labelLozinka.setOnMouseClicked(action -> {
			if (open) {
				return;
			}
			open = true;
			VBox vBox = new VBox();
			PasswordField pf = new PasswordField();
			Button button = new Button("Change");
			button.setMinWidth(100);
			button.setMinHeight(30);
			pf.setPromptText("password");
			pf.setMaxWidth(200);
			pf.setMinHeight(30);
			vBox.setAlignment(Pos.CENTER);
			vBox.getChildren().add(new Label("Unesite novu lozinku:"));
			vBox.getChildren().add(pf);

			vBox.setSpacing(20);
			vBox.getChildren().add(button);
			Scene scene = new Scene(vBox, 300, 200);
			Stage newWindow = new Stage();
			newWindow.setTitle("Password Change");
			newWindow.setScene(scene);

			newWindow.setX(primaryStage.getX() + 200);
			newWindow.setY(primaryStage.getY() + 100);
			Platform.runLater(() -> button.requestFocus());
			button.setOnAction(action1 -> {
				Main.model.getUser().setPassword(pf.getText());
				open = false;
				scene.getWindow().hide();
				Util.showAlert("Uspjesna promjena lozinke", false);
			});
			newWindow.setOnCloseRequest(a -> {
				open = false;
			});
			newWindow.show();
		});
		Menu menuLozinka = new Menu("", labelLozinka);
		menuBar.getMenus().add(menuLozinka);

		Label labelMapa = new Label("Mapa");
		labelMapa.setOnMouseClicked(action -> {
			Label lab = new Label("");
			Mapa map = new Mapa(Main.x, Main.y, false);
			Mapa.setLabel(lab);
			Util.getMapa(map, Main.days);
			if (!Util.blocked) {
				GridPane gP = new GridPane();
				gP.setAlignment(Pos.CENTER);
				VBox vBox = new VBox();
				Label naslov = new Label("Mapa u zadnjih " + Main.days + " dana");
				vBox.getChildren().add(naslov);
				naslov.setFont(new Font(15));
				vBox.getChildren().add(gP);
				vBox.getChildren().add(lab);
				vBox.setAlignment(Pos.TOP_CENTER);
				VBox.setMargin(naslov, new Insets(30, 0, 0, 0));
				vBox.setSpacing(10);
				for (int i = 0; i < Main.x; i++) {
					for (int j = 0; j < Main.y; j++) {
						GridPane.setRowIndex(map.getPolja()[i][j], i);
						GridPane.setColumnIndex(map.getPolja()[i][j], j);
						gP.getChildren().addAll(map.getPolja()[i][j]);
					}
				}
				gP.setPadding(new Insets(5, 5, 5, 5));
				Scene scene = new Scene(vBox, 450, 400);
				Stage newWindow = new Stage();
				newWindow.setTitle("Mapa");
				newWindow.setScene(scene);
				newWindow.show();
			}
		});
		Menu menuMapa = new Menu("", labelMapa);
		menuBar.getMenus().add(menuMapa);
	}
}