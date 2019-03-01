package application;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Pelaaja luokka, johon tallennetaan nykyisen pelaajan tiedot ja jota k‰ytet‰‰n aikaisempien pelaajien hakemiseen
 * @author Mikko
 *
 */
public class Player implements Serializable {
	
	private static final long serialVersionUID = -1875069674452572918L;
	private static List<Player> players = new ArrayList<Player>();
	
	private String name;

	//Lines cleared
	private int level;
	private int lines;
	private int score;
	
	public Player(String name) {
		this.name = name;
		
		if(players.isEmpty()) {
			readPlayers();
		}
		
	}
	
	/**
	 *  Lis‰‰ pisteit‰ tyhjennettyjen rivien ja nykyisen tason mukaan. Lopuksi lis‰t‰‰n uusien tyhjennettyjen rivien m‰‰r‰ kaikkien rivien tyhjennettyyn m‰‰r‰‰n
	 * @param lines tyhjennettyjen rivien m‰‰r‰
	 */
	public void scoreIncrease(int lines) {
		switch(lines) {
		default:
		case 1: 
			score += 40 * (level + 1);
			break;
		case 2:
			score += 100 * (level + 1);
			break;
		case 3:
			score += 300 * (level + 1);
			break;
		case 4:
			score += 1200 * (level + 1);
			break;
		}
		
		this.lines += lines;
		
	}
	
	/**
	 * Tarkistaa onko pelaajalla tarpeeksi rivej‰ tyhjennetty, ett‰ voidaan edet‰ seuraavalle tasolle.
	 * @return Onko rivej‰ tarpeeksi
	 */
	public boolean checkLevel() {
		
		boolean levelup = false;
		if((this.level+1)*10 <= this.lines) {
			this.level++;
			levelup = true;
		}
		
		return levelup;
	}

	/** Pelaajan nimen palautus
	 * @return Palauttaa pelaajan nimen
	 */
	public String getName() {
		return this.name;
	}
	
	/** Pelaajan pisteiden palautus
	 * @return Palauttaa pelaajan pistem‰‰r‰n
	 */
	public double getScore() {
		return this.score;
	}
	
	/** Pelaajan tyhjennettyjen rivien m‰‰r‰
	 * @return Palauttaa tyhjennettyjen rivien m‰‰r‰n
	 */
	public int getLines() {
		return this.lines;
	}
	
	/** Pelaajan taso
	 * @return Palauttaa tason
	 */
	public int getLevel() {
		return this.level;
	}
	
	
	/** Players-listan palauuts
	 * @return Palauttaa listan, jossa on kaikki pelaajat
	 */
	public static List<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Tallentaa pelaajien oliot "Highscores.dat"-tiedostoon, jos pelaajia on alle 10 tai, jos huonoimman pelaajan pistem‰‰r‰ on huonompi kuin nykyisen pelaajan saamat pisteet
	 * Lista on j‰rjestty paremmuus j‰rjestykseen aikaiisemmin, joten listan 9 alkio on aina huonoin pelaaja.
	 * @return Onnistuiko tallennus
	 */
	public static boolean saveScore() {
		boolean success = false, save = false;
		if(players.size() >= 10) {
			if(players.get(9).getScore() < Game.player.getScore()) {
				players.set(9, Game.player);
				save = true;
			}
		} else {
			save = true;
			players.add(Game.player);
		}
		
		if(save) {
			ObjectOutputStream oos = null;
			
			try{
				oos = new ObjectOutputStream(new FileOutputStream("C:/temp/Tetris/Highscores.dat"));
				oos.writeObject(players);
				oos.close();
				
			    success = true;
			    
			} catch (Exception e) {
			    e.printStackTrace();
			} finally {
			    if(oos  != null){
			        try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    } 
			    
			}
		} else {
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Hakee pelaajien oliot "Highscores.dat"-tiedostosta ja tallentaa ne players-listaan.
	 */
	@SuppressWarnings("unchecked")
	public static void readPlayers() {
		
		Path path = Paths.get("C:/temp/Tetris/Highscores.dat");
		
		if(!Files.exists(path)) {
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if(new File(path.toString()).length() != 0) {
			ObjectInputStream ois = null;
			
			try {
			    ois = new ObjectInputStream(new FileInputStream("C:/temp/Tetris/Highscores.dat"));
			    
			    players = (List<Player>)ois.readObject();
			    
			    ois.close();
			    
			} catch (EOFException e) {
			    
			    
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
			    if(ois != null){
			    	try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    } 
			}
		}
		
	}	
	/**
	 * J‰rjest‰‰ players-listan paremmuus j‰rjestykseen pisteiden mukaan.
	 */
	public static void sortPlayers() {
		if(!players.isEmpty()) {
			List<Player> temp = new ArrayList<Player>();

			while(players.size() != 0) {
				double max = players.get(0).getScore();
				int maxi = 0;
				
				for(int i = 0; i < players.size(); i++) {
					if(players.get(i).getScore() > max) {
						max = players.get(i).getScore();
						maxi = i;
					} 
				}
				temp.add(players.get(maxi));	//Lis‰t‰‰n suurimman pistem‰‰r‰n saanut pelaaja v‰liaikaiseen listaan
				players.remove(players.get(maxi));	//Poistetaan suurin pistem‰‰r‰ alkuper‰isest‰ listasta
			}
			
			players = temp;
		}
	}
}

