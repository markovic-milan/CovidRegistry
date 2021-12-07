package org.unibl.etf.mdp.gui.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.unibl.etf.mdp.gui.chat.ReadMes;
import org.unibl.etf.mdp.gui.chat.SendMes;
import org.unibl.etf.mdp.logger.ExceptionLogger;
import org.unibl.etf.mdp.rmi.FajlInterface;
import org.unibl.etf.mdp.util.Util;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Main extends Application {
	public static boolean quit = false;
	private SSLSocket csocket;
	public static String username;
	public static Label label;
	public static Label labelRMI;
	public static boolean isOpen = false;
	public static String ipAddress;
	public static int PORT;
	public static int x, y;
	public static final String PATH = "resources";
	private FajlInterface fileServer;
	public static Logger loger = new ExceptionLogger().getLoger();

	@Override
	public void start(Stage primaryStage) {
		procitajParametre();
		Util.deleteDirectory(new File("tmp"));
		File fs = new File("tmp");
		if (!fs.exists()) {
			fs.mkdirs();
		}
		// RMI
		System.setProperty("java.security.policy", PATH + File.separator + "client_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		// pocetna forma
		Button bt = new Button("NEXT");
		TextField tf = new TextField();
		tf.setPromptText("username");
		bt.setOnAction(action -> {
			if (!tf.getText().isEmpty()) {
				username = tf.getText();
				tf.getScene().getWindow().hide();
				try {
					BorderPane root = new BorderPane();
					MenuBar menuBar = new MenuBar();
					root.setTop(menuBar);
					label = new Label("User chat");
					labelRMI = new Label("Pregled/Preuzimanje");
					labelRMI.setOnMouseClicked(action1 -> {
						try {
							Stage stage = new Stage();
							stage.setTitle("MedFiles");
							stage.setResizable(false);
							VBox vBox = new VBox();
							Label lab = new Label("Odaberite fajl da preuzmete:");
							lab.setFont(new Font(18));
							vBox.setPadding(new Insets(40, 0, 0, 60));
							lab.setAlignment(Pos.CENTER);
							vBox.getChildren().add(lab);
							// vBox.setAlignment(Pos.CENTER);
							vBox.setSpacing(20);
							Scene scene = new Scene(vBox, 400, 450);
							stage.setScene(scene);
							String name = "FileServer";
							Registry registry = LocateRegistry.getRegistry(1099);
							fileServer = (FajlInterface) registry.lookup(name);
							String[] fajlovi = fileServer.prikazInformacija().split(",");
							for (String fajl : fajlovi) {
								Label l = new Label(fajl);
								l.setFont(new Font(15));
								vBox.getChildren().add(l);
								l.setAlignment(Pos.CENTER);
								l.setOnMouseClicked(action2 -> {
									try {
										byte[] fileInBytes = fileServer.download(l.getText());
										Files.write(Paths.get("tmp" + File.separator + fajl), fileInBytes);
										if (fajl.endsWith("zip")) {
											Util.showAlert("Uspjesno preuzimanje zip fajla", false);
										} else {
											Util.showAlert("Uspjesno preuzimanje fajla", false);
										}
									} catch (IOException exception) {
										loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
									}
								});
							}
							stage.show();
						} catch (Exception exception) {
							loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
						}
					});
					label.setOnMouseClicked(action1 -> {
						Stage stage = new Stage();
						stage.setResizable(false);
						BorderPane pane = new BorderPane();
						Scene scene = new Scene(pane, 500, 600);
						stage.setScene(scene);
						try {
							Util.initWindow(pane, "/org/unibl/etf/mdp/view/userChatView.fxml");
							stage.setTitle("UserChat");
							stage.show();
							isOpen = true;
						} catch (IOException exception) {
							loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
						}
					});
					Menu userChat = new Menu("", label);
					Menu rmiMenu = new Menu("", labelRMI);
					menuBar.getMenus().add(userChat);
					menuBar.getMenus().add(rmiMenu);
					startThreads();
					Util.initWindow(root, "/org/unibl/etf/mdp/view/chatView.fxml");
					Util.initWindow(root, "/org/unibl/etf/mdp/view/tokenList.fxml");
					primaryStage.setTitle("MEDAPP");
					primaryStage.setScene(new Scene(root, 905, 625));
					primaryStage.show();
					primaryStage.setOnCloseRequest(action2 -> {
						quit = true;
						synchronized (SendMes.lock) {
							SendMes.lock.notify();
						}
					});
					primaryStage.setResizable(false);
				} catch (Exception exception) {
					loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				}
			} else {
				Util.showAlert("Unesite username", true);
			}
		});
		bt.setMaxHeight(35);
		bt.setMaxWidth(120);
		bt.setMinHeight(35);
		bt.setMinWidth(120);
		tf.setMaxHeight(35);
		tf.setMaxWidth(220);
		tf.setMinHeight(35);
		tf.setMinWidth(200);

		VBox vBox = new VBox();
		Label l = new Label("Unesite username");
		l.setFont(new Font(15));
		vBox.getChildren().add(l);
		vBox.getChildren().add(tf);
		vBox.getChildren().add(bt);
		vBox.setSpacing(35);
		vBox.setAlignment(Pos.CENTER);
		Scene scene = new Scene(vBox, 350, 280);
		Stage newWindow = new Stage();
		newWindow.setScene(scene);
		newWindow.setTitle("LOGIN");
		newWindow.show();
		Platform.runLater(() -> bt.requestFocus());
	}

	public void procitajParametre() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src/org/unibl/etf/mdp/gui/main/program.config"));
			x = Integer.parseInt(prop.getProperty("x"));
			y = Integer.parseInt(prop.getProperty("y"));
			ipAddress = prop.getProperty("IP");
			PORT = Integer.parseInt(prop.getProperty("PORT"));
		} catch (FileNotFoundException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void startThreads() {
		System.setProperty("javax.net.ssl.trustStore", "src/org/unibl/etf/mdp/gui/chat/clientkey.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "sigurnost");
		SSLSocketFactory sslsf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			csocket = (SSLSocket) sslsf.createSocket(ipAddress, PORT);
		} catch (UnknownHostException exception) {
			Util.showAlert("Nepoznata adresa", true);
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			return;
		} catch (IOException exception) {
			Util.showAlert("Chat server nije dostupan", false);
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
			return;
		}
		new ReadMes(csocket).start();
		new SendMes(csocket).start();
	}
}