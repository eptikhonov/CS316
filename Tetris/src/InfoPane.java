import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class InfoPane {
	public static Label scoreLabel;
	public static Text lineClearsText;

	static Rectangle infoPanelRectangle;

	static Pane createInfoPane() {
		Pane infoPane = new Pane();
		infoPane.setPrefWidth(TetrisApp.GRID_WIDTH * TetrisApp.TILE_SIZE);
		infoPane.setPrefHeight(100);

		infoPanelRectangle = new Rectangle();
		infoPanelRectangle.setWidth(TetrisApp.GRID_WIDTH * TetrisApp.TILE_SIZE);
		infoPanelRectangle.setHeight(100);
		infoPanelRectangle.setArcWidth(15);
		infoPanelRectangle.setArcHeight(15);
		infoPanelRectangle.setFill(Tetromino.color);

		Rectangle lineClearsRectangle = new Rectangle();
		lineClearsRectangle.setWidth(125);
		lineClearsRectangle.setHeight(30);
		lineClearsRectangle.setArcWidth(15);
		lineClearsRectangle.setArcHeight(15);
		lineClearsRectangle.setFill(Color.WHITE);
		lineClearsRectangle.setX(165);
		lineClearsRectangle.setY(55);

		lineClearsText = new Text();
		lineClearsText.setText(Integer.toString(TetrisApp.lineClears));
		lineClearsText.setFill(Tetromino.color);
		lineClearsText.setStyle("-fx-font: 24 arial;");
		lineClearsText.setX(168);
		lineClearsText.setY(79);

		Rectangle shapePreviewRectangle = new Rectangle();
		shapePreviewRectangle.setWidth(100);
		shapePreviewRectangle.setHeight(70);
		shapePreviewRectangle.setArcWidth(15);
		shapePreviewRectangle.setArcHeight(15);
		shapePreviewRectangle.setFill(Color.WHITE);
		shapePreviewRectangle.setX(15);
		shapePreviewRectangle.setY(15);

		scoreLabel = new Label();
		scoreLabel.setText(Integer.toString(TetrisApp.score));
		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.setStyle("-fx-font: 36 arial;");
		scoreLabel.setLayoutX(168);
		scoreLabel.setLayoutY(10);

		infoPane.getChildren().addAll(infoPanelRectangle, shapePreviewRectangle, lineClearsRectangle, lineClearsText, scoreLabel);

		return infoPane;
	}

	static void updateColor(int score) {
		if (score % 10 == 0) {
			infoPanelRectangle.setFill(Tetromino.color);
			lineClearsText.setFill(Tetromino.color);

		}
	}
}
