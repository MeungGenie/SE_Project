package se.tetris.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.setting.SettingCode;
import se.tetris.component.*;
import se.tetris.data.*;


public class Start extends JFrame {
//    private int stdclickcount = 0;
//    private int scoreclickcount = 0;
//    private int settingclickcount = 0;
	int KeyCount = 0;
	int KeyFoucus = 0;

    public static Start start;
    
    DBCalls dataCalls = new DBCalls();
    
    int Window = dataCalls.getWindowSetting();
    
    public void startStdMode() {
    	Board main = new Board();
    	
		if(Window == 0) {
			main.setSize(400,600);
		}else if(Window == 1) {
			main.setSize(800,800);
		}else {
			main.setSize(SettingCode.screenWidth, SettingCode.screenHeight);
		}

		main.setVisible(true);

        setVisible(false);
    }
    
    public void startItemMode() {
    	ItemBoard itemBoard = new ItemBoard();
    	
		if(Window == 0) {
			itemBoard.setSize(400,600);
		}else if(Window == 1) {
			itemBoard.setSize(800,800);
		}else {
			itemBoard.setSize(SettingCode.screenWidth, SettingCode.screenHeight);
		}

		itemBoard.setVisible(true);
        setVisible(false);
    }
    
    public void startScoreMode() {
    	Score scoreView = new Score();
		if(Window == 0) {
			scoreView.setSize(400,600);
		}else if(Window == 1) {
			scoreView.setSize(800,800);
		}else {
			scoreView.setSize(SettingCode.screenWidth, SettingCode.screenHeight);
		}
        scoreView.setVisible(true);
        setVisible(false);
    }
    
    public void startSettingMode() {
        SettingCode setting = new SettingCode();
		if(Window == 0) {
			setting.setSize(400,600);
		}else if(Window == 1) {
			setting.setSize(800,800);
		}else {
			setting.setSize(SettingCode.screenWidth, SettingCode.screenHeight);
		}
        setting.setVisible(true);
        setVisible(false);
    }
    
    public void startExitMode() {
    	System.exit(0);
    }



    public Start() {

        setTitle("SeoulTech SE Tettris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(7,1,0,10);
        Container startView = getContentPane();

        startView.setLayout(grid);
        startView.setBackground(Color.white);

        JLabel Title = new JLabel("SeoulTech SE Tettris");
        Title.setFont(new Font("Serif",Font.BOLD,17));
        Title.setHorizontalAlignment(JLabel.CENTER);
        Title.setVerticalAlignment(JLabel.CENTER);

        JLabel Team = new JLabel("Team one");
        Team.setHorizontalAlignment(JLabel.CENTER);
        Team.setVerticalAlignment(JLabel.CENTER);

        //JPanel btnGroup = new JPanel();
        //btnGroup.setBackground(Color.white);
        //btnGroup.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


        JButton stdBtn = new JButton("기본 모드");
        JButton itemBtn = new JButton("아이템 모드");
        JButton scoreBtn = new JButton("스코어");
        JButton settingBtn = new JButton("설정");
        JButton endBtn = new JButton("종료");
        
		
		stdBtn.setBackground(null);
		itemBtn.setBackground(null);
		scoreBtn.setBackground(null);
		settingBtn.setBackground(null);
		endBtn.setBackground(null);

        startView.add(Title);
        //startView.add(btnGroup);

        startView.add(stdBtn);
        startView.add(itemBtn);
        startView.add(scoreBtn);
        startView.add(settingBtn);
        startView.add(endBtn);

        startView.add(Team);
        


        stdBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	startStdMode();
            }
        });
        

        itemBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	startItemMode();
            }
        });
        
        
        scoreBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	startScoreMode();
            }
        });


        endBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	startExitMode();
            }
        });
        
        
        settingBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	startSettingMode();
            }
        });

        startView.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
//        		System.out.println(e);
//        		System.out.println(KeyCount);
        		
    			if(KeyCount == 0) {
        			stdBtn.setBackground(new Color(106,215,255));
        			KeyFoucus = 0;
    			}
        		

        		if(e.getKeyCode() == 40) {
        			if(KeyFoucus == 0 && KeyCount>0) {
        				itemBtn.setBackground(new Color(106,215,255));
        				stdBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 1;
        			}else if(KeyFoucus == 1) {
        				scoreBtn.setBackground(new Color(106,215,255));
        				stdBtn.setBackground(null);
        				itemBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 2;
        			}else if(KeyFoucus ==2) {
        				settingBtn.setBackground(new Color(106,215,255));
        				stdBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				itemBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 3;
        			}else if(KeyFoucus ==3) {
        				endBtn.setBackground(new Color(106,215,255));
        				stdBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				itemBtn.setBackground(null);
        				KeyFoucus = 4;
        			}else if(KeyFoucus ==4) {
        				endBtn.setBackground(new Color(106,215,255));
        				stdBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				itemBtn.setBackground(null);
        				KeyFoucus = 4;
        			}
        			KeyCount++;
        		}
        		
        		if(e.getKeyCode() == 38) {
        			if(KeyFoucus == 0) {
        				stdBtn.setBackground(new Color(106,215,255));
        				itemBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 0;
        			}else if(KeyFoucus ==1) {
        				stdBtn.setBackground(new Color(106,215,255));
        				itemBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 0;
        			}else if(KeyFoucus ==2) {
        				stdBtn.setBackground(null);
        				itemBtn.setBackground(new Color(106,215,255));
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 1;
        			}else if(KeyFoucus ==3) {
        				stdBtn.setBackground(null);
        				itemBtn.setBackground(null);
        				scoreBtn.setBackground(new Color(106,215,255));
        				settingBtn.setBackground(null);
        				endBtn.setBackground(null);
        				KeyFoucus = 2;
        			}else if(KeyFoucus ==4) {
        				stdBtn.setBackground(null);
        				itemBtn.setBackground(null);
        				scoreBtn.setBackground(null);
        				settingBtn.setBackground(new Color(106,215,255));
        				endBtn.setBackground(null);
        				KeyFoucus = 3;
        			}
        			KeyCount++;
        		}
        		
        		
        		if(e.getKeyCode() == 10) {
        			if(KeyFoucus == 0) {
        				startStdMode();
        			}else if(KeyFoucus ==1) {
        				startItemMode();
        			}else if(KeyFoucus ==2) {
        				startScoreMode();
        			}else if(KeyFoucus ==3) {
        				startSettingMode();
        			}else if(KeyFoucus ==4) {
        				startExitMode();
        			}
        		}

			}
		});


        
        startView.setFocusable(true);
    }

    public static Start getStart(){
        return start;
    }

}