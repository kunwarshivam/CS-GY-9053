import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Minesweeper implements Serializable {
    private int field[] = new int[16*16];
    private int minesLeft = 40;
    private long timeTaken = 0l;
//    private Graphics g;

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
        timeTaken = 0l;
    }

//    public Graphics getGraphics() { return g; };

    public long getTimeTaken(){
        return timeTaken;
    }

    public int getMinesLeft() {
        return minesLeft;
    }

    public int[] getField(){
        return field;
    }

    public void setTimeTaken(long timeTaken){
        this.timeTaken = timeTaken;
    }

    public void setMinesLeft(int minesLeft) {
        this.minesLeft = minesLeft;
    }

//    public void setGraphics(Graphics g){
//        this.g = g;
//    }

    public void setField(int field[]){
       this.field = Arrays.copyOf(field, field.length);
    }

	public static void main(String[] args) {
		new MinesweeperGUI();
	}

}