package application;

import javafx.scene.paint.Color;

/**
 * Sis‰lt‰‰ palikoiden muodot, v‰rit ja asetukset
 * @author Mikko Suhonen
 *
 */
public class Tetrominos {
	
	//Palikoiden v‰rit
	public static Color[] blockcolors = new Color[] {Color.CYAN,Color.YELLOW, Color.ORANGE, Color.BLUE, Color.PURPLE, Color.RED, Color.GREEN}; 
	
	//Palikoiden yl‰ ja vasen siirtym‰t
	public static int[][] blockoffsets = {{0,1},{1,1},{0,0},{0,1},{0,0},{0,0},{0,0}}; //Palikoiden yl‰reunan ja vasemmanreunan et‰isyys reunasta. Tarvitaan, ett‰ voidaan keskitt‰‰ palikat.

	//Palikoiden umodott
	public static int[][][][] cblock = new int[][][][] {
		{
			//Line
			{{0,1},{1,1},{2,1},{3,1}},
			{{1,0},{1,1},{1,2},{1,3}},
			{{0,2},{1,2},{2,2},{3,2}},
			{{2,0},{2,1},{2,2},{2,3}}
			
		},{
			
			//Square
			{{1,1},{1,2},{2,1},{2,2}}
			
			//L-Shape
		}, {
			{{0,0},{0,1},{1,1},{2,1}},
			{{0,2},{1,0},{1,1},{1,2}},
			{{0,1},{1,1},{2,1},{2,2}},
			{{1,0},{2,0},{1,1},{1,2}}
			
			//J-Shape
		}, {
			{{0,2},{0,1},{1,1},{2,1}},
			{{1,0},{1,1},{1,2},{2,2}},
			{{0,1},{1,1},{2,1},{2,0}},
			{{1,0},{0,0},{1,1},{1,2}}
		
			//T
		}, {
			{{1,1},{1,0},{0,1},{1,2}},
			{{1,1},{2,1},{0,1},{1,2}},
			{{1,1},{2,1},{1,0},{1,2}},
			{{1,1},{2,1},{1,0},{0,1}}
			
			//Z-shape
		}, {
			{{1,0},{2,0},{0,1},{1,1}},
			{{0,0},{0,1},{1,1},{1,2}},
			{{0,2},{1,2},{1,1},{2,1}},
			{{1,0},{1,1},{2,1},{2,2}}
		}, {
			//S-shape
			{{0,0},{1,0},{1,1},{2,1}},
			{{0,2},{1,0},{1,1},{0,1}},
			{{0,1},{1,2},{1,1},{2,2}},
			{{1,2},{1,1},{2,1},{2,0}}
		}
	};
}
