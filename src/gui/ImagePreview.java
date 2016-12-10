package gui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ImagePreview extends StackPane {

	ImageView image = null;
	Button delete = new Button("Delete");
	FadeTransition deleteButtonAppear = new FadeTransition(Duration.millis(250), delete);
	FadeTransition deleteButtonDisappear = new FadeTransition(Duration.millis(250), delete);
	
	{
		delete.setMinSize(60, 30);
		delete.setMaxSize(60, 30);
		delete.setId("deletebutton");
		delete.setOnAction(e -> Platform.runLater(() -> {
			ObservableList<Node> tilePaneChildren = ImportScene.getTilePane().getChildren();
			int removedIndex = tilePaneChildren.indexOf(this);
			tilePaneChildren.remove(removedIndex);
		}));
		delete.setVisible(false);
		
		deleteButtonAppear.setFromValue(0);
		deleteButtonAppear.setToValue(1);
		deleteButtonAppear.setCycleCount(1);
		deleteButtonDisappear.setFromValue(1);
		deleteButtonDisappear.setToValue(0);
		deleteButtonDisappear.setCycleCount(1);
		deleteButtonDisappear.setOnFinished(e -> delete.setVisible(false));
	}
	
	public ImagePreview(String path) {
		image = new ImageView(path);
		image.setFitWidth(120);
		image.setFitHeight(120);
		image.setPreserveRatio(true);
		this.setMinSize(120, 120);
		this.setMaxSize(120, 120);
		this.getChildren().addAll(image, delete);
		this.setOnMouseEntered(e -> Platform.runLater(() -> {
			delete.setVisible(true);
			deleteButtonAppear.play();
		}));
		this.setOnMouseExited(e -> Platform.runLater(() -> deleteButtonDisappear.play()));
	}
	
	public Image getImage() {
		return image.getImage();
	}
}
