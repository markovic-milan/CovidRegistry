package org.unibl.etf.mdp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;

import org.json.JSONArray;
import org.unibl.etf.mdp.gui.main.Main;
import org.unibl.etf.mdp.model.Controller;
import org.unibl.etf.mdp.model.Mapa;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class Util {
	private static final String BASE_URL = "http://localhost:8080/MDP_CentralniRegistar/api/tokeni/";

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

	public static void checkStatus(ListView<String> list) {
		Runnable runnable = () -> {
			String username = "";
			while (!Main.quit) {
				try {
					Thread.sleep(3000);
					JSONArray jsonArray = readAllJSON(BASE_URL + "potencijalni");
					for (int i = 0; i < jsonArray.length(); i++) {
						username = jsonArray.get(i).toString().split(":")[0];
						if (!list.getItems().contains(username)) {
							String line = username;
							Platform.runLater(() -> {
								list.getItems().add(line);
							});
						}
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

	public static void getMapa(Mapa mapa, String username) {
		try {
			URL url = new URL(BASE_URL + username + "/map/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				showAlert("Blokirani ste ili token nije vazeci",false);
			}
			// citanje odgovora
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			output = br.readLine();
			if (output != null) {
				mapa.parseMap(output);
			} else {
				Util.showAlert("Nema informacija o mapi korisnika", false);
			}
			br.close();
			conn.disconnect();
		} catch (MalformedURLException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			Main.loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
	}

	public static void initWindow(BorderPane pane, String resource) throws IOException {
		FXMLLoader loader = new FXMLLoader(Util.class.getResource(resource));
		loader.load();
		Controller controller = loader.getController();
		controller.init(pane);
	}

	public static JSONArray readAllJSON(String url) throws MalformedURLException, IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonArray = readAll(br);
			JSONArray array = new JSONArray(jsonArray);
			return array;
		} finally {
			is.close();
		}
	}

	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static boolean deleteDirectory(File directory) {
		File[] allContents = directory.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directory.delete();
	}
}