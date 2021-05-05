import java.io.Serializable;

public class GameList implements Serializable{

	private int id;
	private String playerName;
	private String gameDate;
	
	GameList(int id, String playerName, String gameDate) {
		this.id = id;
		this.playerName = playerName;
		this.gameDate = gameDate;
	}
	
	public String getGameName() {
		return "Player Name: " + playerName + ", Date & Time: " + gameDate;
	}
	
	public int getID() {
		return id;
	}
}
