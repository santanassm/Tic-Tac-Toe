
original_game_board = '';
myTurn = false;
username = null;
currentPage = 'login';
gameID = null;
opponent = null;
myLetter = null;
otherLetter = null;
winner = null;
last_game = null; 	// username of the person you previously played with.

pollInvitesId = null;
pollGameStateId = null;

function pollGameState() {
	pollGameStateId = setInterval(function () {
		if (gameID != null && username != null)
			ajaxCall('Play.action', {gameID: gameID, username: username}, updateGameBoard);
		}, 3000);
}

function pollInvites() {
	pollInvitesId = setInterval(function () {
		if (username != null)
				ajaxCall('Refresh.action', {username: username}, updateInvitePage);
		}, 3000);
}

function stopPoll(pullId) {
	if (pullId != null)
		clearInterval(pullId);
}

function login(username) {
	ajaxCall('RegisterUser.action', {username: username}, function(response) {
		setUsername(username);
		goToPage('invite');
	});
}

function logout() {
	username = null;
	myTurn = false;
	goToPage('login');
	stopPoll(pollInvitesId);
	stopPoll(pollGameStateId);
}


function updateInvitePage(response) {
	if (response.opponent.length > 0) {
		var opp = response.opponent;
		if (last_game != opp && opp != opponent) {
			setOpponent(opp);
			openPopup('invite_box_popup');
		} else {
			last_game = null;
		}
	}
}

function acceptInvitation() {
	if (opponent != null) {
		ajaxCall('Invite.action', {username: username}, function(response) {
			stopPoll(pollInvitesId);
			pollGameState();
			closePopup('invite')
			goToPage('play');
			myTurn = true;
			$('#board li').css('cursor', 'pointer');
			myLetter = 'X';
			otherLetter = 'O';
			gameID = response.gameID;
		});
	}
}

function declineInvitation() {
	if (opponent != null) {
		ajaxCall('Invite.action', {username: username, declineGame: true}, function (response) {
			opponent = null;
		});
	}
}

function sendInvitation() {
	to = $('#invite_form input[name="opponent"]').val();
	if (to.length < 1) {
		showErrorPopup('You did not enter a username!');
		return;
	}
	ajaxCall('Invite.action', {username: username, opponent: to}, function (response) {
		goToPage('play');
		myLetter = 'O';
		otherLetter = 'X';
		setOpponent(to);
		gameID = response.gameID;
		stopPoll(pollInvitesId);
		pollGameState();
	});
}

function quitGame() {
	if (opponent != null) {
		last_game = opponent;
		opponent = null;
		gameID = null;
		myTurn = false;
		myLetter = null;
		otherLetter = null;
		winner = null;
		stopPoll(pollGameStateId);
		goToPage('invite');
		ajaxCall('Play.action', {username: username, quitGame: true}, function (response) {
			pollInvites();
		});
		$('#board').html(original_game_board);
	}
}

function isCellTaken(cell) {
	return $('#cell'+cell).html().length > 0;
}

function makeMove(where) {
	if (winner != null)
		return;
	if (isCellTaken(where)) {
		showErrorPopup('That spot is taken!');
		return;
	} else if (myTurn) {
		ajaxCall('Play.action', {gameID: gameID, username: username, requestMove: where}, function (response) {
			$('#cell'+where).html('<span class="me">'+myLetter+'</span>');
			myTurn = false;
			disableElement('#board li');
		});
	} else {
		showErrorPopup('It is not your turn!');
	}
}
  
function updateGameBoard(response) {
	if (response.gameOutcome.length > 0) {
		if (response.gameOutcome == "WAIT") {
			$('#play #message').html('<small><em>Waiting for opponent...</em></small>');
		}
		else if (response.gameOutcome == "TIMEOUT") {
			showErrorPopup('Your opponent has timed out.');
			quitGame();
		}
		else if (response.gameOutcome == "DECLINED") {
			showPopupMessage('<span class="opponent">'+opponent+'</span> has declined your invitation');
			quitGame();
		}
		else if (response.gameOutcome == myLetter) {
			myTurn = false;
			winner = response.gameOutcome;
			stopPoll(pollGameStateId);
			showPopupMessage('You win!');
		}
		else if (response.gameOutcome == "TIE") {
			myTurn = false;
			winner = response.gameOutcome;
			stopPoll(pollGameStateId);
			showPopupMessage('Tie game!');
		}
		else if (response.gameOutcome == otherLetter) {
			myTurn = false;
			winner = response.gameOutcome;
			stopPoll(pollGameStateId);
			showPopupMessage('You lose!');
		}
		return;
	}

	gameID = response.gameID;
	myTurn = response.yourTurn;

	if (myTurn) {
		$('#play #message').html('<strong>It is your turn.</strong>');
		enableElement('#board li');
	} else {
		$('#play #message').html('It is '+otherLetter+'\'s turn.');
		disableElement('#board li');
	}

	// update cell text/cursors
	for (var i = 0; i <= 8; i++) {
		letter = response.boardState[i];
		if (letter == 0) letter = '';
		else if (letter == 1) letter = 'O';
		else if (letter == 2) letter = 'X';
		var htm = '';
		if (letter == myLetter)
			htm = '<span class="me">'+letter+'</span>';
		else if (letter != '')
			htm = '<span class="them">'+letter+'</span>'; 
		$('#board #cell'+i).html(htm);
		if (myTurn) {
			if (letter != '')
				disableElement('#cell'+i);
			else
				enableElement('#cell'+i);
		}
	}

}

// make an ajax call. callback is only called if there are no errors.
function ajaxCall(action, data, callback) {
	$.ajax({
      type: "GET",
	  url: action,
	  cache: false,
	  dataType: "json",
	  data: data,
	  success: function(response) {
	    if (response.hasErrors) {
	    	showErrorPopup(response.errorMessages.join("<br>"));
	    } else {
	    	callback(response);
	    }
	  }
	});
}

function showErrorPopup(msg) {
	$('#error_message').html(msg);
	openPopup('error_popup');
}

function closePopups() {
	$('.popup:visible').fadeOut('fast');
}

function closePopup(popup_id) {
	$('#'+popup_id).fadeOut('fast');
}

function openPopup(popup_id) {
	$('#'+popup_id).fadeIn('slow');
}

function disableElement(element) {
	$(element).css('cursor', 'not-allowed');
}

function enableElement(element) {
	$(element).css('cursor', 'pointer');
}


function showPopupMessage(msg) {
	$('#generic_popup .message').html(msg);
	$('#generic_popup').fadeIn('slow');
}


$(function() {

	original_game_board = $('#board').html();

	goToPage('login');

	$('button').button();

	$('form').attr('onsubmit', 'return false');

	$('#login_button').click(function() {
		login($('input[name="username"]').val());
	});

	$('#close_error_popup').click(function() {
		closePopup('error_popup');
	});

	$('#close_generic_popup').click(function() {
		closePopup('generic_popup');
		if (winner != null) {
			// hack to quit game when user hits OK if winner is set.
			winner = null;
			quitGame();
		}
	});

	$('#send_invite_button').click(function() {
		sendInvitation($('#invite_form input[name="opponent"]').val());
		$('#invite_form input[name="opponent"]').val('');
	});

	$('#quit_game_button').click(function() {
		quitGame();
	});

	$('#logout_button').click(function() {
		logout();
	});

	$('#invite_accept_button').click(function() {
		closePopup('invite_box_popup');	
		acceptInvitation();
	});

	$('#invite_decline_button').click(function() {
		closePopup('invite_box_popup');	
		declineInvitation();
	});

	$('#board li').live('click', function() {
		makeMove($(this).attr('id').replace('cell', ''));
	});

});

function setUsername(str) {
	username = str;
	$('.username').html(username);
}

function setOpponent(str) {
	opponent = str;
	$('.opponent').html(opponent);
}

function goToPage(padeId) {
	currentPage = padeId;
	if (currentPage == 'login') {
		stopPoll(pollInvitesId);
		stopPoll(pollGameStateId);
	} else if (currentPage == 'invite') {
		pollInvites();
	}
	$('.page').hide();
	$('#'+currentPage).show();
}


