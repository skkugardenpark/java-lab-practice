package pa2;

import java.util.*;
import java.io.*;

class ModeInputException extends Exception {
    ModeInputException() {
    	System.out.println("ModeInputException");
    }
}

class BombInputException extends Exception {
    BombInputException() {
    	System.out.println("BombInputException");
    }
}

class HitException extends Exception {
	HitException() {
		System.out.println("Try again");
	}
}

class Ship {
    int size;
    String type;
    boolean isHit;

    Ship(int size, String type) {
        this.size = size;
        this.type = type;
        this.isHit = false;
    }

    void setHit() {
        isHit = true;
    }

    boolean getHit() {
        return isHit;
    }

    String getType() {
        if (this.isHit) {
            return " X" + this.type.toLowerCase();
        } 
        else {
            return " " + this.type.toLowerCase() + " ";
        }
    }

    int getSize() {
        return size;
    }
}

class A extends Ship {
    A() {
        super(6, "A");
    }
}

class B extends Ship {
    B() {
        super(4, "B");
    }
}

class S extends Ship {
    S() {
        super(3, "S");
    }
}

class D extends Ship {
    D() {
        super(3, "D");
    }
}

class P extends Ship {
    P() {
        super(2, "P");
    }
}

class Board {
    static String[][] Board = new String[10][10];
    static String NameOfFile;

    Board(String NameOfFile) {
        this.NameOfFile = NameOfFile;
    }

    Board() {}

    String[][] getBoard() throws FileNotFoundException {
        Scanner scn = new Scanner(new File(NameOfFile));
        for (int i = 0; i < 10; i++) {
            String line = scn.nextLine();
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                
                switch (c) {
                    case 'A':
                        Board[i][j] = " A ";
                        break;
                    case 'B':
                        Board[i][j] = " B ";
                        break;
                    case 'S':
                        Board[i][j] = " S ";
                        break;
                    case 'D':
                        Board[i][j] = " D ";
                        break;
                    case 'P':
                        Board[i][j] = " P ";
                        break;
                    default:
                        break;
                }
            }
        }
        for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (Board[i][j] == null) {
					Board[i][j] = "   ";
				}
			}
		}
		
        
        scn.close();
        
        return Board;
    }
    
    Ship[][] getShip() {
        Ship[][] ship = new Ship[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (Board[i][j] != null && Board[i][j].equals(" A ")) {
                    ship[i][j] = new A();
                }
                else if (Board[i][j] != null && Board[i][j].equals(" B ")) {
                    ship[i][j] = new B();
                }
                else if (Board[i][j] != null && Board[i][j].equals(" S ")) {
                    ship[i][j] = new S();
                }
                else if (Board[i][j] != null && Board[i][j].equals(" D ")) {
                    ship[i][j] = new D();
                }
                else if (Board[i][j] != null && Board[i][j].equals(" P ")) {
                    ship[i][j] = new P();
                }
            }
        }
        return ship;
    }

    static String[][] generateRandomBoard() {
    	for (int i = 0; i < 10; i++) {
    		for (int j = 0; j < 10; j++) {
    			Board[i][j] = "  ";
    		}
    	}
    	
        Random random = new Random();
        
        for (String[] row : Board) {
            Arrays.fill(row, "   ");
        }
        
        int[] sizes = {6, 4, 3, 3, 2}; 
        int[] counts = {1, 2, 2, 1, 4}; 
        String[] types = {" A ", " B ", " S ", " D ", " P "};
        
        for (int index = 0; index < sizes.length; index++) {
            int shipSize = sizes[index];
            String shipType = types[index];
            int count = random.nextInt(counts[index] + 1);
            
            for (int i = 0; i < count; i++) {
                boolean placed = false;
                
                while (!placed) {
                    int row = random.nextInt(10);
                    int col = random.nextInt(10);
                    boolean horizontal = random.nextBoolean();
                    
                    if (canPlaceShip(row, col, shipSize, horizontal, Board)) {
                        for (int j = 0; j < shipSize; j++) {
                            int r = horizontal ? row : row + j;
                            int c = horizontal ? col + j : col;
                            
                            Board[r][c] = shipType;
                        }
                        placed = true;
                    }
                }
            }
        }
        return Board;
    }

    static boolean canPlaceShip(int row, int col, int size, boolean horizontal, String[][] board) {
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;

            if (r >= 10 || c >= 10 || !board[r][c].equals("   ")) {
                return false;
            }
            
            if (!checkAdjacentCells(r, c, board)) {
                return false;
            }
        }
        return true;
    }

    static boolean checkAdjacentCells(int row, int col, String[][] board) {
        int[] dRow = {-1, 1, 0, 0, -1, -1, 1, 1, 0};
        int[] dCol = {0, 0, -1, 1, -1, 1, -1, 1, 0};

        for (int i = 0; i < dRow.length; i++) {
            int adjRow = row + dRow[i];
            int adjCol = col + dCol[i];
            
            if (adjRow >= 0 && adjRow < 10 && adjCol >= 0 && adjCol < 10) {
                if (!board[adjRow][adjCol].equals("   ")) {
                    return false;
                }
            }
        }
        return true;
    }

}

class Game {
    Board newGame;
    String[][] Board = new String[10][10];
    Ship[][] Ship = new Ship[10][10];
    char Mode;
    int NumberOfBombs;

    Game(int NumberOfBombs, char Mode, String NameOfFiles) throws ModeInputException, FileNotFoundException, HitException {
        newGame = new Board(NameOfFiles);
        this.Mode = Mode;
        this.NumberOfBombs = NumberOfBombs;
        Board = newGame.getBoard();
        Ship = newGame.getShip();

        if (Mode == 'd' || Mode == 'D') {
            DebugMode();
        } 
        
        else if (Mode == 'r' || Mode == 'R') {
        	ReleaseMode();
        } 
        
        else {
            throw new ModeInputException();
        }
    }

    Game(int NumberOfBombs, char Mode) throws ModeInputException, HitException {
        newGame = new Board();
        this.Mode = Mode;
        this.NumberOfBombs = NumberOfBombs;
        this.Board = newGame.generateRandomBoard();
        this.Ship = newGame.getShip();

        if (Mode == 'd' || Mode == 'D') {
            DebugMode();
        } 
        else if (Mode == 'r' || Mode == 'R') {
        	ReleaseMode();	
        } 
        else {
            throw new ModeInputException();
        }
    }

    void printBoard() {
        System.out.println("     A  B  C  D  E  F  G  H  I  J  ");
        System.out.println("     -  -  -  -  -  -  -  -  -  -  ");
        for (int i = 0; i < 10; i++) {
            System.out.printf("%-2d |", i + 1);
            
            for (int j = 0; j < 10; j++) {
                System.out.printf("%s", Board[i][j]);
            }
            
            System.out.println();
        }
    }
    
    void DebugMode() throws HitException {
    	int score = 0;
    	int[] coordinate = new int[2];
    	ArrayList<String> AlreadyShot = new ArrayList<>();
    	
    	while (NumberOfBombs > 0) {
    		try {
    			Scanner scn = new Scanner(System.in);
        		String str = scn.nextLine();
        		
        		if(AlreadyShot.contains(str)) {
        			throw new HitException();
        		}
        		
        		AlreadyShot.add(str);
        		
        		String[] input = new String[2];
        		input[0] = str.substring(0, 1);
        		input[1] = str.substring(1);
        		
        		coordinate[0] = input[0].toUpperCase().charAt(0) - 'A';
        		coordinate[1] = Integer.parseInt(input[1]) - 1;
    		}
    		
    		catch (HitException h) {
    			continue;
    		}
    		
    		if (Board[coordinate[1]][coordinate[0]] == " A ") {
    			System.out.println("Hit A");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    			printBoard();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " B ") {
    			System.out.println("Hit B");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    			printBoard();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " S ") {
    			System.out.println("Hit S");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    			printBoard();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " D ") {
    			System.out.println("Hit D");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    			printBoard();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " P ") {
    			System.out.println("Hit P");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    			printBoard();
    		}
    		else {
    			System.out.println("Miss");
    			Board[coordinate[1]][coordinate[0]] = " X ";
    			printBoard();
    		}
    		
    		NumberOfBombs--;
    	}
    	System.out.println("Score " + score);
    }
    
    
    void ReleaseMode() throws HitException {
    	int score = 0;
    	int[] coordinate = new int[2];
    	ArrayList<String> AlreadyShot = new ArrayList<>();
    	
    	while (NumberOfBombs > 0) {
    		try {
    			Scanner scn = new Scanner(System.in);
        		String str = scn.nextLine();
        		
        		if(AlreadyShot.contains(str)) {
        			throw new HitException();
        		}
        		
        		AlreadyShot.add(str);
        		
        		String[] input = new String[2];
        		input[0] = str.substring(0, 1);
        		input[1] = str.substring(1);
        		
        		coordinate[0] = input[0].toUpperCase().charAt(0) - 'A';
        		coordinate[1] = Integer.parseInt(input[1]) - 1;
    		}
    		
    		catch (HitException h) {
    			continue;
    		}
    		
    		if (Board[coordinate[1]][coordinate[0]] == " A ") {
    			System.out.println("Hit A");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " B ") {
    			System.out.println("Hit B");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " S ") {
    			System.out.println("Hit S");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " D ") {
    			System.out.println("Hit D");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    		}
    		else if (Board[coordinate[1]][coordinate[0]] == " P ") {
    			System.out.println("Hit P");
    			Ship[coordinate[1]][coordinate[0]].setHit();
    			Board[coordinate[1]][coordinate[0]] = Ship[coordinate[1]][coordinate[0]].getType();
    			score += Ship[coordinate[1]][coordinate[0]].getSize();
    		}
    		else {
    			System.out.println("Miss");
    			Board[coordinate[1]][coordinate[0]] = " X ";
    		}
    		
    		NumberOfBombs--;
    	}
    	
    	printBoard();
    	System.out.println("Score " + score);
    }
}

public class Battleship {

    public static void main(String[] args) throws ModeInputException, BombInputException, FileNotFoundException, HitException {
        Scanner scn = new Scanner(System.in);
        String str = scn.nextLine();
        String[] input = str.split(" ", 3);
        int NumberOfBombs = Integer.parseInt(input[0]);
        
        if (NumberOfBombs <= 0) {
            throw new BombInputException();
        }

        char Mode = input[1].charAt(0);

        if (input.length == 3) {
            String NameOfFile = input[2];
            File f = new File(NameOfFile);
            
            if (f.exists()) {
                Game withFile = new Game(NumberOfBombs, Mode, NameOfFile);
            } 
            else {
                Game withoutFile = new Game(NumberOfBombs, Mode);
            }
        } 
        else {
            Game withoutFile = new Game(NumberOfBombs, Mode);
        }
    }
}