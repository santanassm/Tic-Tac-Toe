CSC309 A5

Group:
g8petro (Student no. 995250829) Petro Podrezo
g1danks (Student no. 997627411) Andrew Danks


How the work was split up:
Petro did all of the backend (JSP, Java code, JSON generation)
Andrew did all of the frontend (HTML, CSS, JavaScript)

FRONTEND

All the "pages" are on the same page, to give the user a fully dynamic experience. Logging in is just like registring, once one chooses a username, they can login and invite to play with other registered users.

Each action that needs to interact with the server calls the function, 
ajaxCall(action, data, callback), where action is the script to call, data are the parameters to send, and callback is the function to call if the action was successful. If there were any server or user errors, the function takes care of showing an error message to the user.

While the user is on the invite page, it script is polling for new invtites. Otherwise, if the user is on the game page, it is polling for updates to the game.

For the game itself, both the frontend and backend ensure neither user makes illegal moves to ensure security.

When a user logs out, they are presented with the login screen again where they must choose a new username to login again.



BACKEND

The backend is split up into several strut actions; RegisterUser, Refresh, Invite, and Play. Each one uses one of four corresponding JSP files which generate JSON data (application/json MIME type). The JSON data is used by the frontend for parsing on the AJAX front (substituting JSON for XML, of course).

RegisterUser takes a username and registers the user in the system so that others can see him. It does basic validation like the fact that the user doesn't already exist and that the username is not empty.

Refresh is called after registering, and is constantly polled for as long as the user is on the web page. It takes care of removing users that are inactive for 4 minutes and also notifies the user if they have a match invite. Because it does two jobs, we send the keep-alive message to refresh more often than two minutes. However, it still works as expected. We just want to make sure the invite requests are received in a timely fashion.

Invite is called by a user requesting an invite or responding to an invite.

Finally, Play makes use of a completely different class called GameController (as opposed to UserControlled for the previous ones). Play allows the user to see the game state and to make moves. It does processing for if the game is at a timeout (inactive) and if someone has won. Games are NOT cleared upon completion because the user may want to stay in the game to see the result.

We also implemented (both on frontend and back) the option to quit a game prematurely, which will be effectively the same as a user timing out.