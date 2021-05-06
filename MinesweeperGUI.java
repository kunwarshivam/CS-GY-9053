
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;

import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.Image;

public class MinesweeperGUI extends JFrame{

    private static JFrame frame, frame2;
    private JPanel panel, savePanel, loadPanel, mainPanel, namePanel, board;
    private JButton btnSaveGame;
    private JButton[] btnLoadGame;

    private JMenuBar menuBar;
    private JMenu menuBarOptions;
	private JMenuItem menuRestartGame, menuLoadGame, menuSaveGame, menuExit, menuTop5Game;
    private JTextField txtPlayerName = new JTextField(30);
    private JLabel lblPlayerName = new JLabel("Name: ");

    private static Minesweeper minesweeperObj = new Minesweeper();

    private ArrayList<GameList> lists;

    // GUI 
    private final int N_ROWS = 16;
    private final int N_COLS = 16;
    private final int CELL_SIZE = 15;
    private final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
    private final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 110;

	//Networking Variables
	private ObjectOutputStream clientOutputStream;
	private ObjectInputStream clientInputStream;
	private Socket sock;
    private final JLabel statusbar;
	private Board boardObj;

	private int allCells;

    public Minesweeper getGame(){
        return minesweeperObj;
    }

    public MinesweeperGUI(){
        createWindow(BOARD_WIDTH,  BOARD_HEIGHT);
        statusbar = new JLabel("");
		boardObj = new Board(statusbar, minesweeperObj);
        addMainPanel();
        addPlayerName();
        addBoard();
        frame.getContentPane().add(panel);
		frame.setVisible(true);
    }

    public void createWindow(int width, int height){
        frame = new JFrame();
        frame.setTitle("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setJMenuBar(addMenuBar());
        panel = new JPanel(new BorderLayout());
        mainPanel = new JPanel();
        panel = new JPanel(new BorderLayout());
		mainPanel = new JPanel(new BorderLayout());
		namePanel = new JPanel();
    }

    public void addBoard(){
        mainPanel.add(boardObj, BorderLayout.CENTER);
        mainPanel.add(statusbar, BorderLayout.SOUTH);
    }

    public JMenuBar addMenuBar(){
        menuBar = new JMenuBar();
        addMenu();
        return menuBar;
    }

    /**
	 * Creates the main panel that holds the scores.
	 */
	public void addMainPanel(){
		panel.add(mainPanel, BorderLayout.CENTER);
	}

    public void addPlayerName(){
		mainPanel.add(namePanel, BorderLayout.NORTH);
		namePanel.setLayout(new BoxLayout(namePanel,BoxLayout.X_AXIS));
		
		lblPlayerName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPlayerName.setPreferredSize(new Dimension(50,50));
		lblPlayerName.setMaximumSize(new Dimension(50,50));
		
		txtPlayerName.setHorizontalAlignment(SwingConstants.LEFT);
		txtPlayerName.setPreferredSize(new Dimension(200,50));
		txtPlayerName.setMaximumSize(new Dimension(150,30));
		
		namePanel.add(lblPlayerName);
		namePanel.add(txtPlayerName);
	}


    public void addMenu() {
        menuBarOptions = new JMenu("Options");
        menuBarOptions.setMnemonic(KeyEvent.VK_0);
        menuBarOptions.getAccessibleContext().setAccessibleDescription("Game Options");
        menuBar.add(menuBarOptions);
        addOptionItems();
    }

    public void addOptionItems(){
        menuLoadGame = new JMenuItem("Load Game");
        menuSaveGame = new JMenuItem("Save Game");
        menuTop5Game = new JMenuItem("Top 5 Games");
        menuExit = new JMenuItem("Exit");
        
        menuLoadGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		menuLoadGame.addActionListener(new MenuHandler());

        menuSaveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuSaveGame.addActionListener(new MenuHandler());

        menuTop5Game.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		menuTop5Game.addActionListener(new MenuHandler());

        menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		menuExit.addActionListener(new MenuHandler());

		menuBarOptions.add(menuLoadGame);
		menuBarOptions.add(menuSaveGame);
        menuBarOptions.add(menuTop5Game);
		menuBarOptions.add(menuExit);
    }

    public void selectExitDialog(){

		int selection = JOptionPane.showConfirmDialog(frame, "Would you like to exit the game?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (selection == JOptionPane.YES_OPTION){
			System.exit(0);
		}
	}

    	/**
	 *  creates the save window - shows save button.
	 */
	public void saveGameWindow(){
		frame2 = new JFrame();
		frame2.setTitle("Save Game");
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setSize(300,350);
		frame2.setResizable(false);

		panel = new JPanel(new BorderLayout());
		frame2.getContentPane().add(panel);

		savePanel = new JPanel();
		panel.add(savePanel, BorderLayout.CENTER);
		savePanel.setLayout(new BoxLayout(savePanel,BoxLayout.X_AXIS));
		
		btnSaveGame = new JButton("Save Game");
		btnSaveGame.setHorizontalAlignment(SwingConstants.CENTER);
		btnSaveGame.setPreferredSize(new Dimension(150,30));
		btnSaveGame.setMaximumSize(new Dimension(150,30));
		btnSaveGame.addActionListener(new SaveHandler());
		
		savePanel.add(btnSaveGame);

		frame2.setVisible(true);
	}
	
    /**
	 * Get list of games saved from server
	 * @return list of games saved in GameList obj
	 */
	public ArrayList<GameList> retrivePastGames() {
		ArrayList<GameList> lists = new ArrayList<>();
		
		//Init connection and get list of saved games
		System.out.println("Starting Connection...");
		try{
	          sock = new Socket("127.0.0.1", 5001);
	          clientOutputStream = new ObjectOutputStream(sock.getOutputStream());
	          clientInputStream = new ObjectInputStream(sock.getInputStream());
	             
	          System.out.println("Connected...");
	          int init = clientInputStream.read();
	          System.out.println(init);
	          
	          //Code 202: to get list of saved data
	          clientOutputStream.write(202);
	          clientOutputStream.flush();
	          
	          lists = (ArrayList<GameList>) clientInputStream.readObject();
	          
	          clientOutputStream.close();
	          clientInputStream.close();
              sock.close();
	          
	      }catch(ClassNotFoundException | IOException ex){
	          ex.printStackTrace();
	      }
		
		return lists;
	}
	/**
	 *  creates the Load game window - shows game save states.
	 */
	public void loadGameWindow(){
		frame2 = new JFrame();
		frame2.setTitle("Load Game");
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setSize(400,750);
		frame2.setResizable(true);

		panel = new JPanel(new BorderLayout());
		frame2.getContentPane().add(panel);

		loadPanel = new JPanel();
		panel.add(loadPanel, BorderLayout.NORTH);
		loadPanel.setLayout(new BoxLayout(loadPanel,BoxLayout.Y_AXIS));
		
		lists = retrivePastGames();
		btnLoadGame = new JButton[lists.size()];
		
		for (int i = 0; i < lists.size(); i++) {
			
			btnLoadGame[i] = new JButton(lists.get(i).getGameName());
			btnLoadGame[i].setHorizontalAlignment(SwingConstants.CENTER);
			btnLoadGame[i].setPreferredSize(new Dimension(400,30));
			btnLoadGame[i].setMaximumSize(new Dimension(400,30));
			btnLoadGame[i].addActionListener(new RestoreHandler());
			
			loadPanel.add(btnLoadGame[i]);
		}

		frame2.setVisible(true);
	}

    public void top5GameWindow(){
		frame2 = new JFrame();
		frame2.setTitle("Load Game");
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setSize(400,750);
		frame2.setResizable(true);

		panel = new JPanel(new BorderLayout());
		frame2.getContentPane().add(panel);

		// loadPanel = new JPanel();
		// panel.add(loadPanel, BorderLayout.NORTH);
		// loadPanel.setLayout(new BoxLayout(loadPanel,BoxLayout.Y_AXIS));
		
		// lists = retrivePastGames();
		// btnLoadGame = new JButton[lists.size()];
		
		// for (int i = 0; i < lists.size(); i++) {
			
		// 	btnLoadGame[i] = new JButton(lists.get(i).getGameName());
		// 	btnLoadGame[i].setHorizontalAlignment(SwingConstants.CENTER);
		// 	btnLoadGame[i].setPreferredSize(new Dimension(400,30));
		// 	btnLoadGame[i].setMaximumSize(new Dimension(400,30));
		// 	btnLoadGame[i].addActionListener(new RestoreHandler());
			
		// 	loadPanel.add(btnLoadGame[i]);
		// }

		frame2.setVisible(true);
	}


    /**
	 * Finds the source when a menu button is pressed and chooses the correct action to perform.
	 * 
	 * @author Kunwar Shivam Srivastav
	 *
	 */
	class MenuHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			Object source = event.getSource();

			if(source == menuRestartGame){
				//selectRestartDialog();
				selectExitDialog();
			} else if(source == menuLoadGame){
				loadGameWindow();
			} else if ( source == menuSaveGame){
				saveGameWindow();
			}  else if ( source == menuTop5Game){
				top5GameWindow();
			}else if ( source == menuExit){
				selectExitDialog();
			}
		}
	}

    /**
	 * Retrives and restores the game state for the selected game from the retrivePastGames() function
	 * @author Shivam
	 *
	 */
	class RestoreHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			MinesweeperData data = new MinesweeperData();
			
			Object source = event.getSource();
			
			int id = -1;
			for(int i = 0; i < btnLoadGame.length; i++){
				if(source == btnLoadGame[i]){
					id = lists.get(i).getID();
					break;
				}
			}
			//Connect to server and get data
			System.out.println("Starting Connection...");
			try{
		          sock = new Socket("127.0.0.1", 5001);
		          clientOutputStream = new ObjectOutputStream(sock.getOutputStream());
		          clientInputStream = new ObjectInputStream(sock.getInputStream());
		             
		          System.out.println("Connected...");
		          int init = clientInputStream.read();
		          System.out.println(init);
		          
		          //Code 204: to get particular saved data
		          clientOutputStream.write(204);
		          clientOutputStream.flush();
		          
		          clientOutputStream.write(id);
		          clientOutputStream.flush();
		          
		          data = (MinesweeperData) clientInputStream.readObject();
		          System.out.println(data.getMinesweeperData().getField());
		          
		          clientOutputStream.close();
		          clientInputStream.close();
	              sock.close();
		          
		      }catch(ClassNotFoundException | IOException ex){
		          ex.printStackTrace();
		      }
			
			frame2.setVisible(false);
			
			//Update UI based on new data
			
			//Set Player Name
			Minesweeper md = data.getMinesweeperData();
			minesweeperObj.setField(md.getField());
			int[] field = md.getField();
			System.out.println("=====>"+Arrays.toString(field));
			int check[] = minesweeperObj.getField();
			System.out.println("=====>"+Arrays.toString(check));
			boardObj.newGame(true);
//			boardObj.paintComponent(minesweeperObj.getGraphics());

//			addMainPanel();
//			addPlayerName();
			txtPlayerName.setText(data.getPlayerName());
//			addBoard();
        }
	}

    /**
	 *  Shows a pop-up alert for when a player has reached the maximum roll count
	 */
	public void createSaveAlert(int code){
		if (code == 201) {
			JOptionPane.showMessageDialog(frame, "Game Saved.");
		} else {
			JOptionPane.showMessageDialog(frame, "Save Failed. Try Again.");
		}
		
	}
    /**
	 * Save current game state to database on the server
	 * @author Shivam
	 *
	 */
	class SaveHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			String playerName = txtPlayerName.getText();
			
			LocalDateTime myDateObj = LocalDateTime.now();
		    System.out.println("Before formatting: " + myDateObj);
		    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"); //Changeable, doesn't affect gameplay

		    String gameDate = myDateObj.format(myFormatObj);
			
			System.out.println("Starting Connection...");
			try{
		          sock = new Socket("127.0.0.1", 5001);
		          clientOutputStream = new ObjectOutputStream(sock.getOutputStream());
		          clientInputStream = new ObjectInputStream(sock.getInputStream());
		             
		          System.out.println("Connected...");
		          int init = clientInputStream.read();
		          System.out.println(init);
		          
		          MinesweeperData yd = new MinesweeperData(playerName, gameDate, minesweeperObj);

		          //Code 200: to save data
		          clientOutputStream.write(200);
		          clientOutputStream.flush();
		          clientOutputStream.writeObject(yd);
		          clientOutputStream.flush();

		          int response = clientInputStream.read();
		          System.out.println("Response:" + response);
		          createSaveAlert(response);
		          
		          clientOutputStream.close();
		          clientInputStream.close();
	              sock.close();
		          
		      }catch(IOException ex){
		          ex.printStackTrace();
		      }

			frame2.setVisible(false);
		}
	}
}
