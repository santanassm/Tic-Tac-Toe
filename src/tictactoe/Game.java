package tictactoe;

import java.util.Date;

public class Game {
	public final int GAME_SPACES = 9;
	private long gameID;
	private Date lastMove;
	private User x; // inviteD user
	private User o; // inviteE user
	public SquareState[] board;
	public GameState state = GameState.OPPONENT_NOT_CONNECTED;
	public Game(long gameID) {
		this.gameID = gameID;
		lastMove = new Date();
		board = new SquareState[GAME_SPACES];
		for(int i=0;i<GAME_SPACES;i++) {
			board[i] = SquareState.EMPTY;
		}
	}
	public void setX(User x) {
		this.x = x;
	}
	public User getX() {
		return x;
	}
	public void setO(User o) {
		this.o = o;
	}
	public User getO() {
		return o;
	}
	public void setGameID(long gameID) {
		this.gameID = gameID;
	}
	public long getGameID() {
		return gameID;
	}
	
	public String getBoardStateString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<GAME_SPACES;i++) {
			switch(board[i]) {
				case EMPTY:
					sb.append("0");
					break;
				case O:
					sb.append("1");
					break;
				case X:
					sb.append("2");
					break;
			}
			if(i+1 < GAME_SPACES) {
				sb.append(", ");
			}
		}
		return(sb.toString());
	}
	
	// Checks, by square numbers, if all are the same and non-empty
	private void checkWin(int a, int b, int c) {
		if(board[a] == board[b] && board[b] == board[c]) {
			if(board[a] == SquareState.X) {
				state = GameState.X_WIN;
				x.setOpponent(null);
				o.setOpponent(null);
			} else if(board[a] == SquareState.O) {
				state = GameState.O_WIN;
				x.setOpponent(null);
				o.setOpponent(null);
			}
		}
	}
	public String getGameOutcome() {
		/* Game board configuration (square indexes)
		 *   0|1|2
		 *   3|4|5
		 *   6|7|8
		 */
		
		/*   #|#|#
		 *   _|_|_
		 *    | | 
		 */
		checkWin(0,1,2);
		
		/*   _|_|_
		 *   #|#|#
		 *    | | 
		 */
		checkWin(3,4,5);
		
		/*   _|_|_
		 *   _|_|_
		 *   #|#|#
		 */
		checkWin(6,7,8);
		
		/*   #|_|_
		 *   #|_|_
		 *   #| | 
		 */
		checkWin(0,3,6);
		
		/*   _|#|_
		 *   _|#|_
		 *    |#| 
		 */
		checkWin(1,4,7);
		
		/*   _|_|#
		 *   _|_|#
		 *    | |#
		 */
		checkWin(2,5,8);
		
		/*   #|_|_
		 *   _|#|_
		 *    | |#
		 */
		checkWin(0,4,8);
		
		/*   _|_|#
		 *   _|#|_
		 *   #| | 
		 */
		checkWin(2,4,6);
		
		// Check for tie
		if(state == GameState.X_TURN || state == GameState.O_TURN) {
			boolean hasEmpty = false;
			for(int i=0;i<GAME_SPACES;i++) {
				if(board[i] == SquareState.EMPTY) {
					hasEmpty = true;
					break;
				}
			}
			if(!hasEmpty) state = GameState.TIE_GAME;
		}
		
		// If someone wins, set the state as such
		switch(state) {
			case OPPONENT_NOT_CONNECTED:
				return("WAIT");
			case X_TURN:
				return("");
			case O_TURN:
				return("");
			case X_WIN:
				return("X");
			case O_WIN:
				return("O");
			case TIE_GAME:
				return("TIE");
			case USER_TIMEOUT:
				return("TIMEOUT");
			case INVITE_DECLINED:
				return("DECLINED");
			default:
				return("UNKNOWN");
		}
	}
	public void setLastMove(Date lastMove) {
		this.lastMove = lastMove;
	}
	public Date getLastMove() {
		return lastMove;
	}
}
