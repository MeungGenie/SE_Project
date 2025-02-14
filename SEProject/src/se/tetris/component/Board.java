package se.tetris.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.crypto.Data;

import se.tetris.blocks.Block;
import se.tetris.blocks.IBlock;
import se.tetris.blocks.JBlock;
import se.tetris.blocks.LBlock;
import se.tetris.blocks.OBlock;
import se.tetris.blocks.SBlock;
import se.tetris.blocks.TBlock;
import se.tetris.blocks.ZBlock;

import se.tetris.setting.SettingCode;
import se.tetris.setting.SettingValues;
import se.tetris.data.*;

public class Board extends JFrame {
	
	public static Board boardMain;
	private static final long serialVersionUID = 2434035659171694595L;
	
	public static final int HEIGHT = 20;
	public static final int WIDTH = 10;
	public static final char BORDER_CHAR = 'X';
	
	double min;
	double max;
	double percentage;
	double weighted;
	Random rnd;
	int block;
	
	DBCalls dataCalls = new DBCalls();
	
	private static JTextPane tetrisArea;
	private static JTextPane nextArea;
	private JPanel panel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private static JPanel scorePanel;
	private static JPanel levelPanel;
	private static int[][] board;
	private static int[][] nextBoard;
	private KeyListener playerKeyListener;
	private static SimpleAttributeSet stylesetBr;
	private static SimpleAttributeSet stylesetNx;
	private static SimpleAttributeSet stylesetCur;
	private static StyledDocument boardDoc;
	private static StyledDocument nextDoc;
	public static Timer timer;
	private static Block curr;
	private static Block next;
	static int x = 3; //Default Position.
	static int y = 0;
	int nextX = 1;
	int nextY = 1;
	public static int score = 0;
	public static int level = 0;
	
	public static int mode = 0;
	int eraseCnt = 0;



	//initInterval 난이도에 따라 조절
	//public static int initEasyInterval = 2000;
	//public static int initNormalInterval = 1000;
	//public static int initHardInterval = 500;
	final SettingValues setting = SettingValues.getInstance();
	int intervalByMode = setting.intervalNumber;

	//만들어진 블럭 개수 세기
	private static int blockNumber = 0;
	
	ScoreItem scoreItem = new ScoreItem();
	
	static JLabel scoreLb1 = new JLabel("Scores");
	static JLabel scoreLb2 = new JLabel(Integer.toString(score));
	static JLabel levelLb1 = new JLabel("Level");
	static JLabel levelLb2 = new JLabel(Integer.toString(level));

	
	
	
	public Board() {
		
		super("SeoulTech SE Tetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Board display setting.
		tetrisArea = new JTextPane();
		tetrisArea.setEditable(false);
		tetrisArea.setBackground(Color.BLACK);
		CompoundBorder border = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY, 10),
				BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
		tetrisArea.setBorder(border);
		
		nextArea = new JTextPane();
		nextArea.setEditable(false);
		nextArea.setBackground(Color.BLACK);
		nextArea.setBorder(border);
		nextArea.setPreferredSize(new Dimension(150, 200));
		
		scorePanel = new JPanel();
		EtchedBorder scoreBorder = new EtchedBorder();
		scorePanel.setBorder(scoreBorder);
		scorePanel.setPreferredSize(new Dimension(150, 50));
		

		scoreLb1.setForeground(Color.darkGray);
		scoreLb1.setAlignmentX(CENTER_ALIGNMENT);
		
		scoreLb2.setForeground(Color.RED);
		
		scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
		scorePanel.add(scoreLb1);
		scorePanel.add(Box.createVerticalStrut(5));
		scorePanel.add(scoreLb2);
		
		
		levelPanel = new JPanel();
		levelPanel.setBorder(scoreBorder);
		levelPanel.setPreferredSize(new Dimension(150, 50));
		
		mode = dataCalls.getLevelSetting();
	
		levelLb1.setForeground(Color.darkGray);
		levelLb1.setAlignmentX(CENTER_ALIGNMENT);
		
		levelLb2.setForeground(Color.BLUE);
		levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.Y_AXIS));
		levelPanel.add(levelLb1);
		levelPanel.add(Box.createVerticalStrut(5));
		levelPanel.add(levelLb2);
		
		leftPanel = new JPanel();
		leftPanel.add(tetrisArea);
		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(nextArea);
		rightPanel.add(Box.createVerticalStrut(20));
		rightPanel.add(scorePanel);
		rightPanel.add(Box.createVerticalStrut(20));
		rightPanel.add(levelPanel);
		
		panel = new JPanel();
		panel.add(leftPanel);
		panel.add(rightPanel);
		
		add(panel);

		//Set timer for block drops.
		timer = new Timer(getInterval(blockNumber, eraseCnt), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveDown();
			}
		});
		
		//Initialize board for the game.
		board = new int[HEIGHT][WIDTH];
		nextBoard = new int[4][5];
		playerKeyListener = new PlayerKeyListener();
		addKeyListener(playerKeyListener);
		setFocusable(true);
		requestFocus();
		
		//Create the first block and draw
		curr = getRandomBlock(setting.modeChoose);
		next = getRandomBlock(setting.modeChoose);
		
		//Document default style.
		stylesetBr = new SimpleAttributeSet();
		StyleConstants.setFontSize(stylesetBr, 20);
		StyleConstants.setFontFamily(stylesetBr, "Courier New");
		StyleConstants.setBold(stylesetBr, true);
		StyleConstants.setForeground(stylesetBr, Color.WHITE);
		StyleConstants.setAlignment(stylesetBr, StyleConstants.ALIGN_CENTER);
		
		stylesetCur = new SimpleAttributeSet();
		StyleConstants.setFontSize(stylesetCur, 20);
		StyleConstants.setFontFamily(stylesetCur, "Courier New");
		StyleConstants.setBold(stylesetCur, true);
		StyleConstants.setAlignment(stylesetCur, StyleConstants.ALIGN_CENTER);
			
		stylesetNx = new SimpleAttributeSet();
		StyleConstants.setFontSize(stylesetNx, 25);
		StyleConstants.setFontFamily(stylesetNx, "Courier New");
		StyleConstants.setBold(stylesetNx, true);
		StyleConstants.setAlignment(stylesetNx, StyleConstants.ALIGN_CENTER);	
		
		boardDoc = tetrisArea.getStyledDocument();
		nextDoc = nextArea.getStyledDocument();

		placeBlock();
		drawBoard();
		placeNext();
		drawNext();
	
		timer.start();
	}

	private Block getRandomBlock(int modeChoose) {
		switch (modeChoose) {
			case 1:
				min = 1;
				max = 100;
				percentage = Math.random() * (max - min) + min;
				if (percentage <= (double)100 / 720 * 100 * 1.2)
					return new IBlock();
				else
				{
					rnd = new Random(System.currentTimeMillis());
					block = rnd.nextInt(6);
					switch(block) {
						case 0:
							return new JBlock();
						case 1:
							return new LBlock();
						case 2:
							return new ZBlock();
						case 3:
							return new SBlock();
						case 4:
							return new TBlock();
						case 5:
							return new OBlock();
					}
				}
			case 2:
				rnd = new Random(System.currentTimeMillis());
				block = rnd.nextInt(7);
				switch(block) {
					case 0:
						return new IBlock();
					case 1:
						return new JBlock();
					case 2:
						return new LBlock();
					case 3:
						return new ZBlock();
					case 4:
						return new SBlock();
					case 5:
						return new TBlock();
					case 6:
						return new OBlock();
				}
			case 3:
				min = 1;
				max = 100;
				percentage = Math.random() * (max - min) + min;
				if (percentage <= (double)100 / 680 * 100 * 0.8)
					return new IBlock();
				else
				{
					rnd = new Random(System.currentTimeMillis());
					block = rnd.nextInt(6);
					switch(block) {
						case 0:
							return new JBlock();
						case 1:
							return new LBlock();
						case 2:
							return new ZBlock();
						case 3:
							return new SBlock();
						case 4:
							return new TBlock();
						case 5:
							return new OBlock();
					}
				}
				break;
			default:
				break;
		}
		return new IBlock();
	}

	
	private void placeBlock() {
		for(int j=0; j<curr.height(); j++) {
			for(int i=0; i<curr.width(); i++) {
				if (curr.getShape(i, j) == 1)
					board[y+j][x+i] = curr.getShape(i, j);
			}
		}
	}
	
	private void placeNext() {
		for(int j = 0; j < next.height(); j++) {
			for(int i=0; i<next.width(); i++) {
				nextBoard[nextY+j][nextX+i] = next.getShape(i, j);
			}
		}
	}
	
	private void eraseCurr() {
		for(int i=x; i<x+curr.width(); i++) {
			for(int j=y; j<y+curr.height(); j++) {
				if(curr.getShape(i-x,j-y) == 1)
					board[j][i] = 0;
			}
		}
	}
	
	private void eraseNext() {
		for(int i = nextX; i < nextX + next.width(); i++) {
			for(int j=nextY; j< nextY + next.height(); j++) {
				nextBoard[j][i] = 0;
			}
		}
	}
	
	ArrayList<Integer> line = new ArrayList<Integer>();
	ArrayList<Integer> lineCheck() {
		ArrayList<Integer> Item = new ArrayList<Integer>();
		int count;
		for(int i = 0; i < HEIGHT; i++) {
			count = 0;
			for(int j = 0; j < WIDTH; j++)
				if(board[i][j] == 1) 
				{
					count++;
				}
					
			if(count == WIDTH) Item.add(i);
		}
		return Item;
	}
	
	public void collisionOccur() {
		saveBoard();
		curr = next;
		x = 3;
		y = 0;
		if (isGameOver() == true) {
			timer.stop();
			boolean result = scoreItem.showDialog(getNowScore(), 0 , mode);
			setVisible(result);
			//종료 화면과 잇기
		}
		else {
			eraseNext();
			next = getRandomBlock(setting.modeChoose);
			placeNext();
			drawNext();
		}
	}
 
	void lineRemove() {
		line = lineCheck();
		Iterator<Integer> iter = line.iterator();
		int index = 0;
		while(iter.hasNext()) {
			index = iter.next();
			for(int i = index; i > 1; i--) {
				for(int j = 0; j < WIDTH; j++) {
					board[i][j] = board[i-1][j];
				}
			}
			index = 0;
			eraseCnt++;
			getScore(eraseCnt, "line");
			setScore();
		}
	}
	
	public boolean collisionBottom() {
		for (int i = 0; i < curr.height(); i++) {
			for (int j = 0; j < curr.width(); j++) {
				if (y >= HEIGHT - curr.height()) return true;
				if (curr.getShape(j, i) == 1 && i + y < 19) {
					int checkBottom = board[i + y + 1][j + x];
					if (checkBottom == 1) {
						return true;
					}
				}
			}
		}
	
		return false;
	}
	
	public boolean collisionRight() {
		for (int i = 0; i < curr.height(); i++) {
			for (int j = 0; j < curr.width(); j++) {
				if (curr.getShape(j, i) == 1 & j + x < 9) {
					int checkRight = board[i + y][j + x + 1];
					if(checkRight == 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean collisionLeft() {
		for (int i = 0; i < curr.height(); i++) {
			for (int j = 0; j < curr.width(); j++) {
				if (curr.getShape(j, i) == 1 & j + x > 0) {
					int checkLeft = board[i + y][j + x - 1];
					if(checkLeft == 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void moveDown() {
		eraseCurr();	
		if (collisionBottom()) {
			collisionOccur();
		}
		else y++;
		lineRemove();
		if (!isGameOver()) {
			placeBlock();
			drawBoard();
		}
	}
	
	protected void moveRight() {
		eraseCurr();
		if(x < WIDTH - curr.width() && collisionRight() == false) x++;
		placeBlock();
	}

	protected void moveLeft() {
		eraseCurr();
		if(x > 0 && collisionLeft() == false) x--;
		placeBlock();
	}

	public void drawBoard() {
		StringBuffer sb = new StringBuffer();
		for(int t=0; t<WIDTH+2; t++) sb.append(BORDER_CHAR);
		sb.append("\n");
		for(int i=0; i < board.length; i++) {
			sb.append(BORDER_CHAR);
			for(int j=0; j < board[i].length; j++) {
				if(board[i][j] == 1) {
					sb.append("■");
				} else {
					sb.append(" ");
				}
			}
			sb.append(BORDER_CHAR);
			sb.append("\n");
		}
		for(int t=0; t<WIDTH+2; t++) sb.append(BORDER_CHAR);
		tetrisArea.setText(sb.toString());
		boardDoc.setCharacterAttributes(0, boardDoc.getLength(), stylesetBr, false);
		
		for(int j = 0; j < curr.height(); j++) {
			int rows = y+j == 0 ? 1 : y+j+1;
			int offset = rows * (WIDTH+3) + x + 1;
			for (int i = 0; i < curr.width(); i++) {
				 if (curr.getShape(i, j) == 1) {
					 colorBlindModeCurrent(offset + i);
		            }	
			}
		}
	}
	
	//blockNumber 증가 + timer 변경
		public void drawNext() {
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			blockNumber++;
			timer.setDelay(getInterval(blockNumber, eraseCnt));
			for(int i=0; i < nextBoard.length; i++) {
				for(int j=0; j < nextBoard[i].length; j++) {
					if(nextBoard[i][j] == 1) {
						sb.append("■");
					} else {
						sb.append(" ");
					}
				}
				sb.append("\n");
			}
			nextArea.setText(sb.toString());
			colorBlindModeNext();
		}

		private void colorBlindMode(SimpleAttributeSet styleSet, Block block) {
			if (setting.colorBlindModeCheck == 1) {
				StyleConstants.setForeground(styleSet, block.getColorBlind());
			} else {
				StyleConstants.setForeground(styleSet, block.getColor());
			}
		}
		private void colorBlindModeNext(){
			colorBlindMode(stylesetNx, next);
			nextDoc.setParagraphAttributes(0, nextDoc.getLength(), stylesetNx, false);
		}
		private void colorBlindModeCurrent(int offset){
			colorBlindMode(stylesetCur, curr);
			boardDoc.setCharacterAttributes(offset, 1, stylesetCur, true);
		}


		//interval 함수
		int getInterval(int blockNumber, int eraseCnt) {
			if (blockNumber == 30 || blockNumber == 60 || blockNumber == 80 || blockNumber == 100 || blockNumber == 120) {
				if (intervalByMode == 1000) {
					SettingValues.getInstance().intervalNumber *= 0.9;
					getScore(5*eraseCnt, "std");
					setScore();
					level++;
					levelLb2.setText(Integer.toString(level));
				} else if (intervalByMode == 2000) {
					SettingValues.getInstance().intervalNumber *= 0.92;
					getScore(11*eraseCnt, "std");
					setScore();
					level++;
					levelLb2.setText(Integer.toString(level));
				} else if (intervalByMode == 500) {
					SettingValues.getInstance().intervalNumber *= 0.88;
					getScore(20*eraseCnt, "std");
					setScore();
					
					level++;
					levelLb2.setText(Integer.toString(level));
				}
			}
			if (eraseCnt == 5 || eraseCnt == 10 || eraseCnt == 15 || eraseCnt == 20 || eraseCnt == 25) {
				if (intervalByMode == 1000) {
					setting.intervalNumber *= 0.9;
					getScore(3*eraseCnt, "std");
					setScore();
					level++;
					levelLb2.setText(Integer.toString(level));
				} else if (intervalByMode == 2000) {
					getScore(9*eraseCnt, "std");
					setScore();
					setting.intervalNumber *= 0.92;
					level++;
					levelLb2.setText(Integer.toString(level));
				} else if (intervalByMode == 500) {
					setting.intervalNumber *= 0.88;
					getScore(30*eraseCnt, "std");
					setScore();
					level++;
					levelLb2.setText(Integer.toString(level));
				}
			}
			System.out.println("Created : " + blockNumber + "   Removed : " + eraseCnt +"   intervalByMode" +intervalByMode + "   interval Number : " + setting.intervalNumber);
			return setting.intervalNumber;
		}

		public void reset() {
			board = new int[HEIGHT][WIDTH];
			nextBoard = new int[4][5];
			x = 3;
			y = 0;
			curr = getRandomBlock(setting.modeChoose);
			next = getRandomBlock(setting.modeChoose);
			placeBlock();
			drawBoard();
			placeNext();
			drawNext();
			this.board = new int[20][10];
		}

		public boolean collisionCheck() {
			for (int i = 0; i < curr.height(); i++) {
				for (int j = 0; j < curr.width(); j++) {
					// && j + x < 9 && j + x > 0
					if (curr.getShape(j, i) == 1 && i + y < 19) {
						//int checkLeft = board[i + y + 1][j + x - 1];
						//int checkRight = board[i + y + 1][j + x + 1];
						int checkBottom = board[i + y + 1][j + x];
						//|| checkLeft == 1 || checkRight == 1
						if (checkBottom == 1) {
							return true;
						}
					}
				}
			}

			return false;
		}
		
		public boolean startCheck() {
			for (int i = 0; i < curr.height(); i++) {
				for (int j = 0; j < curr.width(); j++)
					if(curr.getShape(j,i) != 0 && board[y + i][x + j] == 1)
						return true;
			}
			return false;
		}

		
		public boolean isGameOver() {
			if (startCheck())
				return true;
			for (int i = 0; i < WIDTH; i++) {
				if (board[0][i] == 1) {
					return true;
				}
			}
			return false;	
		}
		
		public void saveBoard() {
			for (int i = 0; i < curr.height(); i++) {
				for (int j = 0; j < curr.width(); j++) {
					if (curr.getShape(j, i) == 1) {
						board[y + i][j + x] = 1;
					}
				}
			}
		}


		protected void blockRotate() {
			eraseCurr();
			curr.rotate();
			placeBlock();
		}

		public class PlayerKeyListener implements KeyListener {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(SettingValues.getInstance().keyChoose == 1) {
				switch(e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						moveDown();
						drawBoard();
						break;
					case KeyEvent.VK_RIGHT:
						moveRight();
						drawBoard();
						break;
					case KeyEvent.VK_LEFT:
						moveLeft();
						drawBoard();
						break;
					case KeyEvent.VK_UP:
						blockRotate();
						drawBoard();
						break;
					case KeyEvent.VK_ESCAPE:
						timer.stop();
						String[] stopOption = {"Restart", "Play", "Exit"};
						int choice = JOptionPane.showOptionDialog(null, "What Do You Want?", "Stop", 0, 0, null, stopOption, stopOption[1]);
						switch (choice) {
							case 0:
								int confirm1 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
								if (confirm1 == 0) {
									reset();
									score = 0;
									level = 0;
									timer.restart();
								} else {
									timer.start();
								}
								break;
							case 1:
								timer.start();
								break;
							case 2:
								int confirm2 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
								if (confirm2 == 0) {
									dispose(); //or save score and move to score board.
								} else {
									timer.start();
								}
								break;
						}
						break;
				}
				}else if(SettingValues.getInstance().keyChoose == 2) {
					switch(e.getKeyCode()) {
						case KeyEvent.VK_S:
							moveDown();
							drawBoard();
							break;
						case KeyEvent.VK_D:
							moveRight();
							drawBoard();
							break;
						case KeyEvent.VK_A:
							moveLeft();
							drawBoard();
							break;
						case KeyEvent.VK_W:
							blockRotate();
							drawBoard();
							break;
						case KeyEvent.VK_ESCAPE:
							timer.stop();
							String[] stopOption = {"Restart", "Play", "Exit"};
							int choice = JOptionPane.showOptionDialog(null, "What Do You Want?", "Stop", 0, 0, null, stopOption, stopOption[1]);
							switch (choice) {
								case 0:
									int confirm1 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
									if (confirm1 == 0) {
										reset();
										score = 0;
										level = 0;
										timer.restart();
									} else {
										timer.start();
									}
									break;
								case 1:
									timer.start();
									break;
								case 2:
									int confirm2 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
									if (confirm2 == 0) {
										dispose(); //or save score and move to score board.
									} else {
										timer.start();
									}
									break;
							}
							break;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

		}
	
	//max - 30, default - 20,  
	public void setSize(int size) {
		StyleConstants.setFontSize(stylesetBr, size);
		StyleConstants.setFontSize(stylesetCur, size);
		StyleConstants.setFontSize(stylesetNx, size+5);
		drawBoard();
		drawNext();
	}
	
	
	//max - (200, 60), default - (150, 50) 
	public static void setRtSize(int xSize, int ySize) {
		scorePanel.setPreferredSize(new Dimension(xSize, ySize));
		levelPanel.setPreferredSize(new Dimension(xSize, ySize));
		nextArea.setPreferredSize(new Dimension(xSize, ySize * 4));
	}
	
	//max - 17, default - nothing, 
	public static void setLbSize(int size) {
		scoreLb1.setFont(new Font(null, Font.BOLD, size));
		scoreLb2.setFont(new Font(null, Font.BOLD, size));
		levelLb1.setFont(new Font(null, Font.BOLD, size));
		levelLb2.setFont(new Font(null, Font.BOLD, size));
	}
	
	
	public static Board getBoard(){
		return boardMain;
	}

	public void setScore() {
		String scoretxt = Integer.toString(score);
//				String.valueOf(score);
		String prescoretxt = scoreLb2.getText();
		System.out.println("점수 변경" + prescoretxt+"...>"+ scoretxt);
		scoreLb2.setText(scoretxt);
	}

	public void getScore(int lines, String mode) {
		int scorePre = lines;
		updateSroce(scorePre, mode);
	}
	
	public int getNowScore() {
		int score = this.score;
		return score;
	}

	public int updateSroce(int sc, String mode) {
		if(mode =="line") {
			if(sc>0 && sc<=5) {
				this.score += 10;
			}else if(sc>5 && sc<=10) {
				this.score += 15;
			}else {
				this.score += 20;
			}
			if(sc%3 ==0) {
				this.score += 3*sc;
			}
			if(sc%11 ==0) {
				this.score += 11;
			}
		}else {
			this.score += sc;
		}

		setScore();
		return score;
	}
	
}
