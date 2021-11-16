package org.unibl.etf.mdp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Base64;
import java.util.logging.Level;

import org.unibl.etf.mdp.gui.chat.ChatController;
import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.DataModel;
import org.unibl.etf.mdp.model.Mapa;
import org.unibl.etf.mdp.model.Obavjestenje;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

public class Util {
	private static final String BASE_URL = "http://localhost:8080/MDP_CentralniRegistar/api/tokeni/";
	private static Mapa mapaDetalji;
	public static boolean blocked = false, potencijalnoZarazen, zarazen;

	public static boolean checkIsInputEmpty(String... strings) {
		for (String s : strings) {
			if (s.isEmpty())
				return true;
		}
		return false;
	}

	public static void showAlert(String message, boolean error) {
		Alert alert = null;
		if (error) {
			alert = new Alert(Alert.AlertType.ERROR);
		} else {
			alert = new Alert(Alert.AlertType.INFORMATION);
		}
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void initWindow(Stage stage, String title, String resource, int width, int height) {
		FXMLLoader loader = new FXMLLoader(Util.class.getResource(resource));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		Controller controller = loader.getController();
		controller.initModel(getModel());
		stage.setTitle(title);
		stage.setScene(new Scene(root, width, height));
		stage.show();
		stage.setResizable(false);
	}

	public static void getMapaDetalji(int x, int y) {
		if (mapaDetalji != null) {
			GridPane gP = new GridPane();
			Label naslov = new Label("Mapa korisnika " + Main.model.getUser().getUsername());
			naslov.setFont(new Font(15));
			VBox vBox = new VBox();
			vBox.getChildren().add(naslov);
			vBox.getChildren().add(gP);
			vBox.setAlignment(Pos.CENTER);
			gP.setAlignment(Pos.CENTER);
			vBox.getChildren().add(mapaDetalji.getLabel());
			vBox.setSpacing(10);
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					GridPane.setRowIndex(mapaDetalji.getPolja()[i][j], i);
					GridPane.setColumnIndex(mapaDetalji.getPolja()[i][j], j);
					gP.getChildren().addAll(mapaDetalji.getPolja()[i][j]);
				}
			}
			gP.setPadding(new Insets(5, 5, 5, 5));
			Scene scene = new Scene(vBox, 450, 450);
			Platform.runLater(() -> {
				Stage newWindow = new Stage();
				newWindow.setTitle("Obavjestenje o lokaciji");
				newWindow.setScene(scene);
				newWindow.show();
			});
			mapaDetalji = null;
		}
	}

	public static void checkStatus(int x, int y, Label informacijeLabel, Button button) {
		Runnable runnable = () -> {
			while (!Main.closeApp && !zarazen && !potencijalnoZarazen && !blocked) {
				try {
					Thread.sleep(3000);
					if (Main.model.getUser().getToken() != null) {
						URL url = new URL(BASE_URL + Main.model.getUser().getUsername() + "/status");
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setDoOutput(true);
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Content-Type", "application/json");

						JSONObject input = new JSONObject(tokenJson());
						// slanje body dijela
						OutputStream os = conn.getOutputStream();
						os.write(input.toString().getBytes());
						os.flush();
						if (conn.getResponseCode() == 201) {
							// KADA KLIKNE NA DUGME OTVARA SE MAPA
							BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
							String output;
							String last = "";
							output = br.readLine();
							if (!last.equals(output)) {
								last = output;
								if (output.startsWith("-1")) {
									String message = "Oznaceni ste kao potencijalno zarazen";
									Platform.runLater(() -> {
										informacijeLabel.setText(message);
									});
									SerializeUtil.serialize(new Obavjestenje(message));
									button.setDisable(true);
									zarazen = true;
								} else {
									Label lab = new Label("Info");
									mapaDetalji = new Mapa(x, y, false);
									Mapa.setLabel(lab);
									mapaDetalji.parseMap(output);
									String message = "Oznaceni ste kao potencijalno zarazen";
									Platform.runLater(() -> {
										informacijeLabel.setText(message);
										SerializeUtil.serialize(new Obavjestenje(message));
									});
								}
								br.close();
							}
						} else if (conn.getResponseCode() == 202) {
							Platform.runLater(() -> {
								String message = "Oznaceni ste kao zarazen";
								informacijeLabel.setText(message);
								SerializeUtil.serialize(new Obavjestenje(message));
							});
							button.setDisable(true);
							potencijalnoZarazen = true;
						} else if (conn.getResponseCode() == 404) {
							blocked = true;
							Platform.runLater(() -> showAlert("Blokirani ste ili token nije vazeci", false));
							os.close();
							conn.disconnect();
							break;
						} else {
							Platform.runLater(() -> {
								informacijeLabel.setText("Niste zarazeni");
							});
						}
						os.close();
						conn.disconnect();
					}

				} catch (InterruptedException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				} catch (IOException exception) {
					Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
				}
			}
		};
		new Thread(runnable).start();
	}

	public static void sendMap(String xTF, String yTF, String odHH, String odMIN, String doHH, String doMIN,
			LocalDate odDate, LocalDate doDate) {
		try {
			String timeOd = odDate + "*" + odHH + ":" + odMIN;
			String timeDo = doDate + "*" + doHH + ":" + doMIN;
			String mapString = xTF + "," + yTF + "," + timeOd + "," + timeDo + "|";
			URL url = new URL(BASE_URL + "map/" + Main.model.getUser().getUsername() + "/"
					+ Base64.getEncoder().encodeToString(mapString.getBytes()));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject input = new JSONObject(tokenJson());
			// slanje body dijela
			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();
			if (conn.getResponseCode() == 404) {
				blocked = true;
				Platform.runLater(() -> showAlert("Blokirani ste ili token nije vazeci", false));
			} else {
				showAlert("Uspjesno poslana lokacija", false);
			}
			os.close();
			conn.disconnect();
		} catch (MalformedURLException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void initMainDesktopAppWindow(BorderPane borderPane) throws IOException {
		FXMLLoader chatLoader = new FXMLLoader(Util.class.getResource(getChatResource()));
		borderPane.setLeft(chatLoader.load());
		borderPane.setPadding(new Insets(5, 5, 5, 5));
		ChatController chatController = chatLoader.getController();
		chatController.initModel(getModel());
	}

	public static String tokenJson() {
		return "{\"header\":" + "\"" + Main.model.getUser().getToken().getHeader() + "\"" + "," + "\"payload\":" + "\""
				+ Main.model.getUser().getToken().getPayload() + "\"" + "," + "\"sign\":" + "\""
				+ Main.model.getUser().getToken().getSign() + "\"" + "}";
	}

	public static void odjavaIzRegistra() {
		try {
			URL url = new URL(BASE_URL + Main.model.getUser().getUsername() + "/odjava");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject input = new JSONObject(tokenJson());
			// slanje body dijela
			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();
			if (conn.getResponseCode() == 404) {
				blocked = true;
				Platform.runLater(() -> showAlert("Blokirani ste ili token nije vazeci", false));
			} else {
				showAlert("Uspjesno ste se odjavili", false);
				Main.model.getUser().setToken(null);
			}
			os.close();
			conn.disconnect();
		} catch (MalformedURLException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void getMapa(Mapa mapa, int brojDana) {
		try {
			URL url = new URL(BASE_URL + Main.model.getUser().getUsername() + "/map/" + brojDana);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject input = new JSONObject(tokenJson());
			// slanje body dijela
			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();
			if (conn.getResponseCode() == 404) {
				blocked = true;
				Platform.runLater(() -> showAlert("Blokirani ste ili token nije vazeci", false));
			} else {
				// citanje odgovora
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					mapa.parseMap(output);
				}
				br.close();
			}
			os.close();
			conn.disconnect();
		} catch (MalformedURLException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void sendTime() {
		try {
			String vrijeme = Main.vrijemePrijave + ":" + System.currentTimeMillis();
			URL url = new URL(BASE_URL + Main.model.getUser().getUsername() + "/time/" + vrijeme);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject input = new JSONObject(tokenJson());
			// slanje body dijela
			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();
			if (conn.getResponseCode() == 404) {
				blocked = true;
				Platform.runLater(() -> showAlert("Blokirani ste ili token nije vazeci", false));
			}
			os.close();
			conn.disconnect();
		} catch (MalformedURLException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static JSONArray readAllJSON(String sub) throws MalformedURLException, IOException {
		InputStream is = new URL(BASE_URL + sub).openStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonArray = readAll(br);
			JSONArray array = new JSONArray(jsonArray);
			return array;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static String getLoginResource() {
		return "/org/unibl/etf/mdp/gui/login/login.fxml";
	}

	public static String getTokenLoginResource() {
		return "/org/unibl/etf/mdp/gui/token/tokenLogin.fxml";
	}

	public static String getDesktopAppResource() {
		return "/org/unibl/etf/mdp/gui/app/desktopApp.fxml";
	}

	public static String getChatResource() {
		return "/org/unibl/etf/mdp/gui/chat/chatView.fxml";
	}

	public static DataModel getModel() {
		return Main.model;
	}

	public static Stage getStage() {
		return Main.stage;
	}
}