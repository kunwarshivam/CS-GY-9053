
// Author: Kunwar Shivam Srivastav
// https://github.com/kunwarshivam/CS-GY-9053,


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Reference = https://zetcode.com/javagames/minesweeper/

public class Board extends JPanel {

    private JFrame scoreFrame;
    private JTextField txtPlayerName = new JTextField(30);
    private JLabel lblPlayerName = new JLabel("Name: ");
    private JPanel  namePanel;
    private JButton btnSaveScore;

    private static final String MINESWEEPERTILES = "./minesweepertiles/";
    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
    private final int TOP_MARGIN = 30;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private final int N_MINES = 40;
    private final int N_ROWS = 16;
    private final int N_COLS = 16;

    private final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
    private final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;

    private int allCells;
    private final JLabel statusbar;
    private Minesweeper minesweeperObj;

    private JLabel timeLabel = new JLabel("Time Remaining: 1000");
    private JPanel timePanel = new JPanel();
    private Timer timer;

    public Board(JLabel statusbar, Minesweeper obj) {

        this.statusbar = statusbar;
        this.minesweeperObj = obj;
        addTimePanel();
        init();
        createScoreWindow();
    }

    public void addPlayerNameToScorePanel(){
        namePanel = new JPanel();

        scoreFrame.add(namePanel, BorderLayout.NORTH);
        namePanel.setLayout(new BoxLayout(namePanel,BoxLayout.X_AXIS));

        lblPlayerName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPlayerName.setPreferredSize(new Dimension(50,50));
        lblPlayerName.setMaximumSize(new Dimension(50,50));

        txtPlayerName.setHorizontalAlignment(SwingConstants.LEFT);
        txtPlayerName.setPreferredSize(new Dimension(200,50));
        txtPlayerName.setMaximumSize(new Dimension(150,30));

        txtPlayerName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                minesweeperObj.setPlayerName(txtPlayerName.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                minesweeperObj.setPlayerName(txtPlayerName.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                minesweeperObj.setPlayerName(txtPlayerName.getText());
            }
        });

        namePanel.add(lblPlayerName);
        namePanel.add(txtPlayerName);
    }

    public void createScoreWindow(){
        scoreFrame = new JFrame();
        scoreFrame.setTitle("Save Score");
        scoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        scoreFrame.setSize(300, 100);
        scoreFrame.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        scoreFrame.getContentPane().add(panel);
        JPanel savePanel = new JPanel();
        panel.add(savePanel, BorderLayout.CENTER);
        savePanel.setLayout(new BoxLayout(savePanel,BoxLayout.X_AXIS));
        addPlayerNameToScorePanel();

        btnSaveScore = new JButton("Save Score");
        btnSaveScore.setHorizontalAlignment(SwingConstants.CENTER);
        btnSaveScore.setPreferredSize(new Dimension(150,30));
        btnSaveScore.setMaximumSize(new Dimension(150,30));
        btnSaveScore.addActionListener(new Board.SaveScoreHandler());

        savePanel.add(btnSaveScore);
    }

    class SaveScoreHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String playerName = txtPlayerName.getText();
            minesweeperObj.setPlayerName(playerName);
            minesweeperObj.saveScore();
            scoreFrame.setVisible(false);
        }
    }

    public void addTimePanel(){
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
        timePanel.add(timeLabel);
        add(timePanel);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minesweeperObj.setTimeLeft(minesweeperObj.getTimeLeft() - 1);
                timeLabel.setText("Time Remaining: " + minesweeperObj.getTimeLeft());
                timeLabel.repaint();
                if(minesweeperObj.getTimeLeft() <= 0){
                    repaint();
                }
            }
        });
    }

    private void init() {

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {

            String path = MINESWEEPERTILES + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MinesAdapter());
        newGame();
    }



    public void newGame(boolean loadedFromDB) {

        inGame = true;
        allCells = N_ROWS * N_COLS;
        minesLeft = this.minesweeperObj.getMinesLeft();
        field = Arrays.copyOf(this.minesweeperObj.getField(), this.minesweeperObj.getField().length);
        statusbar.setText(Integer.toString(minesLeft));
        repaint();
    }

    public void newGame() {

        int cell;
        if(timer.isRunning()){
            timer.stop();
        }
        Random random = new Random();
        inGame = true;
        allCells = N_ROWS * N_COLS;
        field = new int[allCells];
        minesweeperObj.setTimeLeft(1000);

        minesLeft = minesweeperObj.getMinesLeft();
        for (int i = 0; i < allCells; i++) {

            field[i] = COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(minesLeft));
        int i = 0;


        while (i < N_MINES) {

            int position = (int) (allCells * random.nextDouble());

            if ((position < allCells)
                    && (field[position] != COVERED_MINE_CELL)) {

                int current_col = position % N_COLS;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;
                if (cell >= 0) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                cell = position + N_COLS;
                if (cell < allCells) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {
                    cell = position - N_COLS + 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
        repaint();
        minesweeperObj.setField(field);
    }

    private void find_empty_cells(int j) {

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < allCells) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        int uncover = 0;

        if (minesweeperObj.getTimeLeft() == 0) {
            inGame = false;
        }

        for (int i = 0; i < N_ROWS; i++) {

            for (int j = 0; j < N_COLS; j++) {

                int cell = field[(i * N_COLS) + j];

                if (inGame && cell == MINE_CELL) {

                    inGame = false;
                }

                if (!inGame) {

                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }

                } else {

                    if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }
                g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE) + TOP_MARGIN, this);
            }
        }

        if (uncover == 0 && inGame) {
            inGame = false;
            scoreFrame.setVisible(true);
            statusbar.setText("Game won");
            timer.stop();
        } else if (!inGame) {
            statusbar.setText("Game lost");
            timer.stop();
        }
    }

    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            if(!timer.isRunning()){
                timer.start();
            }

            int x = e.getX();
            int y = e.getY() - TOP_MARGIN;

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean doRepaint = false;

            if (!inGame) {

                newGame();
                repaint();
                return;
            }

            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (field[(cRow * N_COLS) + cCol] > MINE_CELL) {

                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) {

                            if (minesLeft > 0) {
                                field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                                minesLeft--;
                                String msg = Integer.toString(minesLeft);
                                statusbar.setText(msg);
                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {

                            field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            statusbar.setText(msg);
                        }
                    }

                } else {

                    if (field[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {

                        return;
                    }

                    if ((field[(cRow * N_COLS) + cCol] > MINE_CELL)
                            && (field[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {

                        field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] == MINE_CELL) {
                            inGame = false;
                        }

                        if (field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {
                            find_empty_cells((cRow * N_COLS) + cCol);
                        }
                    }
                }

                if (doRepaint) {
                    minesweeperObj.setField(field);
                    repaint();
                }
            }
        }
    }
}