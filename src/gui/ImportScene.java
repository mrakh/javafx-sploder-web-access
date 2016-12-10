package gui;

import static gui.SploderApplication.APPLICATION_BACKGROUND;
import static gui.SploderApplication.RALEWAY;
import static gui.SploderApplication.SPLODER_BUTTON_DECORATOR;
import static gui.SploderApplication.getFileInputStream;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import utils.ImageUtils;

public class ImportScene extends Scene {
	
	private static final String N = System.lineSeparator();
	private static final String PARAGRAPH = "Add graphics by dragging image" + N
			+ "files here, or by opening images from a folder" + N
			+ "with the 'Open' button.";
	private static final String LIMITATION = "The importer does not currently support animated GIF images." + N
			+ "If the image is not 20 by 20 pixels, 40 by 40 pixels, or 60 by 60 pixels," + N
			+ "the image will not load.";
	
	private static AnchorPane anchor = new AnchorPane();
		private static VBox vBox = new VBox();
			private static ScrollPane scrollPane = new ScrollPane();
				private static AnchorPane scrollAnchor = new AnchorPane();
					private static TilePane tilePane = new TilePane(5, 5);
					private static Label info = new Label(PARAGRAPH);
					private static Label lims = new Label(LIMITATION);
			private static HBox hBox = new HBox();
				private static CheckBox publicPublish = new CheckBox("Make public");
				private static Button chooseFileButton = new Button("Open");
				private static Button importButton = new Button("Import");
				private static Region[] region = new Region[2];
	
	private static ImageReader imageReader = ImageIO.getImageReadersBySuffix("GIF").next();
	private static ListProperty<Node> tilePaneChildrenProperty = new SimpleListProperty<Node>(tilePane.getChildren());
	
	static {
		region[0] = new Region();
		region[1] = new Region();
		info.setVisible(true);
		importButton.disableProperty().bind(tilePaneChildrenProperty.emptyProperty());
		createInfoLabel(info, 25);
		createInfoLabel(lims, 15);
		VBox infoWrapper = new VBox(info, lims);
		setAnchors(infoWrapper, 0.0);
		infoWrapper.setAlignment(Pos.CENTER);
		scrollAnchor.getChildren().add(infoWrapper);
		infoWrapper.setMouseTransparent(true);
		generateHierarchy();
	}
	
	public ImportScene() {
		super(anchor);
		this.getStylesheets().add("file:styler.css");
	}
	
	public static AnchorPane getTopPane() {
		return anchor;
	}
	
	private static void generateHierarchy() {
		anchor.setBackground(APPLICATION_BACKGROUND);
		anchor.setMinSize(300, 200);
		anchor.setPrefSize(500, 300);
		anchor.getChildren().add(vBox);
		
		AnchorPane.setTopAnchor(vBox, 0.0);
		AnchorPane.setBottomAnchor(vBox, 0.0);
		AnchorPane.setLeftAnchor(vBox, 0.0);
		AnchorPane.setRightAnchor(vBox, 0.0);
		
		vBox.getChildren().addAll(scrollPane, hBox);
		
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.prefWidthProperty().bind(anchor.widthProperty());
		scrollPane.setContent(scrollAnchor);
		scrollPane.setStyle("-fx-background-color: transparent;");
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		addDragListenerToScrollPane();
		
		scrollAnchor.prefWidthProperty().bind(anchor.widthProperty());
		scrollAnchor.minHeightProperty().bind(scrollPane.heightProperty());
		scrollAnchor.setBackground(new Background(new BackgroundFill(Color.rgb(128, 24, 128), null, null)));
		scrollAnchor.getChildren().add(tilePane);
		AnchorPane.setBottomAnchor(tilePane, 10.0);
		AnchorPane.setTopAnchor(tilePane, 10.0);
		AnchorPane.setLeftAnchor(tilePane, 10.0);
		AnchorPane.setRightAnchor(tilePane, 10.0);
		tilePane.setStyle("-fx-background-color: transparent;");
		
		hBox.setAlignment(Pos.CENTER);
		hBox.setMinHeight(50);
		hBox.setPrefHeight(50);
		hBox.prefWidthProperty().bind(anchor.widthProperty());
		VBox.setVgrow(hBox, Priority.NEVER);
		hBox.getChildren().addAll(publicPublish, region[0], chooseFileButton, region[1], importButton);
		
		publicPublish.setMinWidth(100);
		publicPublish.setFont(RALEWAY);
		publicPublish.setTextFill(Color.WHITESMOKE);
		HBox.setMargin(publicPublish, new Insets(0, 0, 0, 10));
		
		HBox.setHgrow(region[0], Priority.ALWAYS);
		
		SPLODER_BUTTON_DECORATOR.accept(chooseFileButton);
		chooseFileButton.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Import Image");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
			List<File> files = fileChooser.showOpenMultipleDialog(SploderApplication.currentStage);
			addImagesFromFiles(files);
		});
		
		HBox.setHgrow(region[1], Priority.ALWAYS);
		
		SPLODER_BUTTON_DECORATOR.accept(importButton);
		HBox.setMargin(importButton, new Insets(0, 10, 0, 0));
		importButton.setOnAction(ImportScene::importImages);
		
		hBox.setAlignment(Pos.CENTER);
		
		Tooltip publishToolTip = new Tooltip("Check this box if you want your graphic made public when published.");
		publishToolTip.setFont(RALEWAY);
		publishToolTip.setAutoHide(true);
		publicPublish.setTooltip(publishToolTip);
	}
	
	private static void addDragListenerToScrollPane() {
		scrollAnchor.setOnDragOver(e -> {
			final Dragboard db = e.getDragboard();
			if(!db.hasFiles()) {
				e.consume();
				return;
			} else
				e.acceptTransferModes(TransferMode.COPY);
		});
		
		scrollAnchor.setOnDragDropped(e -> {
			final Dragboard db = e.getDragboard();
			if(!db.hasFiles()) {
				e.setDropCompleted(false);
				e.consume();
				return;
			}
			addImagesFromFiles(db.getFiles());
		});
	}
	
	private static ImagePreview parseToImage(File f) {
		try(ImageInputStream iis = ImageIO.createImageInputStream(f)) {
			if(!Files.probeContentType(f.toPath()).contains("image"))
				return null;
			imageReader.setInput(iis);
			int images = imageReader.getNumImages(true);
			if(images > 1)
				return null;
			else if(!ImageUtils.isValidDimension(f))
				return null;
			else
				return new ImagePreview(f.toURI().toURL().toString());
		} catch(Exception e) {
			System.out.println("Exception hit!");
			e.printStackTrace();
			return null;
		}
	}
	
	private static void addImagesFromFiles(List<File> files) {
		if(files == null || files.isEmpty())
			return;
		files.stream()
			.map(ImportScene::parseToImage)
			.filter(iv -> iv != null)
			.forEach(imagePreview -> Platform.runLater(() -> tilePane.getChildren().add(imagePreview)));
	}
	
	private static void createInfoLabel(Label label, double fontSize) {
		label.visibleProperty().bind(tilePaneChildrenProperty.emptyProperty());
		label.setStyle("-fx-background-color: transparent;");
		label.setFont(Font.loadFont(getFileInputStream("resources/fonts/RALEWAY-LIGHT.TTF"), fontSize));
		label.setTextAlignment(TextAlignment.CENTER);
		label.setTextFill(Color.rgb(248, 248, 248, 0.75));
		label.setMouseTransparent(true);
	}
	
	private static void importImages(ActionEvent e) {
		try {
			List<Image> images = tilePane
					.getChildren()
					.stream()
					.map(node -> ((ImagePreview) node).getImage())
					.collect(Collectors.toList());
			System.out.println("Before Platform.runLater() in importImages()");
			Platform.runLater(() -> {
				Stage dialog = new ImportDialog(images, publicPublish.isSelected());
				System.out.println("Dialog about to show!");
				dialog.show();
				System.out.println("Dialog shown!");
			});
			System.out.println("After Platform.runLater() in importImages()");
		} catch(Exception e1) {
			return;
		}
	}
	
	public static TilePane getTilePane() {
		return tilePane;
	}
	
	public static void setAnchors(Node n, double value) {
		AnchorPane.setTopAnchor(n, value);
		AnchorPane.setBottomAnchor(n, value);
		AnchorPane.setLeftAnchor(n, value);
		AnchorPane.setRightAnchor(n, value);
	}
}
