package tictactoe;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class GameController extends ActionSupport implements ServletContextAware {
	
	private final long MAX_TIMEOUT = 2*60;
	private long gameID;
	private String username = "";
	private int requestMove = -1;
	private boolean quitGame = false;

	private Game g = null;
	
	private boolean yourTurn = false;
	private String boardState = "";
	

	private String gameOutcome = "";
	
	private ServletContext context;
	private Hashtable<String,User> users;
	private Vector<Game> games;
	
	@SuppressWarnings("unchecked")
	private void loadMemory() {
		// Load users
		users = (Hashtable<String,User>) context.getAttribute("userlist");
		if (users == null) {
			users = new Hashtable<String,User>();
		}
		// Load games
		games = (Vector<Game>) context.getAttribute("gamelist");
		if(games == null) {
			games = new Vector<Game>();
		}
		// Set current game, release any games that may have timeout
		Date now = new Date();
		Iterator<Game> i = games.iterator();
		Game gi;
		while(i.hasNext()) {
			gi = i.next();
			Date value = gi.getLastMove();
			if(CommonOperations.getElapsedSeconds(value, now) > MAX_TIMEOUT && gi.state != GameState.X_WIN && gi.state != GameState.O_WIN && gi.state != GameState.TIE_GAME && gi.state != GameState.INVITE_DECLINED) {
				gi.state = GameState.USER_TIMEOUT;
			}
			if(gi.getGameID() == gameID) {
				g = gi;
			}
		}
	}
	private void storeMemory() {
		
		if (users == null) {
			users = new Hashtable<String,User>();
		}
		context.setAttribute("userlist", users);
		if (games == null) {
			games = new Vector<Game>();
		}
		context.setAttribute("gamelist", games);
	}
	
	public String play() {
		loadMemory();
		User u = users.get(username);
		if(u == null) {
			addActionError("You are not registered");
		}
		else if(g == null) {
			addActionError("That game ID doesn't exist");
		} else if (g.state != GameState.USER_TIMEOUT && quitGame) {
			if(g.getX().getUsername().equals(u.getUsername()) || g.getO().getUsername().equals(u.getUsername())) {
				g.state = GameState.USER_TIMEOUT;
				g.getX().setOpponent(null);
				g.getO().setOpponent(null);
			}
			else {
				addActionError("You cannot quit from a game you're not in");
			}
			
		} else {
			// make a move, if requested
			if(requestMove >= 0 && requestMove <= 8) {
				if(g.board[requestMove] == SquareState.EMPTY) {
					if(g.getX() == u) {
						g.board[requestMove] = SquareState.X;
						g.state = GameState.O_TURN;
						g.setLastMove(new Date());
					} else if(g.getO() == u) {
						g.board[requestMove] = SquareState.O;
						g.state = GameState.X_TURN;
						g.setLastMove(new Date());
					} else {
						addActionError("You're not a part of this game");
					}
				}
			}
			// get info about the board
			boardState = g.getBoardStateString(); // get the spaces
			gameOutcome = g.getGameOutcome();
			// Update if it's the user's turn
			if(g.getX().getUsername().equals(u.getUsername()) && g.state == GameState.X_TURN) {
				setYourTurn(true);
			} else if(g.getO().getUsername().equals(u.getUsername()) && g.state == GameState.O_TURN) {
				setYourTurn(true);
			}
		}
		
		storeMemory();
		return(Action.SUCCESS);
	}
	
	@Override
	public void setServletContext(ServletContext arg0) {
		context = arg0;

	}
	public void setGameOutcome(String gameOutcome) {
		this.gameOutcome = gameOutcome;
	}
	public String getGameOutcome() {
		return gameOutcome;
	}
	public void setYourTurn(boolean yourTurn) {
		this.yourTurn = yourTurn;
	}
	public boolean isYourTurn() {
		return yourTurn;
	}
	public String getBoardState() {
		return boardState;
	}
	public void setBoardState(String boardState) {
		this.boardState = boardState;
	}
	public long getGameID() {
		return gameID;
	}
	public void setGameID(long gameID) {
		this.gameID = gameID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setRequestMove(int requestMove) {
		this.requestMove = requestMove;
	}
	public int getRequestMove() {
		return requestMove;
	}
	public void setQuitGame(boolean quitGame) {
		this.quitGame = quitGame;
	}
	public boolean isQuitGame() {
		return quitGame;
	}
}
