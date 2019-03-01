package application;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Gameover-luokka
 * Tulostetaan viesti "Game Over", saatu pistem‰‰r‰ ja ohjeet kuinka aloitetaan uusi peli. 
 * 
 * @author Mikko Suhonen
 *
 */

public class GameOver extends VBox {

	public GameOver() {
		Text gameover = new Text("Game over!");
		Text score = new Text("Score: "+String.valueOf(Game.player.getScore()));
		Text info = new Text("Press 'R' to replay!");
		
		gameover.getStyleClass().add("gameover");
		score.getStyleClass().add("score");
		info.getStyleClass().add("info");
		
		getChildren().addAll(gameover,score, info);
		setMinWidth(Main.jump*10);
		
		
		setAlignment(Pos.CENTER);
 		setLayoutY(Main.jump*5);
 		toFront();	//asetetaan n‰kym‰ p‰‰llim‰iseksi
	}

}
