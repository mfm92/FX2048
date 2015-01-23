package seps2014.mosergolser.view;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The view for the game (MVC-Pattern).
 */
import java.util.ArrayList;
import java.util.Properties;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import seps2014.mosergolser.controller.GameController;
import seps2014.mosergolser.controller.Subject;
import seps2014.mosergolser.controller.SubjectState;
import seps2014.mosergolser.model.Cell;

public class Game2048GUI implements Observer, EventHandler<KeyEvent>{

	Subject toObserve;
	
	private Group root;
	private Properties properties;
	private boolean wonShown;
	private int rectWidth;
	private int rectHeight;
	private int rectGap;
	private int bottomStatsArea;
	
	/**
	 * Initiating the view.
	 */
	public Game2048GUI (Stage primaryStage, GameController board, Properties properties) {	
		root = new Group();
		this.properties = properties;
		
		setSubject(board);
		
		rectWidth = 800 / toObserve.getState().getMatrix()[0].length;
		rectHeight = 800 / toObserve.getState().getMatrix().length;
		rectGap = Integer.parseInt(properties.getProperty("RECT_GAP"));
		bottomStatsArea = Integer.parseInt(properties.getProperty("BOTTOM_STATS"));
		
		displayMatrixLayout (toObserve.getState());
		
		Scene scene = new Scene (root, 
				toObserve.getState().getMatrix()[0].length * (rectWidth + rectGap) + rectGap, 
				toObserve.getState().getMatrix().length * (rectHeight + rectGap) 
				+ bottomStatsArea + 2*rectGap);
		
		wonShown = false;
		
		Rectangle background = new Rectangle(scene.getWidth(), scene.getHeight());
		background.setFill(Color.DARKGRAY);
		root.getChildren().add(0, background);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle(properties.getProperty("WINDOW_TITLE"));
		primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, this);
	}

	/**
	 * Setting the subject that is to be observed.
	 */
	@Override public void setSubject (Subject subject) {
		toObserve = subject;
	}

	/**
	 * Updating the view if the subject tells the view to do so.
	 */
	@Override public void update () {		
		SubjectState state = toObserve.getState();
		for (TranslateTransition transition : getTransitions(state.getMatrix())) {
			transition.getNode().toFront();
			transition.play();
		}
	}
	
	/**
	 * Listening for key events and letting the subject update its state
	 * accordingly.
	 */
	@Override
	public void handle (KeyEvent event) {
		toObserve.setState(event);
	}

	/**
	 * Creating all the animations to visualise the merging process. This is achieved
	 * by comparing the previous x- and y- coordinates of each cell with the updated
	 * coordinates.
	 */
	private ArrayList<TranslateTransition> getTransitions (Cell[][] subjectState) {
		ArrayList<TranslateTransition> transitions = new ArrayList<>();
		
		for (int row = 0; row < subjectState.length; row++) {
			for (int column = 0; column < subjectState[0].length; column++) {
				Cell cell = subjectState[row][column];
				Node nodeCell = root.lookup("#SubGroup_" + cell.getPrevX() + "_" + cell.getPrevY());
				final double DURATION_LENGTH = Double.parseDouble(
						properties.getProperty("ANIMATION_DURATION_IN_SECS"));
				boolean showTransition = !cell.isEmpty() || !subjectState[cell.getPrevX()][cell.getPrevY()].hasClashed()
						&& cell.getShiftCounter() > 0;
				
				if (showTransition) {
					TranslateTransition tTransition = new TranslateTransition(
							Duration.seconds(DURATION_LENGTH), nodeCell);
					tTransition.setByX((rectWidth + rectGap)*(cell.getY() - cell.getPrevY()));
					tTransition.setByY((rectHeight + rectGap)*(cell.getX() - cell.getPrevX()));
					tTransition.setAutoReverse(false);
					tTransition.setCycleCount(1);
					tTransition.setOnFinished(event -> displayMatrixLayout(toObserve.getState()));
					transitions.add(tTransition);
				}
			}
		}
		return transitions;
	}
	
	/**
	 * Shows the matrix using the GridPane layout. Furthermore it keeps the user
	 * updated about the total score and moves and tells the user s/he has lost/won
	 * the game (in case).
	 */
	private void displayMatrixLayout (SubjectState state) {
		root.getChildren().remove(root.lookup("#baseGridPane"));		
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(rectGap));
		gridPane.setHgap(rectGap);
		gridPane.setVgap(rectGap);
		gridPane.setId("baseGridPane");
		
		Group allGrids = new Group();
		int fontSize = Integer.parseInt(properties.getProperty("CELL_VALUE_FONT_SIZE"));
		String font = properties.getProperty("CELL_VALUE_FONT");
		String smallScoreColour = properties.getProperty("SCORE_FONT_COLOUR_SMALL");
		String bigScoreColour = properties.getProperty("SCORE_FONT_COLOUR_BIG");
		
		for (int row = 0; row < state.getMatrix().length; row++) {
			RowConstraints rowConstaints = new RowConstraints(rectHeight);
			gridPane.getRowConstraints().add(rowConstaints);
				
			for (int column = 0; column < state.getMatrix()[0].length; column++) {
				Cell cell = state.getMatrix()[row][column];
				ColumnConstraints colConstrains = new ColumnConstraints(rectWidth);
				gridPane.getColumnConstraints().add(colConstrains);
				
				Group grids = new Group();
				grids.setId("SubGroup_" + row + "_" + column);
				
				Rectangle gridRectangle = new Rectangle(rectWidth, rectHeight);
				gridRectangle.setFill(state.getMatrix()[row][column].getBackgroundColor());
				
				String textWebCode = cell.getValue() < 128 ? "#0C090A" : "#E5E4E2";
				String scoreGridStyle = "-fx-text-fill: " + textWebCode + ";";
				
				Label scoreGrid = new Label(state.getMatrix()[row][column].getDisplayIcon() + "");
				scoreGrid.setPrefHeight(rectHeight);
				scoreGrid.setPrefWidth(rectWidth);
				scoreGrid.setAlignment(Pos.CENTER);
				scoreGrid.setFont(Font.font(font, FontWeight.BOLD, fontSize));
				scoreGrid.setStyle(scoreGridStyle);
				
				grids.getChildren().addAll(gridRectangle, scoreGrid);
				grids.setLayoutX(rectWidth*column);
				grids.setLayoutY(rectHeight*row);
				allGrids.getChildren().add(grids);
				gridRectangle.setEffect(cell.hasClashed() ? new Glow(1.0) : null);
		
				if (!(cell.getTmpValue() == 0)) {
					String scoreLabelStyle = "-fx-font-size: " + 
				(cell.getValue() < 100 ? 36 : 24) + "pt; " +
							"-fx-font-family: \"Arial\";" + 
							"-fx-text-alignment: center;" +
							"-fx-text-fill: " + 
							(cell.getTmpValue() < 100 ? smallScoreColour : bigScoreColour) + ";";
					
					Label scoredLabel = new Label("+" + cell.getTmpValue());
					scoredLabel.setPrefHeight(50);
					scoredLabel.setPrefWidth(100);
					scoredLabel.setWrapText(true);
					scoredLabel.setStyle(scoreLabelStyle);
					scoredLabel.setAlignment(Pos.CENTER);
					grids.getChildren().add(scoredLabel);
					
					FadeTransition fadeTransition = new FadeTransition
							(Duration.seconds(0.8), scoredLabel);
					fadeTransition.setFromValue(1);
					fadeTransition.setToValue(0);
					fadeTransition.setAutoReverse(false);
					fadeTransition.setCycleCount(1);
					fadeTransition.play();
					
				}
				gridPane.getChildren().addAll(grids);
				GridPane.setConstraints(grids, column, row);
				GridPane.setValignment(grids, VPos.CENTER);
				GridPane.setHalignment(grids, HPos.CENTER);
			}
		}
		
		RowConstraints scoreRow = new RowConstraints(bottomStatsArea);
		String statsFont = properties.getProperty("STATS_SHOW_FONT");
		String labelStyle = "-fx-font-size: 24pt; " +
				"-fx-font-family: \"" + statsFont + "\";" + 
				"-fx-text-alignment: center;" +
				"-fx-text-fill: " + (state.hasLost() ? "red" : "blue") + ";"
				+ "-fx-background-color: #E8E8E8;";
		
		gridPane.getRowConstraints().add(scoreRow);
		Label scoreLabel = new Label("Score: " + state.getScore());
		scoreLabel.setId("ScoreLabel");
		scoreLabel.setStyle(labelStyle);
		scoreLabel.setPrefWidth((rectGap + rectWidth) * state.getMatrix()[0].length / 2);
		scoreLabel.setPrefHeight(bottomStatsArea);
		scoreLabel.setAlignment(Pos.CENTER);
		
		GridPane.setHalignment(scoreLabel, HPos.CENTER);
		GridPane.setValignment(scoreLabel, VPos.CENTER);
		gridPane.add(scoreLabel, 
				0, 
				state.getMatrix().length, 
				state.getMatrix()[0].length / 2, 
				1);
		
		Label movesLabel = new Label("Moves: " + state.getMoves());
		movesLabel.setId("MovesLabel");
		movesLabel.setStyle(labelStyle);
		movesLabel.setAlignment(Pos.CENTER);
		movesLabel.setPrefHeight(bottomStatsArea);
		movesLabel.setPrefWidth((rectGap + rectWidth) * state.getMatrix()[0].length / 2);
		
		GridPane.setHalignment(movesLabel, HPos.CENTER);
		GridPane.setValignment(movesLabel, VPos.CENTER);
		gridPane.add(movesLabel, 
				(state.getMatrix()[0].length + 1)/ 2, 
				state.getMatrix().length, 
				state.getMatrix()[0].length / 2, 
				1);
		
		if (state.hasWon() && !wonShown) showWin(state, gridPane);
		if (state.hasLost()) showLoss(state, gridPane);
		
		root.getChildren().add(gridPane);
	}
	
	private void showWin (SubjectState state, GridPane gridPane) {
		String winLabelStyle = "-fx-font-size: 24pt; " +
				"-fx-font-family: \"Inconsolata\";" + 
				"-fx-text-alignment: center;" +
				"-fx-text-fill: green;" +
				"-fx-background-color: #E8E8E8;";
		
		Label winLabel = new Label("Congratulations, you reached 2048!");
		winLabel.setId("WinLabel");
		winLabel.setStyle(winLabelStyle);
		winLabel.setAlignment(Pos.CENTER);
		winLabel.setPrefHeight(bottomStatsArea);
		winLabel.setPrefWidth(state.getMatrix()[0].length * (rectGap+rectWidth) + rectGap);
		GridPane.setHalignment(winLabel, HPos.CENTER);
		GridPane.setValignment(winLabel, VPos.CENTER);
		gridPane.add(winLabel, 0, state.getMatrix().length, state.getMatrix()[0].length, 1);
	}
	
	private void showLoss (SubjectState state, GridPane gridPane) {
		int lossFadeDuration = Integer.parseInt(properties.getProperty("LOSS_FADE_DURATION"));
		String deadLabelStyle = "-fx-font-size: 24pt; " +
				"-fx-font-family: \"Inconsolata\";" + 
				"-fx-text-alignment: center;" +
				"-fx-text-fill: red;" +
				"-fx-background-color: #E8E8E8;";
		
		Label deadLabel = new Label("You have lost the game!");
		deadLabel.setId("LostLabel");
		deadLabel.setStyle(deadLabelStyle);
		deadLabel.setAlignment(Pos.CENTER);
		deadLabel.setPrefWidth(state.getMatrix()[0].length * (rectGap+rectWidth) + rectGap);
		deadLabel.setPrefHeight(bottomStatsArea);
		
		GridPane.setHalignment(deadLabel, HPos.CENTER);
		GridPane.setValignment(deadLabel, VPos.CENTER);
		gridPane.add(deadLabel, 0, state.getMatrix().length, state.getMatrix()[0].length, 1);
		
		int xOffset = 310;
		int yOffset = 368;
		
		Label finalStats = new Label("Final score: " + state.getScore() + System.lineSeparator()
				+ "Total moves: " + state.getMoves());
		finalStats.setAlignment(Pos.CENTER);
		finalStats.setLayoutX(xOffset);
		finalStats.setLayoutY(yOffset);
		finalStats.setStyle("-fx-font-size: 24pt; " +
				"-fx-font-family: \"Inconsolata\";" + 
				"-fx-text-alignment: center;" +
				"-fx-text-fill: black;");
		root.getChildren().add(0, finalStats);
		
		FadeTransition fadeTransition = new FadeTransition(
				Duration.seconds(lossFadeDuration), gridPane);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0.12);
		fadeTransition.setAutoReverse(false);
		fadeTransition.setCycleCount(1);
		fadeTransition.setOnFinished(event -> finalStats.toFront());
		fadeTransition.play();
	}
}