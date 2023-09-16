package estudy_imageview;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PhotoViewer extends Application {

	private final List<String> imageFiles = new ArrayList<>();
	private int currentIndex = -1;

	public enum ButtonMove {
		NEXT, PREV
	};

	private ImageView currentImageView;
	private ProgressIndicator progressIndicator;
	private AtomicBoolean loading = new AtomicBoolean(false);

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Photo Viewer");
		Group root = new Group();
		Scene scene = new Scene(root, 800, 400, Color.valueOf("#1A1919"));

//		scene.getStylesheets().add(getClass().getResource("photo-viewer.css").toExternalForm());

		primaryStage.setScene(scene);

		currentImageView = createImageView(scene.widthProperty());

		setupDragDrop(scene);

		Group buttonGroup = createButtonPanel(scene);
		progressIndicator = createProgressIndicator(scene);

		root.getChildren().addAll(currentImageView, buttonGroup, progressIndicator);

		primaryStage.show();
	}

	// Configuração de arrasta e largar.
	private void setupDragDrop(Scene scene) {
		scene.setOnDragOver((DragEvent event) -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasFiles() || (dragboard.hasUrl() && isValidImageFile(dragboard.getUrl()))) {
				event.acceptTransferModes(TransferMode.LINK);
			} else {
				event.consume();
			}
		});

		scene.setOnDragDropped((DragEvent event) -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasFiles() && !dragboard.hasUrl()) {
				dragboard.getFiles().stream().forEach(file -> {
					try {
						addImage(file.toURI().toURL().toString());
					} catch (MalformedURLException exception) {
						exception.printStackTrace();
					}
				});
			} else {
				addImage(dragboard.getUrl());
			}

			if (currentIndex > -1) {
				loadImage(imageFiles.get(currentIndex));
			}

		});
	}

	private Group createButtonPanel(Scene scene) {
		// create button panel
		Group buttonGroup = new Group();
		Rectangle buttonArea = new Rectangle(0, 0, 60, 30);
		buttonArea.setFill(Color.valueOf("#1A1919"));
		buttonArea.setStroke(Color.WHITE);
		buttonArea.setStrokeWidth(1.2);
		buttonArea.setArcHeight(20);
		buttonArea.setArcWidth(20);

//		buttonArea.getStyleClass().add("button-panel");
		buttonGroup.getChildren().add(buttonArea);
		// left arrow button
		Arc leftButton = new Arc(12, 16, 15, 15, -30, 60);
		leftButton.setType(ArcType.ROUND);
		leftButton.setFill(Color.WHITE);
		// return to previous image
		leftButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
			System.out.println("busy loading? " + loading.get());
			// if no previous image or currently loading.
			if (currentIndex == 0 || loading.get())
				return;
			int indx = gotoImageIndex(ButtonMove.PREV);
			if (indx > -1) {
				loadImage(imageFiles.get(indx));
			}
		});

		// right arrow button
		Arc rightButton = new Arc(50, 16, 15, 15, 180 - 30, 60);
		rightButton.setType(ArcType.ROUND);
		rightButton.setFill(Color.WHITE);
		// advance to next image
		rightButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
			System.out.println("busy loading? " + loading.get());
			// if no next image or currently loading.
			if (currentIndex == imageFiles.size() - 1 || loading.get())
				return;
			int indx = gotoImageIndex(ButtonMove.NEXT);
			if (indx > -1) {
				loadImage(imageFiles.get(indx));
			}
		});
		// add buttons to button group
		buttonGroup.getChildren().addAll(leftButton, rightButton);
		// move button group when scene is resized
		buttonGroup.translateXProperty().bind(scene.widthProperty().subtract(buttonArea.getWidth() + 6));
		buttonGroup.translateYProperty().bind(scene.heightProperty().subtract(buttonArea.getHeight() + 6));
		return buttonGroup;
	}

	private int gotoImageIndex(ButtonMove direction) {
		int size = imageFiles.size();
		if (size == 0) {
			currentIndex = -1;
		} else if (direction == ButtonMove.NEXT && size > 1 && currentIndex < size - 1) {
			currentIndex += 1;
		} else if (direction == ButtonMove.PREV && size > 1 && currentIndex > 0) {
			currentIndex -= 1;
		}
		return currentIndex;
	}

	private ProgressIndicator createProgressIndicator(Scene scene) {
		ProgressIndicator progress = new ProgressIndicator(0);
		progress.setVisible(false);
		progress.layoutXProperty().bind(scene.widthProperty().subtract(progress.widthProperty()).divide(2));
		progress.layoutYProperty().bind(scene.heightProperty().subtract(progress.heightProperty()).divide(2));
		return progress;
	}

	private void loadImage(String url) {
		if (!loading.getAndSet(true)) {
			Task<?> loadImage = createWorker(url);
			progressIndicator.setVisible(true);
			progressIndicator.progressProperty().unbind();
			progressIndicator.progressProperty().bind(loadImage.progressProperty());
			try {
				new Thread(loadImage).start();
			} catch (RuntimeException exception) {
				exception.printStackTrace();
			}
		}
	}

	private Task<?> createWorker(String url) {

		return new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				Image img = new Image(url, false);
				Platform.runLater(new Runnable() {
					public void run() {
						System.out.println("done loading image " + url);
						currentImageView.setImage(img);
						progressIndicator.setVisible(false);
						loading.set(false);
					}
				});

				return true;
			}
		};
	}

	private void addImage(String url) {
		if (isValidImageFile(url)) {
			currentIndex += 1;
			imageFiles.add(currentIndex, url);
		}
	}

	private boolean isValidImageFile(String url) {
		List<String> imgTypes = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
		return imgTypes.stream().anyMatch(t -> url.endsWith(t));
//		return Files.exists(Path.of(url), LinkOption.NOFOLLOW_LINKS);
	}

	private ImageView createImageView(ReadOnlyDoubleProperty widthProperty) {
		ImageView imageView = new ImageView();
		imageView.setPreserveRatio(true);
		imageView.fitWidthProperty().bind(widthProperty);
		return imageView;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
