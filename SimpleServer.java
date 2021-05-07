
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SimpleServer {

  ArrayList clientOutputStreams;
  private  MinesweeperData y;
  private Connection conn;

  SimpleServer () {

  }
  public static void main(String args[]){
      new SimpleServer().go();
  }
  
  /**
   * Start up server and handle requests
   */
  public void go(){
      clientOutputStreams = new ArrayList();
      
      try {
    	  conn = DriverManager.getConnection("jdbc:sqlite:finalproject.db");

      } catch (SQLException e) {
    	  System.err.println("Connection error: " + e);
    	  System.exit(1);
      }
      
      System.out.println("Starting Server...");
      try{
          ServerSocket server = new ServerSocket(5001);
          System.out.println("Server Started...");
          while(true){
        	  
              Socket clientSocket = server.accept();
              
              ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
              ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
     
              System.out.println("Client Connected...");
              
              serverOutputStream.write(100);
              serverOutputStream.flush();
              
              int code = serverInputStream.read();
              if (code == 200) {
            	  MinesweeperData data = (MinesweeperData) serverInputStream.readObject();
            	 int returnCode = saveData(data);
            	  serverOutputStream.write(returnCode);
            	  serverOutputStream.flush();
              }
              
              if (code == 202) {
            	 ArrayList<GameList> list = retrieveData();
            	  serverOutputStream.writeObject(list);
            	  serverOutputStream.flush();
              }
              
              if (code == 204) {
            	  int id = serverInputStream.read();
            	  
            	MinesweeperData data = getMinesweeperData(id);
            	  serverOutputStream.writeObject(data);
            	  serverOutputStream.flush();
              }
			  if (code  == 206) {
			  	Score score = (Score) serverInputStream.readObject();
			  	int returnCode = saveScore(score);
			  	serverOutputStream.write(returnCode);
			  	serverOutputStream.flush();
			  }
			  if (code == 208) {
				  ArrayList<ScoreList> score = retrieveScores();
				  serverOutputStream.writeObject(score);
				  serverOutputStream.flush();
			  }
              
          }
      }catch(IOException e){
          System.out.println("Cause1"+e.getCause());
          e.printStackTrace();
	  }
      catch(ClassNotFoundException e){
          System.out.println("Cause2"+e.getCause());
          e.printStackTrace();
      }
  }
  
//   /**
//    * Insert game data into database
//    * @param MinesweeperData obj
//    * @return int sucess or failure code
//    */
  public int saveData(MinesweeperData data) {
	  System.out.println("Name: "+data.getPlayerName());
	  System.out.println("Date: "+data.getGameDate());
	  System.out.println("Y: "+data.getMinesweeperData().toString());
	  //Add JDBC code to save to database
	  
	  String sql = "INSERT INTO gamedata(name, date_time, serialized_obj) values(?,?,?)";
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		   ps.setString(1, data.getPlayerName());
		   ps.setString(2, data.getGameDate());
		   
		   //Save object data
		   ByteArrayOutputStream baos = new ByteArrayOutputStream();
		   ObjectOutputStream oout = new ObjectOutputStream(baos);
		   oout.writeObject(data);
		   oout.close();
		   ps.setBytes(3, baos.toByteArray());
		   
		   int res = ps.executeUpdate();
		   ps.close();
		   
		   if(res>0) {
			   return 201;
		   }
		   else {
			   return 404; 
		   }
		   
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return 404;
		}
  }

  public int saveScore(Score score) {
		//Add JDBC code to save to database
		System.out.println(">>>>>>score:"+score.getScore());
		String sql = "INSERT INTO scores(name, score) values(?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, score.getPlayerName());
			ps.setString(2, ""+score.getScore());

			int res = ps.executeUpdate();
			ps.close();

			if(res>0) {
				return 207;
			}
			else {
				return 404;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 404;
		}
	}

	public ArrayList<ScoreList> retrieveScores() {
		//Retrieve all game states
		ArrayList<ScoreList> lists = new ArrayList<>();
		int id;
		String name;
		int score;

		String sql = "Select id, name, score from scores order by score desc limit 5";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rset = ps.executeQuery();

			while (rset.next()) {
				id = rset.getInt(1);
				name = rset.getString(2);
				score = Integer.parseInt(rset.getString(3));

				ScoreList list = new ScoreList(id, name, score);
				System.out.println("Fetched: " + list.getGameName());

				lists.add(list);
			}

			rset.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lists;
	}

//   /**
//    * Get list of all the saved games
//    * @return Array with all games saved
//    */
  public ArrayList<GameList> retrieveData() {
	  //Retrieve all game states
	  ArrayList<GameList> lists = new ArrayList<GameList>();
	  int id;
	  String name, datetime;
	  
	  String sql = "Select id, name, date_time from gamedata";
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		   ResultSet rset = ps.executeQuery();
		   
		   while (rset.next()) {
			   id = rset.getInt(1);
			   name = rset.getString(2);
			   datetime = rset.getString(3);
			   
			   GameList list = new GameList(id, name, datetime);
			   System.out.println("Fetched: " + list.getGameName());
			   
			   lists.add(list);
		   }
		   
		   rset.close();
		   ps.close();
		   
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  
	  return lists;
  }
  
//   /**
//    * Get MinesweeperData object from the database
//    * @param id of row to retrieve from data
//    * @return MinesweeperData obj from database where id
//    */
  public MinesweeperData getMinesweeperData(int id) {
	  //Use id to fetch that game state
	  MinesweeperData data = new MinesweeperData();
	  
	  String sql = "Select name, date_time, serialized_obj from gamedata where id = ?";
	  System.out.println(id);
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		   ps.setInt(1, id);

		   ResultSet rset = ps.executeQuery();
		   rset.next();
		   
		   byte[] buf = rset.getBytes("serialized_obj");
		   ObjectInputStream objectIn = null;
		   if (buf != null)
			   objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
		   
		   data = (MinesweeperData) objectIn.readObject();
		   
		   System.out.println("Name:"+data.getPlayerName());
		   System.out.println("Date:"+data.getGameDate());
		   
		   rset.close();
		   ps.close();
		   
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	  
	  return data;
  }
}
