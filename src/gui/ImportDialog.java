package gui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ImportDialog extends Stage {
	
	private ExecutorService imageUploader = Executors.newSingleThreadExecutor();
	private ProgressBar progressBar = new ProgressBar(0);
	private List<Image> listToProcess = null;
	private boolean isPublic;
	
	public ImportDialog(List<Image> listToProcess, boolean isPublic) {
		super();
		this.listToProcess = listToProcess;
		this.isPublic = isPublic;
		this.setScene(new Scene(generateHierarchy(), 400, 100));
		this.initOwner(SploderApplication.currentStage);
		this.initModality(Modality.WINDOW_MODAL);
		this.initStyle(StageStyle.UNDECORATED);
		this.setOnShowing(this::beginUpload);
		System.out.println("ImportDialog successfully instantiated!");
	}
	
	private AnchorPane generateHierarchy() {
		AnchorPane anchor = new AnchorPane();
		VBox vBox = new VBox();
		ImportScene.setAnchors(vBox, 0.0);
		anchor.getChildren().add(vBox);
		vBox.setAlignment(Pos.CENTER);
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> {
			imageUploader.shutdown();
			Platform.runLater(this::close);
		});
		progressBar.progressProperty().addListener((ov, o, n) -> {
			if(n.doubleValue() >= 1)
				Platform.runLater(this::close);
		});
		
		vBox.getChildren().addAll(region(Priority.ALWAYS), progressBar, region(Priority.ALWAYS), cancel, region(Priority.ALWAYS));
		return anchor;
	}
	
	private void beginUpload(WindowEvent e) {
		System.out.println("Beginning upload!");
		final double numOfImages = listToProcess.size();
		for(int i = 0; i < numOfImages; i++) {
			final int index = i;
			System.out.println("About to submit image!");
			imageUploader.submit(() -> {
				try {
					SploderApplication.getClient().saveGraphicAs(true, !isPublic, listToProcess.get(index));
				} catch (Exception e1) {
					System.out.println("Error saving graphic at index " + (numOfImages - listToProcess.size()));
				}
				progressBar.setProgress((double)(index+1)/numOfImages);
			});
			System.out.println("Image submitted!");
		}
	}
	
	private Region region(Priority p) {
		Region r = new Region();
		VBox.setVgrow(r, p);
		return r;
	}
}
