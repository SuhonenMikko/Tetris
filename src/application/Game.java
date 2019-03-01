package application;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 * Tetriksen keskeisin luokka. Sis�lt�� pelaamiseen tarvittavat metodit.
 * 
 * @author Mikko Suhonen
 *
 */

public class Game extends Main {
	
	protected static Player player;
	
	private HBox gameView = new HBox(); //Kokonainen n�kym�
	private Pane gameleft; //Vasemmanpuoleinen n�kym�
	private VBox info; //Oikeanpuoleinen n�kym�
	private Rectangle highlight; //Varjo, joka n�ytt�� palikan tiputuskohdan
	private double jump, width,height; //Ruudun leveys/korkeus, ikkunan leveys ja korkeus
	private int[][][][] cblock = Tetrominos.cblock; //Palikan muodot taulukko
	private Color[] blockcolors = Tetrominos.blockcolors; //Palikan v�rit taulukko

	
	private int r, c, x, y; // r = palikan kulma, c = nykyisen palikan tyyppi, x = x-sijainti,  y = y-sijainti
	
	private int nextb = nextBlock(); //Seuraavan palikan numero
	private int[][] board = new int[20][10]; //Taulukko, johon palikoiden sijainnit tallennetaan
	private int blocks = 0; //Palikoiden lukum��r�
	
	private ArrayList<Rectangle> playerblocks= new ArrayList<Rectangle>(); //Nykyisen palikan osat
	private ArrayList<Rectangle> ghostblocks = new ArrayList<Rectangle>(); //Nykyisen palikan haamun osat
	private ArrayList<Rectangle> lockedblocks = new ArrayList<Rectangle>(); //Lukitut palikat
	
	
	private boolean running = true; //Pelin nykyinen tila
	private Timeline drop; //Tiputus animaatio
	private Timeline lockblock; //Tiputus animaatio
	private boolean locking = false;
	private boolean harddrop = false;
	
	/**
	 * Alustaa Game-luokan
	 * @param jump Yhden ruudun koko
	 * @param name Pelaajan sy�tt�m� nimi
	 */
	
 	public Game(double jump, String name) {
 		
		this.jump = jump;
		this.height = jump*20;
		this.width = jump*10;
		
		player = new Player(name);
		
		gameleft = new Pane();
		info = new Info();

		
		gameleft.getStyleClass().add("bgleft");
		info.getStyleClass().add("bgright");
		
		//Luodaan ruudukon pystyrivit
		for(int i = 0; i < 10; i++) {
			Line line = new Line(i*jump,0,i*jump,height);
			line.getStyleClass().add("grid");
			gameleft.getChildren().add(line);
		}
		
		//Luodaan ruudukon vaakarivit
		for(int i = 0; i < 20; i++) {
			Line line = new Line(0,i*jump,width,i*jump);
			line.getStyleClass().add("grid");
			gameleft.getChildren().add(line);
		}
		
		highlight = new Rectangle(0,0,0,height);
		highlight.getStyleClass().add("highlight");
		
		gameleft.getChildren().add(highlight);
				
		newBlock();
		
		gameleft.setMinWidth(width);
		info.setMinWidth(width/2);
		
		gameView.getChildren().addAll(gameleft, info);
		
		updateSpeed();
		
	}
 	
 	/**
 	 * Kertoo onko peli k�ynniss�.
 	 * @return Palauttaa pelin nykyisen tilan.
 	 */
 	public boolean isRunning() {
 		return this.running;
 	}
 	
 	/**
 	 * Pelin n�kym�n palautus
 	 * @return Palauttaa pelin n�kym�n
 	 */
 	public HBox getGameView() {
 		return this.gameView;
 	}
	
 	/**
 	 *  Tyhjent�� nykyisen palikan sijainnin taulukosta
 	 */
	public void clearPlayer() {
		for(int i = 0; i < cblock[c][r].length;i++) {
			board[(cblock[c][r][i][0]+y)][(cblock[c][r][i][1]+x)] = 0;
		}
	}
	
	/**
	 *  Asettaa nykyisen palikan sijainnin taulukkoon
	 */
	public void setPlayer() {
		for(int i = 0; i < cblock[c][r].length;i++) {
			board[(cblock[c][r][i][0]+y)][(cblock[c][r][i][1]+x)] = 1;
		}
	}
	
	/**
	 * Piirt�� pelilaudan taulukon avulla
	 */
	public void drawBoard() {
		setPlayer();

		//Piirret�� pelaajan palikka taulukon avulla
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 10; j++) {
		
				if(board[i][j] == 1) {
					
					Rectangle player = new Rectangle(j*jump,i*jump,jump,jump);
					
					player.setFill(blockcolors[c].brighter());
					player.setStroke(Color.BLACK);
					player.setStrokeType(StrokeType.INSIDE);
					
					playerblocks.add(player);
				}
				
			}
			
		}
		gameleft.getChildren().addAll(playerblocks);		
		
	}
	
	/**
	 * K��nt�� pelaajan palikkaa my�t� tai vastap�iv��n
	 * @param turn -1 k��nt�� palikkaa vastap�iv��n 1 my�t�p�iv��n.
	 */
	public void playerRotate(int turn) {
		
		//Tarkistetaan tapahtuuko t�rm�ys ennen k��nt��
		checkCollision();
		
		
		if(cblock[c][r].length > 1) { //Jos palikalla ei ole kuin yksi k��nn�s mahdollisuus, ei suoriteta k��nn�st�, koska sille ei ole tarvetta.
			
			
			if(x < 0) {
				playerMove(2);
			} else if(x >= 8) {
				playerMove(1);
			}
			
			
			int prevr = r;
			
			if(turn == 1) {
				if(r+turn < cblock[c].length) {
					r++;
				} else {
					r = 0;
				}
			} else {
				if(r+turn < 0) {
					r = cblock[c].length -1;
				} else {
					r--;
				}
			}
		
			boolean allowrotate = true;

			
			for(int i = 0; i < cblock[c].length;i++) {
				if(cblock[c][r][i][0]+y >= 20 || cblock[c][r][i][1]+x < 0 || cblock[c][r][i][1]+x > 9) {
					allowrotate = false;
					break;
				}
				
				if(board[cblock[c][r][i][0]+y][cblock[c][r][i][1]+x] == 2) {
					allowrotate = false;
					break;
				}
			}

			if(allowrotate && c != 1 && !locking) { //Jos k��nn�kselle ei ole esteit� tehd��n k��nn�s

				for(int i = 0; i < cblock[c].length;i++) {
					board[(cblock[c][prevr][i][0]+y)][(cblock[c][prevr][i][1]+x)] = 0;
				}
				
				for(int i = 0; i < cblock[c].length;i++) {
					board[(cblock[c][r][i][0]+y)][(cblock[c][r][i][1]+x)] = 1;
				}
				
				for(int i = 0; i < playerblocks.size();i++) {
					playerblocks.get(i).setX((cblock[c][r][i][1]+x)*jump);
					playerblocks.get(i).setY((cblock[c][r][i][0]+y)*jump);
				}
				updateHighlight();
			} else {
				r = prevr;
			}
		}
		
	}
	
	/**
	 * Tiputtaa nykyisen palikan niin alas kuin mahdollista. (ns. Hard drop)
	 */
	public void drop() {
		harddrop = true;
		for(int i = 0; i < 20 && harddrop;i++) {
			playerMove(0);
		}

	}
	
	/**
	 * Liikuttaa palikkaa halutttuun suuntaan
	 * @param suunta 0 = alasp�in, 1 vasemmalle, 2 oikealle
	 */
	public void playerMove(int suunta) {
		
		//Tarkistetaan tapahtuuko t�rm�ys ennen liikkmuista
		checkCollision();
		
		//Tarkistetaan, ett� palikka pysyy siirron j�lkeen pelialueella ja palikka ei mene muiden palikoiden p��lle
		boolean allowmove = true;
		for(int i = 0; i < cblock[c][r].length;i++) {
			if((((cblock[c][r][i][1]+x)-1) < 0 && suunta == 1) || (((cblock[c][r][i][1]+x)+1) > 9 && suunta == 2)) {
				allowmove = false;
				break;
				
			} 
			
			if(!((cblock[c][r][i][1]+x)-1 < 0 || (cblock[c][r][i][1]+x)+1 > 9)) {
				if(((board[(cblock[c][r][i][0]+y)][(cblock[c][r][i][1]+x)-1] == 2 && suunta == 1) || board[(cblock[c][r][i][0]+y)][(cblock[c][r][i][1]+x)+1] == 2 && suunta == 2)) {
					allowmove = false;
					break;
				}
			} 
			
			if(cblock[c][r][i][0]+y+1 >= 20 && suunta == 0) {
				allowmove = false;
				break;
			}
				
		}
		
		
		
		if(allowmove && !(locking && suunta == 0)) { //Jos siirrolle ei ole esteit� suoritetaan siirto.
			clearPlayer();
			
			switch(suunta) {
			
			case 0: 
				for(Rectangle block : playerblocks) {
					block.setY(block.getY()+jump);
				}
				y++;
				break;
			
			case 1:
				for(Rectangle block : playerblocks) {
					block.setX(block.getX()-jump);
				}
				x--;
				break;
				
			case 2:
				
				for(Rectangle block : playerblocks) {
					block.setX(block.getX()+jump);
				}
				x++;
				
				break;
			}
			
			setPlayer();
			updateHighlight();
		}
		


		
	}
	
	/**
	 * Tarkistaa tapahtuuko pelaajan ja muiden palikoiden/pelin alustan kanssa t�rm�ys.
	 * Jos t�rm�ys tapahtuu Siirret��n pelaajan palikka lockedblocks listaan ja tarkistetaan pystyt��nk� peli� jatkamaan.
	 */
	public void checkCollision() {
		
		boolean collision = false;	//Tapahtuuko t�rm�ys lattiaan tai muuhun pelaajaan
		
		for(int i = 0; i < cblock[c][r].length;i++) {
			if(cblock[c][r][i][0]+y == 19) {
				collision = true;
				break;
				
			} else if(cblock[c][r][i][0]+y < 19) {
				if(board[((cblock[c][r][i][0]+y)+1)][(cblock[c][r][i][1]+x)] == 2) {
					collision = true;
					break;
					
				}
			}
		}
		
		
		if(collision && !locking) { //Jos t�rm�ys tapahtuu k�sitell��n se
			
			if(harddrop) {	//Jos pelaaja tiputtaa palikan v�lily�nnill� suoraan alas ei k�ynnistet� lukitusviivett�
				handleCollision();
				harddrop = false;
				
			} else {
				locking = true;
				drop.stop();
				
				lockblock = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
		            public void handle(ActionEvent e) {
		            	handleCollision();	
		            	locking = false;
		            	drop.play();
		            }
		        }));
				lockblock.play();
			}
			
		}
	}
	
	public void handleCollision() {

		
		boolean collision = false;	//Tapahtuuko t�rm�ys lattiaan tai muuhun pelaajaan
		for(int i = 0; i < cblock[c][r].length;i++) {
			if(cblock[c][r][i][0]+y == 19) {
				collision = true;
				break;
				
			} else if(cblock[c][r][i][0]+y < 19) {
				if(board[((cblock[c][r][i][0]+y)+1)][(cblock[c][r][i][1]+x)] == 2) {
					collision = true;
					break;
					
				}
			}
		}
		
		if(collision) {
			lockedblocks.addAll(playerblocks);
			
			for(int i = 0; i < cblock[c][r].length;i++) {
				board[(cblock[c][r][i][0]+y)][(cblock[c][r][i][1]+x)] = 2;
			}
			
			checkLines();
			newBlock();
		}
	}

	
	/**
	 * Tarkistaa onko t�ysi� rivej� muodostunut.
	 * Jos t�ysi� rivej� on, k�sitell��n ne ja siirret�� yl�puolella olevia palikoita alasp�in
	 * Lopuksi lis�t��n tyhjennetyt rivit Player luokkaan ja tarkistetaan saatiikon tarpeeksi pisteit� seuraavaa tasoa varten.
	 */
	public void checkLines() {
		
		ArrayList<Integer> lines = new ArrayList<Integer>();

		do {
			lines.clear();
			
			for(int i = 19; i > 0; i--) {
				boolean fullline = true;
				
				for(int j = 0; j < 10; j++) {
					if(board[i][j] != 2) {
						fullline  = false;
						break;
					}
				}
				
				if(fullline) {
					lines.add(i);
				}
			}
			
			if(lines.size() > 0) {
				ArrayList<Rectangle> removed = new ArrayList<Rectangle>();

				for(int line : lines) {
					for(Rectangle block : lockedblocks) {
						
							if(block.getY() == line*jump) {
								removed.add(block);
							} else if(block.getY() < line*jump) {
								block.setY(block.getY()+jump);
							}

						}
						
					for(int i = line; i > 1; i--) {
						for(int j = 0; j < 10; j++) {
							board[i][j] = board[i-1][j]; 
						} 
					}
				}
				
				
				lockedblocks.removeAll(removed);
				gameleft.getChildren().removeAll(removed);
				
				//Kasvatetaan pisteit� ja tarvittaessa tasoa.
				player.scoreIncrease(lines.size());
				if(player.checkLevel()) {
					updateSpeed();
				}
				
				
				((Info) info).updateScores(); //P�vitet��n info-luokan pisteet n�kyviin.
				
				
			}
		} while(lines.size() > 0);
		
	}
	
	/**
	 * Arpoo seuraavan palikan muodon
	 * @return Paluttaa seuraavan palikan muodon kokonaisluvun
	 */
	
	public int nextBlock() {
		Random rnd = new Random();
		return rnd.nextInt(7);
	}
	
	/**
	 * Luo uuden palikan
	 */
	public void newBlock() {
		if(isRunning()) {
			playerblocks.clear();
			
			if(blocks == 0) {
				y = 1;
			} else {
				y = 0;
			}
			
			blocks++;
			c = nextb;
			
			r = 0;
			
			if(c == 0 || c == 1 || c == 3) {
				x = 3;
			
			} else {
				x = 4;
			}
			
			boolean gameover = false;
			if(blocks != 0) {
				for(int i = 0; i < cblock[c][0].length;i++) {
					if(board[cblock[c][0][i][0]+y][cblock[c][0][i][1]+x] == 2) {
						gameover = true;
						break;
					}
				}
			}
			
			if(!gameover) {
				nextb = nextBlock();
				
				((Info) info).updateNextBlock(cblock[nextb][0], blockcolors[nextb], nextb);
				
				updateHighlight();
				drawBoard();
			} else {
				gameOver();
			}
		}
	}
	
	/**
	 * P�ivitt�� haamupalikan ja haamupylv��n.
	 * Suoritetaan aina kun pelaaja liikkuu, k��nt�� palikkaa tai kun luodaan uusi palikka.
	 */
	public void updateHighlight() {

		int offset = 0;
		
		//Palikan muotokohtaiset m��rittelyt, jotta palikka saadaan oikealle paikalle.
		if(c == 0 && (r == 0 || r == 2)) {
			offset = -1;
		} else if(c == 0 && r == 1 || c == 4 && r == 0 || c == 5 && r == 1 || c == 2 && r == 1 ||  c == 3 && r == 3 || c == 6 && r == 1) {
			offset = 1;
		} 
		
		//Tarkistetaan millon palikka koskee muita palikoita
		int ypos = 17+offset;
		
		out: {
			for(int i = 0; i < 17+offset;i++) {
				for(int j = 0; j < cblock[c][r].length;j++) {
					if(board[(cblock[c][r][j][0]+i+1)][(cblock[c][r][j][1]+x)] == 2) {
						ypos = i;
						break out;
					}
				}	
			}
		
		}
	
		if(!ghostblocks.isEmpty()) { //Jos haamu on jo olemassa, p�viitet��n sit�, muuten luodaan uusi haamu.
			for(int i = 0; i < ghostblocks.size();i++) {
				ghostblocks.get(i).setX((cblock[c][r][i][1]+x)*jump);
				ghostblocks.get(i).setY((cblock[c][r][i][0]+ypos)*jump);
				ghostblocks.get(i).setStroke(Color.BLACK);
				ghostblocks.get(i).setFill(blockcolors[c].brighter());
				ghostblocks.get(i).setOpacity(0.3);
				
			}
			
			
		} else {
			for(int i = 0; i < cblock[c][r].length;i++) {
				
				Rectangle rect = new Rectangle((cblock[c][r][i][1]+x)*jump,(cblock[c][r][i][0]+ypos)*jump,jump,jump);
				ghostblocks.add(rect);
				rect.setStroke(Color.BLACK);
				rect.setStrokeWidth(2);
				rect.setFill(blockcolors[c].brighter());
				rect.setOpacity(0.3);
				
				
			}
			gameleft.getChildren().addAll(ghostblocks);
		}
		
		//P�ivitet��n haamupylv��n sijainti ja koko.
		double lx = 10,rx = -10;
		for(int i = 0; i < cblock[c][r].length;i++) {
				double xblock = cblock[c][r][i][1]+x;
				
				if(xblock < lx) {
					lx = xblock;
				}
				
				if(xblock > rx) {
					rx = xblock;
				}
		}
			
		highlight.setX(lx*jump);
		highlight.setWidth(rx*jump+jump-lx*jump);
		
	}
	
	
	/**
	 * P�ivitt�� pelin nopeuden nykyisen tason mukaiseksi.
	 */
	public void updateSpeed() {
		int level = player.getLevel();
		int frames = 48;
		
		if(level < 10) {
			switch(level) {
				case 0: frames = 48;break;
				case 1: frames = 43;break;
				case 2: frames = 38;break;
				case 3: frames = 33;break;
				case 4: frames = 28;break;
				case 5: frames = 23;break;
				case 6: frames = 18;break;
				case 7: frames = 13;break;
				case 8: frames = 8;break;
				case 9: frames = 6;break;
			}
		} else {
			if(level < 13) frames = 5;
			else if(level < 16) frames = 4;
			else if(level < 19) frames = 3;
			else if(level < 29) frames = 2;
			else frames = 1;
		}
		
		if(drop != null) {
			drop.stop();
			drop = null;
		}
		
		//P�ivitet��n tippuvan palikan nopeus
		drop = new Timeline(new KeyFrame(Duration.millis(1000/60*frames), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
               playerMove(0);
            }
        }));
	 
		drop.setCycleCount(Timeline.INDEFINITE);
		drop.play();
		
	}
	
	/**
	 * Peli p��ttyy. Asetetaan peli p��ttyneeksi ja n�ytet��n peli p��ttyi viesti GameOver-luokan avulla.
	 */
	public void gameOver() {
		
		if(isRunning()) {
	 		this.running = false;
	 		drop.stop();
	 		
	 		gameleft.getChildren().add(new GameOver());
	 		
	 		Player.saveScore(); //Tallennetaan pisteet talteen.
		}
	}
	
}
