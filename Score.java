import java.io.Serializable;

public class Score implements Serializable {
    private String playerName = "Player1";
    private int score = 0;

    public Score(String playerName, int score){
        this.playerName = playerName;
        this.score = score;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }
}
