package seps2014.mosergolser.view;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15)
 * ---------------------------------------------------------------------------
 * Asking the player for his/her desired matrix dimensions.
 */

import java.util.ArrayList;

import seps2014.mosergolser.controller.GameController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class InitGUI extends Application {

	/**
	 * Before the actual game starts, the user can specify the number of rows
	 * and columns of the board, the number of blocking cells at the beginning
	 * of the game and whether or not the deterministic factory shall be used
	 * (in which case the user is to tick the box on top of the 'confirm' button).
	 */
	@Override
	public void start (Stage primaryStage) throws Exception {
		Group root = new Group();
		root.setId("IO_root");
		int bgWidth = 820;
		int bgHeight = 920;
		Rectangle background = new Rectangle(bgWidth, bgHeight);
		background.setFill(Color.LIGHTGRAY);
		root.getChildren().add(background);
		
		String[] denominators = new String[]{"rows", "columns", "blocking cells at "
				+ "the beginning"};
		
		ArrayList<ListView<Integer>> lists = new ArrayList<>();
		
		Integer[][] choicesInteger = new Integer[][] {
				{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
				{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
				{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
		};
		
		for (int i = 0; i < 3; i++) {
			int listWidth = 200;
			int listDistance = 40;
			int yOffset = 50;
			int xOffsetLabel = 100;
			int xOffsetList = 500;
			
			Label label = new Label("How many " + denominators[i] + "?");
			label.setLayoutX(xOffsetLabel);
			label.setLayoutY(yOffset + i*(listWidth+listDistance));
			label.setPrefHeight(yOffset);
			label.setPrefWidth(listWidth + 100);
			label.setAlignment(Pos.CENTER);
			
			ObservableList<Integer> choices = FXCollections.observableArrayList(choicesInteger[i]);
			ListView<Integer> list = new ListView<>(choices);
			list.setLayoutX(xOffsetList);
			list.setLayoutY(yOffset + i*(listWidth + listDistance));
			list.setPrefHeight(listWidth);
			list.setPrefWidth(listWidth);
			lists.add(list);
			
			root.getChildren().addAll(label, list);
		}
		
		CheckBox beRandom = new CheckBox("Be deterministic." + System.lineSeparator() + 
				"In this case the board will be 4x4.");
		beRandom.setLayoutX(290);
		beRandom.setLayoutY(735);
		root.getChildren().add(beRandom);
		
		Button button = new Button("Confirm!");
		button.setLayoutX(210);
		button.setLayoutY(785);
		button.setPrefWidth(400);
		button.setPrefHeight(100);
		button.setOnAction(event -> {
			Integer rows = lists.get(0).getSelectionModel().getSelectedItem();
			Integer columns = lists.get(1).getSelectionModel().getSelectedItem();
			Integer evils = lists.get(2).getSelectionModel().getSelectedItem();
			
			boolean beDeterministic = beRandom.selectedProperty().get();
			
			if (rows == null || columns == null || evils == null) {
				button.setText("Invalid configuration! Set again!");
				return;
			}
			
			try {
				if (checkValidity(rows, columns, evils)) {
					new GameController(rows, columns, evils, primaryStage, beDeterministic);
				} else {
					button.setText("Invalid configuration! Set again!");
				}
			} catch (Exception exception) {
				root.getChildren().clear();
				root.getChildren().add(new Rectangle(bgWidth, bgHeight));
				
				Label errorText = new Label("oops... some error occurred! "
						+ "very sorry about it...");
				errorText.setLayoutX(100);
				errorText.setLayoutY(410);

				root.getChildren().add(errorText);
				exception.printStackTrace();
			}
		});
		root.getChildren().add(button);
		
		primaryStage.setX(200);
		primaryStage.setY(50);
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Board Configuration");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch (args);
	}
	
	private boolean checkValidity(int rows, int height, int evils) {
		if (rows < 2 && height < 2) return false;
		if (rows*height <= evils) return false;
		if (rows < 1 || height < 1) return false;
		
		return true;
	}
}
