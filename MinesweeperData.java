import java.io.Serializable;

public class MinesweeperData implements Serializable{
	
	private Minesweeper y;
	
	private String playerName, gameDate;
	
	MinesweeperData() {
		
	}
	
	MinesweeperData(String playerName, String gameDate, Minesweeper y) {
		this.playerName = playerName;
		this.gameDate = gameDate;
		this.y = y;
		
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public String getGameDate() {
		return gameDate;
	}
	
	public Minesweeper getMinesweeperData() {
		return y;
	}
}
