import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import java.util.Arrays;


// Author: Kunwar Shivam Srivastav
// https://github.com/kunwarshivam/CS-GY-9053,

public class Minesweeper implements Serializable {
    private int field[] = new int[16*16];
    private int minesLeft = 40;
    private int timeLeft = 1000;
    private String playerName = "Player1";
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
    private Socket sock;
    public Minesweeper(){
        for (int i = 0; i < 16*16; i++) {
            field[i] = 10;
        }
    }

    public void restartGame() {
        for (int i = 0; i < 16*16; i++) {
            field[i] = 10;
        }
        minesLeft = 40;
        timeLeft = 1000;
    }


    public int getTimeLeft(){
        return timeLeft;
    }

    public int getMinesLeft() {
        return minesLeft;
    }

    public int[] getField(){
        return field;
    }

    public String getPlayerName() { return playerName;}

    public void setTimeLeft(int timeLeft){
        this.timeLeft = timeLeft;
    }

    public void setMinesLeft(int minesLeft) {
        this.minesLeft = minesLeft;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public void setField(int field[]){
       this.field = Arrays.copyOf(field, field.length);
    }

    private int calculateScore() {
       return (int) timeLeft;
    }

    public void saveScore() {
        System.out.println("Saving score...");
        try{
            sock = new Socket("127.0.0.1", 5001);
            clientOutputStream = new ObjectOutputStream(sock.getOutputStream());
            clientInputStream = new ObjectInputStream(sock.getInputStream());

            System.out.println("Connected...");
            int init = clientInputStream.read();
            System.out.println(init);

            Score score = new Score(playerName, calculateScore());

            //Code 206: to save score
            clientOutputStream.write(206);
            clientOutputStream.flush();
            clientOutputStream.writeObject(score);
            clientOutputStream.flush();

            int response = clientInputStream.read();
            clientOutputStream.close();
            clientInputStream.close();
            sock.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

	public static void main(String[] args) {
		new MinesweeperGUI();
	}

}