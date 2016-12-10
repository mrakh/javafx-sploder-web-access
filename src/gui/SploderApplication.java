package gui;

import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import log_on.SploderClient;

public class SploderApplication extends Application {
	
	public static final Font RALEWAY = Font.loadFont(getFileInputStream("resources/fonts/RALEWAY-SEMIBOLD.TTF"), 12);
	public static final ExecutorService LOGIN_REQUEST = Executors.newSingleThreadExecutor();
	private static final String BACKGROUND_GRADIENT = "from 0% 0% to 0% 100%, #670167 0%, #34103e 100%";
	public static final Background APPLICATION_BACKGROUND = new Background(new BackgroundFill(LinearGradient.valueOf(BACKGROUND_GRADIENT), null, null));
	
	public static final Image SPLODER_BUTTON_IMAGE = new Image("file:resources/images/loginButtonBackground.png");
	public static final Image SPLODER_BUTTON_IMAGE_HOVER = new Image("file:resources/images/loginButtonBackgroundHover.png");
	public static final Background SPLODER_BUTTON_BACKGROUND = new Background(new BackgroundImage(
			SPLODER_BUTTON_IMAGE,
			BackgroundRepeat.NO_REPEAT,
			BackgroundRepeat.NO_REPEAT,
			BackgroundPosition.CENTER,
			new BackgroundSize(SPLODER_BUTTON_IMAGE.getWidth(), SPLODER_BUTTON_IMAGE.getHeight(), false, false, false, false)
	));
	public static final Background SPLODER_BUTTON_BACKGROUND_HOVER = new Background(new BackgroundImage(
			SPLODER_BUTTON_IMAGE_HOVER,
			BackgroundRepeat.NO_REPEAT,
			BackgroundRepeat.NO_REPEAT,
			BackgroundPosition.CENTER,
			new BackgroundSize(SPLODER_BUTTON_IMAGE.getWidth(), SPLODER_BUTTON_IMAGE.getHeight(), false, false, false, false)
	));
	public static final Consumer<Button> SPLODER_BUTTON_DECORATOR = button -> {
		button.setMinSize(100, 33);
		button.setMaxSize(100, 33);
		button.setId("sploderbutton");
	};
	
	private static SploderClient client = new SploderClient("", "");
	public static Stage currentStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		currentStage = primaryStage;
		primaryStage.setMinWidth(600);
		primaryStage.setMinHeight(400);
		primaryStage.setWidth(700);
		primaryStage.setHeight(600);
		primaryStage.setTitle("Sploder Graphics Importer");
		
		primaryStage.setScene(new LoginScene());
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		client.logOut();
		super.stop();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	protected static FileInputStream getFileInputStream(String path) {
		try {
			return new FileInputStream(path);
		} catch(Exception e) {
			return null;
		}
	}
	
	public static SploderClient getClient() {
		return client;
	}
	
	public static Scene getCurrentScene() {
		return currentStage.getScene();
	}
	
	public static void setCurrentScene(Scene s) {
		currentStage.setScene(s);
	}
}
