package gui;

import static gui.SploderApplication.APPLICATION_BACKGROUND;
import static gui.SploderApplication.RALEWAY;
import static gui.SploderApplication.SPLODER_BUTTON_DECORATOR;

import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

public class LoginScene extends Scene {

	private static AnchorPane anchor = new AnchorPane();
	private static GridPane grid = new GridPane();
	private static ImageView logo = new ImageView("file:resources/images/logintitle.png");
	private static TextField usernameField = new TextField();
	private static PasswordField passwordField = new PasswordField();
	private static Label usernameLabel = new Label("Username");
	private static Label passwordLabel = new Label("Password");
	private static Button loginButton = new Button("Log In");
	private static ProgressIndicator loadingIcon = new ProgressIndicator();
	private static Label invalidLogin = new Label("Login failed.");
	
	private static BooleanProperty logInAttempt = new SimpleBooleanProperty(false);
	private static BooleanProperty emptyFields = new SimpleBooleanProperty(true);
	
	static {
		emptyFields.bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
		loginButton.disableProperty().bind(logInAttempt.or(emptyFields));
		usernameField.disableProperty().bind(logInAttempt);
		passwordField.disableProperty().bind(logInAttempt);
		loadingIcon.visibleProperty().bind(logInAttempt);
		invalidLogin.setVisible(false);
		generateHierarchy();
	}
	
	public LoginScene() {
		super(anchor);
		this.getStylesheets().add("file:styler.css");
	}
	
	public static AnchorPane getTopPane() {
		return anchor;
	}
	
	//http://cdn.sploder.com/chrome/v2-body-bkgd.jpg
	private static void generateHierarchy() {
		anchor.setMinSize(300, 200);
		anchor.setPrefSize(500, 300);
		anchor.setBackground(APPLICATION_BACKGROUND);
		anchor.getChildren().add(grid);
		AnchorPane.setTopAnchor(grid, 0.0);
		AnchorPane.setBottomAnchor(grid, 0.0);
		AnchorPane.setRightAnchor(grid, 0.0);
		AnchorPane.setLeftAnchor(grid, 0.0);
		
		grid.getColumnConstraints().addAll(
				colConstWithHGrow(Priority.ALWAYS),
				colConstWithHGrow(Priority.ALWAYS),
				colConstWithHGrow(Priority.ALWAYS)
		);
		
		grid.getRowConstraints().addAll(
				rowConstWithVGrow(Priority.ALWAYS),  //Region
				rowConstWithVGrow(Priority.ALWAYS),  //Image
				rowConstWithVGrow(Priority.ALWAYS),  //Region
				rowConstWithVGrow(Priority.NEVER),   //Label
				rowConstWithVGrow(Priority.NEVER),   //Field
				rowConstWithVGrow(Priority.NEVER),   //Label
				rowConstWithVGrow(Priority.NEVER),   //Field
				rowConstWithVGrow(Priority.ALWAYS),  //Region
				rowConstWithVGrow(Priority.ALWAYS),  //Button
				rowConstWithVGrow(Priority.ALWAYS),  //Region
				rowConstWithVGrow(Priority.ALWAYS),  //Progress/Invalid
				rowConstWithVGrow(Priority.ALWAYS)   //Region
		);
		
		HBox.setHgrow(logo, Priority.ALWAYS);
		GridPane.setHalignment(logo, HPos.CENTER);
		GridPane.setValignment(logo, VPos.CENTER);
		logo.fitWidthProperty().bind(anchor.widthProperty().subtract(50));
		logo.fitHeightProperty().bind(anchor.heightProperty().divide(3).subtract(60));
		logo.setPreserveRatio(true);
		
		usernameField.setId("splodertextfield");
		usernameField.setPrefWidth(300);
		
		passwordField.setId("splodertextfield");
		passwordField.setPrefWidth(300);
		passwordField.setOnKeyPressed(keyEvent -> {
			if(keyEvent.getCode() == KeyCode.ENTER && !emptyFields.get())
				submitCredentials(null);
		});
		
		GridPane.setHalignment(usernameLabel, HPos.CENTER);
		GridPane.setValignment(usernameLabel, VPos.CENTER);
		usernameLabel.setFont(RALEWAY);
		usernameLabel.setTextFill(Color.WHITESMOKE);
		
		GridPane.setHalignment(passwordLabel, HPos.CENTER);
		GridPane.setValignment(passwordLabel, VPos.CENTER);
		passwordLabel.setFont(RALEWAY);
		passwordLabel.setTextFill(Color.WHITESMOKE);
		
		GridPane.setHalignment(loginButton, HPos.CENTER);
		GridPane.setValignment(loginButton, VPos.CENTER);
		SPLODER_BUTTON_DECORATOR.accept(loginButton);
		loginButton.setOnAction(LoginScene::submitCredentials);
		
		GridPane.setHalignment(loadingIcon, HPos.CENTER);
		GridPane.setValignment(loadingIcon, VPos.CENTER);
		loadingIcon.setEffect(new ColorAdjust(0.5, 0.0, 0.1, 0.0)); // To make it purple
		loadingIcon.setMinSize(30, 30);
		loadingIcon.setMaxSize(30, 30);
		
		GridPane.setHalignment(invalidLogin, HPos.CENTER);
		GridPane.setValignment(invalidLogin, VPos.CENTER);
		invalidLogin.setFont(RALEWAY);
		invalidLogin.setTextFill(Color.ORANGERED);
		
		grid.addColumn(1,
				new Region(),
				logo,
				new Region(),
				usernameLabel,
				usernameField,
				passwordLabel,
				passwordField,
				new Region(),
				loginButton,
				new Region(),
				loadingIcon,
				new Region());
		grid.add(invalidLogin, 1, GridPane.getRowIndex(loadingIcon));
		grid.add(new Region(), 0, 0);
		grid.add(new Region(), 0, 2);
	}
	
	private static ColumnConstraints colConstWithHGrow(Priority p) {
		ColumnConstraints cc = new ColumnConstraints();
		cc.setHgrow(p);
		cc.setMinWidth(10);
		cc.setPrefWidth(100);
		return cc;
	}
	
	private static RowConstraints rowConstWithVGrow(Priority p) {
		RowConstraints rc = new RowConstraints();
		rc.setVgrow(p);
		rc.setMinHeight(10);
		rc.setPrefHeight(30);
		return rc;
	}
	
	private static void submitCredentials(ActionEvent event) {
		invalidLogin.setVisible(false);
		logInAttempt.set(true);
		SploderApplication.getClient().setCredentials(usernameField.getText(), passwordField.getText());
		
		// supplyAsync: Begin authentication with Sploder.
		// handleAsync: Upon authentication completion, update the GUI accordingly.
		CompletableFuture.supplyAsync(SploderApplication.getClient()::logIn).handleAsync((loggedIn, throwable) -> {
			logInAttempt.set(false);
			if(!loggedIn)
				invalidLogin.setVisible(true);
			else
				Platform.runLater(() -> SploderApplication.setCurrentScene(new ImportScene()));
			return true;
		});
	}
}
