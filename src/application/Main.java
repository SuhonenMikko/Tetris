package application;
	

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/**
 * Pääluokka 
 * @author Mikko Suhonen
 *
 */
public class Main extends Application {

	protected static double jump =  40;
	
	private Menu menu = new Menu();
	private Game game;
	private Scene gamescene;
	private Stage stage;
	
	/**
	 * Aloitus metodi. Kysytään pelaajlta nimi ja kun pelaaja valitsee "Play"-käynnistetään itse peli.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			this.stage = primaryStage;
			
	        Scene menuscene = new Scene(menu,jump*15,jump*20);
	        menuscene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

	        menu.btnplay.setOnAction(e -> {	//Kun pelaaja valitsee "PLay" aloitetaan uusi peli.
	        	if(!menu.getName().isEmpty()) {	//Tarksitetaan, onko pealaajn nimi tyhjä
	        		menu.stopDrop();
	        	    initGame();
	        	}
	        });
			
	        stage.setTitle("Tetris");
			stage.setScene(menuscene);
			stage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		
	}
	

	
	/**
	 * Aloittaa uuden pelin. Luo tarvittavat luokat ja asetaa eventhandlerit.
	 */
	public void initGame() {
		game = new Game(jump, menu.getName()); 
		gamescene = new Scene(game.getGameView(),jump*15,jump*20);;
		gamescene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		stage.setScene(gamescene);
		
		 game.getGameView().setOnKeyPressed(
	        new EventHandler<KeyEvent>()
	        {
	            public void handle(KeyEvent e)
	            {
	            	
	            	if(game.isRunning()) {
		            	switch(e.getCode().toString()) {
		    			case "LEFT": 
		    				game.playerMove(1);
		    				break;
		    				
		    			case "RIGHT":
		    				game.playerMove(2);
		    				break;
		    				 
		    			case "DOWN":
		    				game.playerMove(0);
		    				break;
		    				
		    			case "Z": 
		    				game.playerRotate(-1);
		    				break;
		    				
		    			case "X": 
		    				game.playerRotate(1);
		    				break;
		    				
		    			case "SPACE": 
		    				game.drop();
		    				break;
		            	}
	            	} 
	            	
	            	if(e.getCode().toString().equals("R")) {	//Uusi peli
            			initGame();
            		}
	            }
	        });
			 
		 game.getGameView().requestFocus();
		 
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
