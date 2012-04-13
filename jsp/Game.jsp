<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="application/json; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="tictactoe.GameController" %>

{
	"gameID" : <s:property value="gameID" />,
	"boardState" : [<s:property value="boardState" />],
	"gameOutcome" : "<s:property value="gameOutcome" />",
	"yourTurn" : <s:property value="yourTurn" />,
	"hasErrors" : <s:property value="hasActionErrors()" />,
	"errorMessages" : [
		<s:iterator value="actionErrors">
		"<s:property escape="false"/>",
		</s:iterator>
		""
	]
}
