package org.unibl.etf.mdp.gui.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.gui.chat.SendMes;
import org.unibl.etf.mdp.logger.ExceptionLogger;
import org.unibl.etf.mdp.model.DataModel;
import org.unibl.etf.mdp.util.Util;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static boolean closeApp = false;
	public static DataModel model;
	public static Stage stage;
	public static Long vrijemePrijave;
	public static String ipAddress;
	public static int PORT;
	public static int x, y, days;
	public static Logger loger;

	@Override
	public void start(Stage primaryStage) {
		loger = new ExceptionLogger().getLoger();
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src/org/unibl/etf/mdp/gui/main/program.config"));
			x = Integer.parseInt(prop.getProperty("x"));
			y = Integer.parseInt(prop.getProperty("y"));
			days = Integer.parseInt(prop.getProperty("days"));
			ipAddress = prop.getProperty("IP");
			PORT = Integer.parseInt(prop.getProperty("PORT"));
		} catch (FileNotFoundException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		} catch (IOException exception) {
			loger.log(Level.SEVERE, exception.fillInStackTrace().toString());
		}
		stage = primaryStage;
		model = new DataModel();
		Util.initWindow(primaryStage, "Login", Util.getLoginResource(), 350, 400);
		primaryStage.setOnCloseRequest(action -> {
			closeApp = true;
			if (model.getUser().getToken() != null)
				Util.sendTime();
			synchronized (SendMes.lock) {
				SendMes.lock.notify();
			}
		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}