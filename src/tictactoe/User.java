package tictactoe;

import java.util.Date;

public class User {
	private Date lastRefresh;
	private String opponent;
	private String username;
	
	public User(String username, Date lastRefresh) {
		this.username = username;
		this.lastRefresh = lastRefresh;
	}

	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public Date getLastRefresh() {
		return lastRefresh;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public String getOpponent() {
		return opponent;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
	
}
