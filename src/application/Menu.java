package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
/**
 * Aloitusnäkymä
 * Pelaaja syöttää nimen ja peli alkaa kun pelaaja painaa "Play"-painiketta.
 * @author Mikko Suhonen
 *
 */
public class Menu extends Pane {
	
	private Text header = new Text("Tetris");
	Button btnplay = new Button("Play");
	private TextField tfnimi;
	private Random rnd = new Random();
	private List<Rectangle> bgblocks = new ArrayList<Rectangle>();
	private Timeline drop;
	
	public Menu() {
	        double jump = Main.jump;
			getStyleClass().add("bgleft");
			
			
			for(int i = 0; i < 15;i++) { //Luodaan 5 tippuvaa palikkaa taustalle
				spawnBlock(true);
			}
			
			createBackground();
		
			
			VBox main = new VBox(50);
			main.getStyleClass().add("bgright");
			main.setOpacity(0.9);
			
			HBox paneeli = new HBox(10);
			
			main.setAlignment(Pos.CENTER);
			paneeli.setAlignment(Pos.CENTER);
			
	        
	        Text lbnimi = new Text("Name:");
	        tfnimi = new TextField();
	        
	        header.setFill(Color.WHITE);
	        lbnimi.setFill(Color.WHITE);
			lbnimi.getStyleClass().add("infotext");
	        
	        paneeli.getChildren().addAll(lbnimi,tfnimi,btnplay);
	        
	        
	        main.getChildren().addAll(header,paneeli);
			header.getStyleClass().add("menuheader");
			
			main.setPrefWidth(jump*15);
			main.setPrefHeight(jump*20);
			

			getChildren().add(main);
			main.toFront();
			
	}
	
	/**
	 * Luo aloitusvalikkoon tippuvien palikoiden animaation
	 */
	public void createBackground() {
		drop = new Timeline(new KeyFrame(Duration.millis(1000/300), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                for(Rectangle block : bgblocks) {//Tarkistetaan onko palikoita ruudun ulkpuolella

            	   if(block.getY()+1 > 20*Main.jump) {
            		   block.setY(Main.jump*-4);
            	   } else {
                	   block.setY(block.getY()+1);
            	   }
                }
            }
        }));
	 
		drop.setCycleCount(Timeline.INDEFINITE);
		drop.play();
	}
	
	
	/**
	 * Pysäyttää tippumisen.
	 */
	public void stopDrop() {
		drop.stop();
	}
	
	/**
	 * Luo taustalle palikan
	 * @param randomY Jos true palikalle arvotaan myös satunnainen Y-sijainti.
	 */
	public void spawnBlock(boolean randomY) {
		
		int y;
		if(randomY) {
			y = rnd.nextInt(20);
		} else {
			y = -4;
		}
		
		int x = rnd.nextInt(15);
		int c = rnd.nextInt(7);
		
		int r;
		if(c != 1) {
			r = rnd.nextInt(4);
		} else {
			r = 0;
		}
		
		List<Rectangle> adding = new ArrayList<Rectangle>();
		
		for(int i = 0; i < Tetrominos.cblock[c][r].length;i++) {
			Rectangle rect = new Rectangle((Tetrominos.cblock[c][r][i][1]+x)*Main.jump,(Tetrominos.cblock[c][r][i][0]+y)*Main.jump-Main.jump,Main.jump,Main.jump);
			rect.setFill(Tetrominos.blockcolors[c]);
			rect.setStroke(Color.BLACK);
			rect.setOpacity(0.3);
			adding.add(rect);
		}
		
		boolean stacking = false;
		for(Rectangle block : adding) {
			for(Rectangle block2 : bgblocks) {
				if(block.getBoundsInParent().intersects(block2.getBoundsInParent())) {
					stacking = true;
					break;
				}
			}
			
			if(stacking) break;
		}
		
		if(stacking) {
			spawnBlock(true);
		} else {
			getChildren().addAll(adding);
			bgblocks.addAll(adding);
		}
		
	}
	
	/**
	 * Käytetään tarkistukseen onko pelaajan nimi tyhjä ja Player-luokan alustuksessa.
	 * @return palauttaa pelaajan nimen
	 */
	
	public String getName() {
		return this.tfnimi.getText();
	}
}
