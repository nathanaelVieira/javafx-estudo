package application;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ComplexShapes extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		stage.setHeight(400);
		stage.setWidth(600);

		Color backGroundColor = Color.valueOf("004491");

		stage.setTitle("Capitulo 2 - Formas Complexas.");

		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, backGroundColor);
		CubicCurve cubicCurve = cubicCurveMethod(backGroundColor);
		Path path = casquinhaDeSorvete();
		Path triangulo = triangulos();

		animacaoTriangulo(scene, triangulo);

		root.getChildren().add(triangulo);
		root.getChildren().add(path);
		root.getChildren().add(cubicCurve);
		stage.setScene(scene);
		stage.show();
	}

	private void animacaoTriangulo(Scene scene, Path triangulo) {
		TranslateTransition transition = new TranslateTransition();
		transition.setNode(triangulo);
		transition.setToX(scene.getHeight() - 100);
		transition.setToY(scene.getWidth() - 100);
		transition.setDuration(Duration.seconds(3));
		transition.setAutoReverse(true);
		transition.setCycleCount(Animation.INDEFINITE);
		transition.play();
	}

	private Path triangulos() {
		Path triangulo = new Path();
		triangulo.setStroke(Color.WHITE);
		triangulo.setStrokeWidth(2);

		// Mover para o ponto inicial (coordenadas x=100, y=100)
		MoveTo moveTo = new MoveTo(100, 100);

		// Criar uma linha para o segundo ponto (x=200, y=100)
		LineTo lineTo1 = new LineTo(200, 100);

		// Criar uma linha para o terceiro ponto (x=150, y=50)
		LineTo lineTo2 = new LineTo(150, 50);

		// Fechar o caminho, retornando ao ponto inicial (x=100, y=100)
		ClosePath closePath = new ClosePath();

		triangulo.setTranslateX(150);
		triangulo.setTranslateY(150);

		// Adicionar os elementos ao Path
		triangulo.getElements().addAll(moveTo, lineTo1, lineTo2, closePath);
		return triangulo;
	}

	private Path casquinhaDeSorvete() {
		// Cone do sorvete.

		/*
		 * a classe Path é parte do pacote javafx.scene.shape e é usada para representar
		 * uma sequência de segmentos e linha e curvas.
		 */
		Path path = new Path();
		path.setStrokeWidth(2);
		path.setStroke(Color.WHITE);

		MoveTo moveTo = new MoveTo();
		moveTo.setX(50);
		moveTo.setY(150);

		QuadCurveTo curveTo = new QuadCurveTo();
		curveTo.setX(150);
		curveTo.setY(150);
		curveTo.setControlX(100);
		curveTo.setControlY(50);

		LineTo lineTo1 = new LineTo();
		lineTo1.setX(50);
		lineTo1.setY(150);

		LineTo lineTo2 = new LineTo();
		lineTo2.setX(100);
		lineTo2.setY(275);

		LineTo lineTo3 = new LineTo();
		lineTo3.setX(150);
		lineTo3.setY(150);

		path.getElements().addAll(moveTo, curveTo, lineTo1, lineTo2, lineTo3);

		path.setTranslateY(-80);
		path.setTranslateX(120);
		return path;
	}

	private CubicCurve cubicCurveMethod(Color backGroundColor) {
		CubicCurve cubicCurve = new CubicCurve(50, 75, 80, -25, 110, 175, 140, 75);
		cubicCurve.setStrokeType(StrokeType.CENTERED);
		cubicCurve.setStrokeWidth(2);

		// Definindo cor de curso, ou seja as extremidades.
		cubicCurve.setStroke(Color.WHITE);

		// Definindo preenchimento
		cubicCurve.setFill(Color.WHITE);

		cubicCurve.setFill(Color.TRANSPARENT);
		return cubicCurve;
	}

}
