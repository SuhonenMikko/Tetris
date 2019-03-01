package application;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Oikean puoleinen n‰kym‰ peliss‰. 
 * N‰ytet‰‰n seuraavan nykyinen taso, rivien tyhjennykset ja pisteet.
 * Lis‰ksi n‰ytet‰‰n seuraavan palikan muoto ja piste-enn‰tykset.
 * @author Mikko Suhonen
 *
 */
public class Info extends VBox {
	private double jump;
	
	private StackPane nextblock = new StackPane();
	
	private Text tlevel = new Text("Level: 0");
	private Text tlines = new Text("Lines: 0");
	private Text tscore = new Text("Score: 0");
	
	/**
	 * Alustetaan luokka hakemalla yhden ruudun koko ja luokan eri osat.
	 */
	public Info() {
		this.jump = Main.jump;
		
		getChildren().addAll(createScores(), createblockView(), createHighScores());
		
		setSpacing(jump);
		setPadding(new Insets(jump,0,0,0));
		maxWidth(jump*5);
		
	}
	
	/**
	 * Luo "Tetris"-tekstin, tason, rivit ja pistem‰‰r‰n.
	 * @return palauttaa luodun paneelin
	 */
	public VBox createScores() {		
		VBox numbers = new VBox(5);
		
		Text tetris = new Text("TETRIS");
		tetris.getStyleClass().add("header");
		
		tlevel.getStyleClass().add("textbasic");
		tlines.getStyleClass().add("textbasic");
		tscore.getStyleClass().add("textbasic");
		
		numbers.getChildren().addAll(tetris,tlevel,tlines,tscore);
		numbers.setAlignment(Pos.CENTER);
		
		return numbers;
		
	}
	
	/**
	 * P‰ivitt‰‰ tason, rivit ja pisteet.
	 */
	public void updateScores() {
		tlevel.setText("Level: "+Game.player.getLevel());
		tlines.setText("Lines: "+Game.player.getLines());
		tscore.setText("Score: "+Game.player.getScore());
	}
	
	/**
	 * Luo Seuraavan palikan n‰kym‰n
	 * @return palauttaa paneelin
	 */
	public VBox createblockView() {
		VBox paneeli = new VBox(5);
		
		Text nbtext = new Text("Next:");
		
		nbtext.getStyleClass().add("textbasic");
		
		paneeli.getChildren().addAll(nbtext,nextblock);
		paneeli.setAlignment(Pos.CENTER);
		

		return paneeli;
		
	}
	
	/**
	 * P‰ivitt‰‰ seuraavan palikan n‰kym‰n
	 * @param rect Palikka taulukko
	 * @param color Palikan v‰ri
	 * @param type Palikan arvo
	 */
	public void updateNextBlock(int[][] rect, Color color, int type) {
		Pane paneeli = new Pane();
		
		for(int i = 0; i < rect.length;i++) {
			
				Rectangle rec = new Rectangle((rect[i][1]-Tetrominos.blockoffsets[type][1])*jump,(rect[i][0]-Tetrominos.blockoffsets[type][0])*jump,jump,jump);
				rec.setFill(color);
				
				rec.setStroke(Color.BLACK);
				paneeli.getChildren().add(rec);	
		}
		
		nextblock.setPadding(new Insets(jump/2,jump/2,jump/2,jump/2));
		
		paneeli.setTranslateX(jump);
		paneeli.setMinHeight(jump*4);
		nextblock.getChildren().clear();
		nextblock.getChildren().add(paneeli);
	}
	
	/**
	 * Luo piste-enn‰tykset Player-luokan players-listasta.

	 * @return palauttaa luodun paneelin
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public VBox createHighScores() {
		VBox paneeli = new VBox();
		
		Player.sortPlayers();
		List<Player> players = Player.getPlayers();
		
		
		if(players.size() > 0) { //Luodaan taulukko, jos pelaajia on yli 0.
			TableView<Player> table = new TableView();
			 
			
			TableColumn colname = new TableColumn("Name");
	        TableColumn colscore = new TableColumn("Score");
	        TableColumn colline = new TableColumn("Lines");
	        
	        colname.setMaxWidth((jump*5/3)-(jump/2));

	        table.getColumns().addAll(colname, colscore, colline);
	        
		
			ObservableList<Player> data = FXCollections.observableArrayList(players);
			
			colname.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));
			colscore.setCellValueFactory(new PropertyValueFactory<Player, Integer>("score"));
			colline.setCellValueFactory(new PropertyValueFactory<Player, Integer>("lines"));
			
			
			table.setItems(data);
			table.getStyleClass().add("table-view");
			paneeli.setPadding(new Insets(0,0,0,jump/2));
			
			paneeli.getChildren().add(table);
		} 
		
		
		paneeli.setAlignment(Pos.CENTER);
		
		return paneeli;
	}
}
