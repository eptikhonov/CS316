import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class TetrisApp extends Application {

	public static final int TILE_SIZE = 20;
	public static final int GRID_WIDTH = 15;
	public static final int GRID_HEIGHT = 20;
	public static final int INFO_PANEL_HEIGHT = 100;
	public static int lineClears = 0;
	public static int score = 0;

	VBox vbox = new VBox(8);
	
	private double time;
	private GraphicsContext g;

	private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];

	private List<Tetromino> original = new ArrayList<>();
	private List<Tetromino> tetrominos = new ArrayList<>();

	private Tetromino selected;

	private Pane createGameboard() {

		Pane gameBoard = new Pane();
		gameBoard.setPrefSize(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

		Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
		g = canvas.getGraphicsContext2D();

		gameBoard.getChildren().addAll(canvas);

		original.add(new Tetromino(Tetromino.color,
				new Piece(0, Direction.DOWN),
				new Piece(1, Direction.LEFT),
				new Piece(1, Direction.RIGHT),
				new Piece(2, Direction.RIGHT)
				));
		original.add(new Tetromino(Tetromino.color,
				new Piece(0, Direction.DOWN),
				new Piece(1, Direction.LEFT),
				new Piece(1, Direction.RIGHT),
				new Piece(1, Direction.DOWN)
				));

		original.add(new Tetromino(Tetromino.color,
				new Piece(0, Direction.DOWN),
				new Piece(1, Direction.RIGHT),
				new Piece(2, Direction.RIGHT),
				new Piece(1, Direction.DOWN)));

		original.add(new Tetromino(Tetromino.color,
				new Piece(0, Direction.DOWN),
				new Piece(1, Direction.RIGHT),
				new Piece(1, Direction.RIGHT, Direction.DOWN),
				new Piece(1, Direction.DOWN)));

		spawn();

		return gameBoard;
	}

	private void update() {
		makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
	}

	private void render() {
		g.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

		tetrominos.forEach(p -> p.draw(g));
	}

	private void placePiece(Piece piece) {
		grid[piece.x][piece.y]++;
	}

	private void removePiece(Piece piece) {
		grid[piece.x][piece.y]--;
	}

	private boolean isOffscreen(Piece piece) {
		return piece.x < 0 || piece.x >= GRID_WIDTH
				|| piece.y < 0 || piece.y >= GRID_HEIGHT;
	}

	private void makeMove(Consumer<Tetromino> onSuccess, Consumer<Tetromino> onFail, boolean endMove) {
		selected.pieces.forEach(this::removePiece);

		onSuccess.accept(selected);

		boolean offscreen = selected.pieces.stream().anyMatch(this::isOffscreen);

		if (!offscreen) {
			selected.pieces.forEach(this::placePiece);
		} else {
			onFail.accept(selected);

			selected.pieces.forEach(this::placePiece);

			if (endMove) {
				sweep();
			}

			return;
		}

		if (!isValidState()) {
			selected.pieces.forEach(this::removePiece);

			onFail.accept(selected);

			selected.pieces.forEach(this::placePiece);

			if (endMove) {
				sweep();
			}
		}
	}

	private boolean isValidState() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				if (grid[x][y] > 1) {
					return false;
				}
			}
		}

		return true;
	}

	private void sweep() {
		List<Integer> rows = sweepRows();
		rows.forEach(row -> {
			for (int x = 0; x < GRID_WIDTH; x++) {
				for (Tetromino tetromino : tetrominos) {
					tetromino.detach(x, row);
				}

				grid[x][row]--;
			}
			score += 1000;
			lineClears += 1;
		});

		rows.forEach(row -> {
			tetrominos.stream().forEach(tetromino -> {
				tetromino.pieces.stream()
				.filter(piece -> piece.y < row)
				.forEach(piece -> {
					removePiece(piece);
					piece.y++;
					placePiece(piece);
				});
			});
		});

		spawn();
	}

	private List<Integer> sweepRows() {
		List<Integer> rows = new ArrayList<>();

		outer:
			for (int y = 0; y < GRID_HEIGHT; y++) {
				for (int x = 0; x < GRID_WIDTH; x++) {
					if (grid[x][y] != 1) {
						continue outer;
					}
				}

				rows.add(y);
			}

		return rows;
	}

	private void spawn() {
		Tetromino tetromino = original.get(new Random().nextInt(original.size())).copy();
		tetromino.move(GRID_WIDTH / 2, 0);

		selected = tetromino;

		tetrominos.add(tetromino);
		tetromino.pieces.forEach(this::placePiece);

		if (!isValidState()) {
			System.exit(0);
		}
		score += 15;
	}

	@Override
	public void start(Stage stage) throws Exception {

		vbox.setPrefWidth(300);
		vbox.setPrefHeight(500);

		vbox.getChildren().addAll(InfoPane.createInfoPane(), createGameboard());
		Scene scene = new Scene(vbox);

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (time % 1000 == 0) {
					score += 1;
					// update score text and colors
					Tetromino.randomColorChanger(score);
					InfoPane.scoreLabel.setText(Integer.toString(score));
					InfoPane.lineClearsText.setText("clears: "+Integer.toString(lineClears));
					InfoPane.updateColor(score);
				}

				time += 0.03;

				if (time >= 0.5) {
					update();
					render();
					time = 0;
				}
			}
		};
		timer.start();

		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.UP) {
				makeMove(p -> p.rotate(), p -> p.rotateBack(), false);
			} else if (e.getCode() == KeyCode.LEFT) {
				makeMove(p -> p.move(Direction.LEFT), p -> p.move(Direction.RIGHT), false);
			} else if (e.getCode() == KeyCode.RIGHT) {
				makeMove(p -> p.move(Direction.RIGHT), p -> p.move(Direction.LEFT), false);
			} else if (e.getCode() == KeyCode.DOWN) {
				makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
			}

			render();
		});

		scene.getRoot().requestFocus();
		stage.setTitle("Tetris");
		stage.setResizable(false);
		stage.sizeToScene();
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
