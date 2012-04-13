package tictactoe;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class UserController extends ActionSupport implements ServletContextAware {
	
	private final long MAX_TIMEOUT = 4*60; // 4 minute user timeout
	
	private String username = "";
	private String opponent = "";
	private long gameID = -1;
	private boolean declineGame = false;
	
	private long lastRefresh;
	private Hashtable<String,User> users;
	private Vector<Game> games;
	private ServletContext context;
	
	public long getNewGameID() {
		Long lastGame = (Long) context.getAttribute("gameid");
		if(lastGame == null) {
			lastGame = 0L;
		} else {
			lastGame++;
			context.setAttribute("gameid", lastGame);
		}
		return(lastGame);
	}
	
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
		// Check for timed out users and remove them
		Date now = new Date();
		Enumeration<String> keys = users.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			User u = users.get(key);
			Date value = u.getLastRefresh();
			if(CommonOperations.getElapsedSeconds(value, now) > MAX_TIMEOUT) {
				// first check if they're in a game, update game state if yes
				Iterator<Game> i = games.iterator();
				while(i.hasNext()) {
					Game g = i.next();
					if((g.getO().getUsername().equals(u.getUsername()) || g.getX().getUsername().equals(u.getUsername())) && (g.state != GameState.X_WIN && g.state != GameState.O_WIN && g.state != GameState.TIE_GAME)) {
						g.state = GameState.USER_TIMEOUT;
						g.getO().setOpponent(null);
						g.getX().setOpponent(null);
					}
				}
				users.remove(key);
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
	
	public String register() {
		loadMemory();
		Object alreadyRegistered = users.get(username);
		if (alreadyRegistered != null) {
			addActionError("That username is already in use");
		} else if (username.equals("")) {
			addActionError("Username cannot be blank");
		} else {
			Date newStop = new Date();
			users.put(username, new User(username,newStop));			
		}
		storeMemory();
		return Action.SUCCESS;
	}
	
	public String refresh() {
		loadMemory();
		User u = users.get(username);
		if (u != null) {
			opponent = u.getOpponent();
			Date lastStop = u.getLastRefresh();
			Date newStop = new Date();
			lastRefresh = CommonOperations.getElapsedSeconds(lastStop, newStop);
			u.setLastRefresh(newStop);
		} else {
			addActionError("User does not exist");
		}
		storeMemory();
		return Action.SUCCESS;
	}
	
	public String invite() {
		loadMemory();
		User u = users.get(username);
		if (u == null) {
			addActionError("You are not registered in the system");
		} else {
			if(opponent.equals("")) {
				// we are accepting/declining an invite
				Iterator<Game> i = games.iterator();
				while(i.hasNext()) {
					Game g = i.next();
					// the INVITED user is always going to be X
					if(g.getX() == u && g.state == GameState.OPPONENT_NOT_CONNECTED) {
						if(declineGame) {
							// decline the invite, close the game
							User o = users.get(u.getOpponent());
							if(o != null) {
								o.setOpponent(null);
								u.setOpponent(null);
								g.state = GameState.INVITE_DECLINED;
								storeMemory();
								return Action.SUCCESS;
							}
						} else {
							// accept the invite, start the game
							gameID = g.getGameID();
							opponent = u.getOpponent();
							g.state = GameState.X_TURN;
							storeMemory();
							return Action.SUCCESS;
						}
					}
				}
				// if we reach here, then there is no game where we have been invited to...
				addActionError("You cannot accept/decline an invite because you were not invited to any game");
			} else {
				
				// we are inviting someone
				User o = users.get(opponent);
				if (u.getOpponent() != null) {
					addActionError("You already have an invite pending");
				} else if(o == null) {
					addActionError("Requested opponent is not registered in system");
				} else if(o.getOpponent() != null) {
					addActionError("That opponent already has a pending invite");
				} else if (opponent.equals(username)) {
					addActionError("You cannot play with yourself");
				} else {
					gameID = getNewGameID();
					Game g = new Game(gameID);
					g.setX(o);
					g.setO(u);
					o.setOpponent(username);
					u.setOpponent(opponent);
					games.add(g);
				}
			}
		}
		storeMemory();
		return Action.SUCCESS;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setServletContext(ServletContext arg0) {
		context = arg0;
	}

	public void setLastRefresh(long lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public long getLastRefresh() {
		return lastRefresh;
	}
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}
	public String getOpponent() {
		return opponent;
	}

	public void setGameID(long gameID) {
		this.gameID = gameID;
	}

	public long getGameID() {
		return gameID;
	}

	public void setDeclineGame(boolean declineGame) {
		this.declineGame = declineGame;
	}

	public boolean isDeclineGame() {
		return declineGame;
	}
}
