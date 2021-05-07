

import java.io.Serializable;

public class ScoreList implements Serializable {

    private int id;
    private String playerName;
    private int score;

    ScoreList(int id, String playerName, int score) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName(){
        return playerName;
    }

    public  int getScore(){
        return score;
    }

    public String getGameName() {
        return "Player Name: " + playerName + ", Score: " + score;
    }

    public int getID() {
        return id;
    }
}
